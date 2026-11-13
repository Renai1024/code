package com.miracle.extra.service.impl;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.ProductRoutingStep;
import com.miracle.extra.domain.ProductSN;
import com.miracle.extra.domain.StationRecord;
import com.miracle.extra.mapper.ProcessStationMapper;
import com.miracle.extra.service.IProcessStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProcessStationServiceImpl implements IProcessStationService {

    //通过
    private static final int RESULT_PASS = 0;
    // NG / 测试不通过
    private static final short RESULT_NG = 1;

    // 业务异常
    private static final short RESULT_BUSINESS_ERROR = 2;
    //特批
    private static final int RESULT_SPECIAL_APPROVAL = 3;

    @Autowired
    private ProcessStationMapper processStationMapper;

    @Override
    public void verifyStation(String barcode, String equipmentNo, String productCode) {
        // 基础资料校验
        Integer equipmentId = processStationMapper.selectEquipmentIdByCode(equipmentNo);
        if (equipmentId == null || equipmentId == 0) {
            throw new IllegalArgumentException("设备编号 " + equipmentNo + " 不存在或未启用");
        }

        Integer defaultProductId = processStationMapper.selectProductIdByCode(productCode);
        if (defaultProductId == null || defaultProductId == 0) {
            throw new IllegalArgumentException("产品料号 " + productCode + " 不存在或未启用");
        }

        Integer defaultRoutingId = processStationMapper.selectProductRoutingIdByProductId(defaultProductId);
        if (defaultRoutingId == null || defaultRoutingId == 0) {
            throw new IllegalArgumentException("产品 " + productCode + " 无启用的工序链路配置");
        }

        // 条码对应的产品序列号信息
        List<ProductSN> snList = processStationMapper.selectProductSNsByBarcode(barcode);
        if (snList == null) {
            snList = Collections.emptyList();
        }

        ProductSN snInfo = snList.isEmpty() ? null : snList.get(0);

        int currentProductId;
        int currentRoutingId;

        if (snInfo != null) {
            if (!Objects.equals(snInfo.getProductId(), defaultProductId)) {
                throw new IllegalArgumentException("该条码所属产品与当前设备产品型号不匹配");
            }

            currentProductId = snInfo.getProductId();
            currentRoutingId = snInfo.getProductRoutingId() != null
                    ? snInfo.getProductRoutingId()
                    : defaultRoutingId;

            Integer status = snInfo.getStatus();
            if (status != null && (status == 1 || status == 2 || status == 3)) {
                String statusStr = getStatusStr(status);
                throw new IllegalArgumentException("产品状态:" + statusStr + "，禁止再次过站");
            }
        } else {
            currentProductId = defaultProductId;
            currentRoutingId = defaultRoutingId;
        }

        // 获取当前设备可执行的工序
        List<ProductRoutingStep> deviceSteps =
                processStationMapper.selectDeviceSteps(currentRoutingId, equipmentId);

        if (deviceSteps == null || deviceSteps.isEmpty()) {
            throw new IllegalArgumentException("当前产品在该设备上无可用工序配置");
        }

        // 获取全量工序
        List<ProductRoutingStep> allSteps =
                processStationMapper.selectAllSteps(currentRoutingId);

        if (allSteps == null) {
            allSteps = Collections.emptyList();
        }

        // 获取过站历史，并按工序分组
        List<StationRecord> historyRecords =
                processStationMapper.selectStationRecordsByBarcode(barcode);

        if (historyRecords == null) {
            historyRecords = Collections.emptyList();
        }

        Map<Integer, List<StationRecord>> recordsByProcess = new HashMap<>();

        for (StationRecord record : historyRecords) {
            if (record == null || record.getProcessId() == null) {
                continue;
            }

            Integer processId = record.getProcessId();
            List<StationRecord> list = recordsByProcess.get(processId);
            if (list == null) {
                list = new ArrayList<>();
                recordsByProcess.put(processId, list);
            }

            list.add(record);
        }

        // 计算已通过工序、已过站工序
        Set<Integer> passedProcessIds = new HashSet<>();
        Set<Integer> recordedProcessIds = new HashSet<>();

        for (ProductRoutingStep step : allSteps) {
            if (step == null || step.getProcessId() == null) {
                continue;
            }

            Integer processId = step.getProcessId();

            List<StationRecord> processRecords = recordsByProcess.get(processId);
            if (processRecords == null) {
                processRecords = Collections.emptyList();
            }

            // 判断是否存在未作废记录，用于 recordedProcessIds
            boolean hasValidRecord = false;

            for (StationRecord record : processRecords) {
                if (record == null) {
                    continue;
                }

                Boolean reworkVoid = record.getIsReworkVoid();
                if (reworkVoid != null && reworkVoid) {
                    continue;
                }

                hasValidRecord = true;
                break;
            }

            if (hasValidRecord) {
                recordedProcessIds.add(processId);
            }

            // 判断该工序是否通过
            if (isProcessPassed(processRecords, step.getRequiredCount())) {
                passedProcessIds.add(processId);
            }
        }

        // 推断当前目标工序
        ProductRoutingStep targetStep = null;

        for (ProductRoutingStep step : deviceSteps) {
            if (step == null || step.getProcessId() == null) {
                continue;
            }

            if (!passedProcessIds.contains(step.getProcessId())) {
                targetStep = step;
                break;
            }
        }

        if (targetStep == null) {
            targetStep = deviceSteps.get(deviceSteps.size() - 1);
        }

        if (targetStep == null || targetStep.getProcessId() == null) {
            throw new IllegalArgumentException("工序配置异常：无法确定当前目标工序");
        }

        Integer targetProcessId = targetStep.getProcessId();

        // 条码规则校验
        if (Boolean.TRUE.equals(targetStep.getBarcodeVerify())) {
            String barcodeRule = processStationMapper.selectBarcodeRuleByProductId(currentProductId);

            if (barcodeRule != null && !barcodeRule.isEmpty()) {
                String ruleError = isValidBarcode(barcode, barcodeRule);
                if (ruleError != null) {
                    throw new IllegalArgumentException("条码校验拦截：" + ruleError);
                }
            }
        }

        // 顺序校验
        if (Boolean.TRUE.equals(targetStep.getSequenceVerify())) {
            Integer targetSequence = targetStep.getSequence();

            if (targetSequence != null) {
                boolean hasMissingPreProcess = false;

                for (ProductRoutingStep step : allSteps) {
                    if (step == null
                            || step.getSequence() == null
                            || step.getProcessId() == null) {
                        continue;
                    }

                    if (step.getSequence() < targetSequence
                            && !passedProcessIds.contains(step.getProcessId())) {
                        hasMissingPreProcess = true;
                        break;
                    }
                }

                if (hasMissingPreProcess) {
                    throw new IllegalArgumentException("前置工序未完成或不合格，禁止进入本工序");
                }
            }
        }

        // 重复过站校验
        if (Boolean.TRUE.equals(targetStep.getDuplicateVerify())) {

            Integer requiredCountObj = targetStep.getRequiredCount();
            int requiredCount = (requiredCountObj == null || requiredCountObj <= 0)
                    ? 1
                    : requiredCountObj;

            if (requiredCount > 1) {

                // 多次操作工序：如果已经达到 RequiredCount，则禁止继续过站
                if (passedProcessIds.contains(targetProcessId)) {
                    throw new IllegalArgumentException(
                            "重码拦截：" + barcode + " 该工序已完成" + requiredCount + "次操作，禁止继续过站"
                    );
                }

                List<StationRecord> targetRecords = recordsByProcess.get(targetProcessId);
                if (targetRecords == null) {
                    targetRecords = Collections.emptyList();
                }

                long validCount = 0;

                for (StationRecord record : targetRecords) {
                    if (record == null) {
                        continue;
                    }

                    Boolean reworkVoid = record.getIsReworkVoid();
                    if (reworkVoid != null && reworkVoid) {
                        continue;
                    }

                    validCount++;
                }

                // 有效记录数超过配置次数，视为数据异常
                if (validCount > requiredCount) {
                    throw new IllegalArgumentException(
                            "业务异常：" + barcode + " 该工序有效记录数(" + validCount
                                    + ")超过所需次数(" + requiredCount + ")"
                    );
                }

            } else {

                // 单次操作工序：只要存在未作废过站记录，则禁止重复过站
                if (recordedProcessIds.contains(targetProcessId)) {
                    throw new IllegalArgumentException(
                            "重码拦截：" + barcode + " 已存在过站记录，禁止重复过站"
                    );
                }
            }
        }
    }

    @Override
    public void processStation(String barcode, String equipmentNo, String productCode, String dataTraceId) {
        processStation(barcode, equipmentNo, productCode, true, null, dataTraceId);
    }

    /**
     * 过站处理
     *
     * @param barcode       条码
     * @param equipmentNo   设备编号
     * @param productCode   产品料号
     * @param isPass        本次测试是否通过
     * @param defectReason  NG原因
     * @param dataTraceId   数据追踪ID
     */
    @Override
    @DataSource(value = DataSourceType.SLAVE6)
    @Transactional(rollbackFor = Exception.class)
    public void processStation(String barcode, String equipmentNo, String productCode, boolean isPass, String defectReason, String dataTraceId) {
        Date now = new Date();

        if (dataTraceId == null || dataTraceId.isEmpty()) {
            dataTraceId = UUID.randomUUID().toString().replace("-", "");
        }

        // 基础资料校验

        Integer equipmentId = processStationMapper.selectEquipmentIdByCode(equipmentNo);
        if (equipmentId == null || equipmentId == 0) {
            throw new IllegalArgumentException("当前设备内部编号" + equipmentNo + "不存在或未启用");
        }

        Integer defaultProductId = processStationMapper.selectProductIdByCode(productCode);
        if (defaultProductId == null || defaultProductId == 0) {
            throw new IllegalArgumentException("当前产品料号" + productCode + "不存在或未启用");
        }

        Integer defaultRoutingId = processStationMapper.selectProductRoutingIdByProductId(defaultProductId);
        if (defaultRoutingId == null || defaultRoutingId == 0) {
            throw new IllegalArgumentException("当前产品" + productCode + "无启用的工序链路配置");
        }

        // 获取产品SN信息

        List<ProductSN> snList = processStationMapper.selectProductSNsByBarcode(barcode);
        if (snList == null) {
            snList = Collections.emptyList();
        }

        ProductSN snInfo = snList.isEmpty() ? null : snList.get(0);

        int currentProductId;
        int currentRoutingId;
        long productSnId = 0L;

        if (snInfo != null) {
            currentProductId = snInfo.getProductId();
            currentRoutingId = snInfo.getProductRoutingId() != null
                    ? snInfo.getProductRoutingId()
                    : defaultRoutingId;

            Long snId = Long.valueOf(snInfo.getId());
            productSnId = snId != null ? snId : 0L;
        } else {
            currentProductId = defaultProductId;
            currentRoutingId = defaultRoutingId;
        }

        // 获取设备可执行工序

        List<ProductRoutingStep> deviceSteps =
                processStationMapper.selectDeviceSteps(currentRoutingId, equipmentId);

        if (deviceSteps == null || deviceSteps.isEmpty()) {
            throw new IllegalArgumentException("当前产品在该设备上无可用工序配置");
        }

        // 获取全量工序

        List<ProductRoutingStep> allSteps =
                processStationMapper.selectAllSteps(currentRoutingId);

        if (allSteps == null) {
            allSteps = Collections.emptyList();
        }

        int maxSequence = 0;
        for (ProductRoutingStep step : allSteps) {
            if (step == null || step.getSequence() == null) {
                continue;
            }
            if (step.getSequence() > maxSequence) {
                maxSequence = step.getSequence();
            }
        }

        // 获取过站历史，并按工序分组

        List<StationRecord> historyRecords =
                processStationMapper.selectStationRecordsByBarcode(barcode);

        if (historyRecords == null) {
            historyRecords = Collections.emptyList();
        }

        Map<Integer, List<StationRecord>> recordsByProcess = new HashMap<>();

        for (StationRecord record : historyRecords) {
            if (record == null || record.getProcessId() == null) {
                continue;
            }

            Integer processId = record.getProcessId();
            List<StationRecord> list = recordsByProcess.get(processId);
            if (list == null) {
                list = new ArrayList<>();
                recordsByProcess.put(processId, list);
            }

            list.add(record);
        }

        // 基于 RequiredCount 和 IsReworkVoid 计算历史通过状态

        Set<Integer> passedProcessIds = new HashSet<>();
        Set<Integer> recordedProcessIds = new HashSet<>();

        for (ProductRoutingStep step : allSteps) {
            if (step == null || step.getProcessId() == null) {
                continue;
            }

            Integer processId = step.getProcessId();

            List<StationRecord> processRecords = recordsByProcess.get(processId);
            if (processRecords == null) {
                processRecords = Collections.emptyList();
            }

            // 判断是否存在未作废记录
            boolean hasValidRecord = false;

            for (StationRecord record : processRecords) {
                if (record == null) {
                    continue;
                }

                Boolean reworkVoid = record.getIsReworkVoid();
                if (reworkVoid != null && reworkVoid) {
                    continue;
                }

                hasValidRecord = true;
                break;
            }

            if (hasValidRecord) {
                recordedProcessIds.add(processId);

                if (isProcessPassed(processRecords, step.getRequiredCount())) {
                    passedProcessIds.add(processId);
                }
            }
        }

        // 推断当前目标工序

        ProductRoutingStep targetStep = null;

        for (ProductRoutingStep step : deviceSteps) {
            if (step == null || step.getProcessId() == null) {
                continue;
            }

            if (!passedProcessIds.contains(step.getProcessId())) {
                targetStep = step;
                break;
            }
        }

        if (targetStep == null) {
            targetStep = deviceSteps.get(deviceSteps.size() - 1);
        }

        if (targetStep == null || targetStep.getProcessId() == null) {
            throw new IllegalArgumentException("工序配置异常：无法确定当前目标工序");
        }

        Integer targetProcessId = targetStep.getProcessId();

        // 业务异常检查

        boolean isBusinessError = false;
        String businessErrorDetail = "";

        // 8.1 条码校验
        if (Boolean.TRUE.equals(targetStep.getBarcodeVerify())) {
            String barcodeRule = processStationMapper.selectBarcodeRuleByProductId(currentProductId);

            if (barcodeRule != null && !barcodeRule.isEmpty()) {
                String ruleError = isValidBarcode(barcode, barcodeRule);
                if (ruleError != null) {
                    isBusinessError = true;
                    businessErrorDetail = ruleError;
                }
            }
        }

        // 顺序校验
        if (!isBusinessError && Boolean.TRUE.equals(targetStep.getSequenceVerify())) {
            Integer targetSequenceObj = targetStep.getSequence();

            if (targetSequenceObj != null) {
                int targetSequence = targetSequenceObj;
                boolean hasMissingPreProcess = false;

                for (ProductRoutingStep step : allSteps) {
                    if (step == null
                            || step.getSequence() == null
                            || step.getProcessId() == null) {
                        continue;
                    }

                    if (step.getSequence() < targetSequence
                            && !passedProcessIds.contains(step.getProcessId())) {
                        hasMissingPreProcess = true;
                        break;
                    }
                }

                if (hasMissingPreProcess) {
                    isBusinessError = true;
                    businessErrorDetail = "前置工序未完成或不合格(跳站)";
                }
            }
        }

        // 重复校验
        if (!isBusinessError && Boolean.TRUE.equals(targetStep.getDuplicateVerify())) {

            Integer requiredCountObj = targetStep.getRequiredCount();
            int requiredCount = (requiredCountObj == null || requiredCountObj <= 0)
                    ? 1
                    : requiredCountObj;

            if (requiredCount > 1) {
                // 多次操作工序：已经达到 RequiredCount，则禁止继续过站
                if (passedProcessIds.contains(targetProcessId)) {
                    isBusinessError = true;
                    businessErrorDetail = "该工序已完成" + requiredCount + "次操作，禁止继续过站";
                } else {

                    List<StationRecord> targetRecords = recordsByProcess.get(targetProcessId);
                    if (targetRecords == null) {
                        targetRecords = Collections.emptyList();
                    }

                    long validCount = 0;

                    for (StationRecord record : targetRecords) {
                        if (record == null) {
                            continue;
                        }

                        Boolean reworkVoid = record.getIsReworkVoid();
                        if (reworkVoid != null && reworkVoid) {
                            continue;
                        }

                        validCount++;
                    }

                    if (validCount > requiredCount) {
                        isBusinessError = true;
                        businessErrorDetail = "该工序有效记录数(" + validCount
                                + ")超过所需次数(" + requiredCount + ")";
                    }
                }

            } else {
                // 单次操作工序：存在未作废记录，则禁止重复过站
                if (recordedProcessIds.contains(targetProcessId)) {
                    isBusinessError = true;
                    businessErrorDetail = "该工序已存在过站记录(重码/非法复测)";
                }
            }
        }

        // 最终结果判定

        short finalResult;
        String resultDetail;

        if (!isPass) {
            // NG 优先级最高：即使存在业务异常，也记录为 NG
            finalResult = RESULT_NG;
            resultDetail = defectReason != null ? defectReason : "操作/测试NG";

            if (isBusinessError) {
                resultDetail += " [同时存在业务异常:" + businessErrorDetail + "]";
            }
        } else if (isBusinessError) {
            // 测试结果为 Pass，但业务异常
            finalResult = RESULT_BUSINESS_ERROR;
            resultDetail = businessErrorDetail;
        } else {
            // 测试结果为 Pass，且无业务异常
            finalResult = RESULT_PASS;
            resultDetail = null;
        }

        // 首站激活：如果没有产品SN，则新增
        if (productSnId == 0L) {
            processStationMapper.insertProductSN(
                    barcode,
                    currentProductId,
                    currentRoutingId,
                    0,
                    now,
                    "API"
            );

            List<ProductSN> newSnList = processStationMapper.selectProductSNsByBarcode(barcode);
            if (newSnList != null && !newSnList.isEmpty()) {
                Long newSnId = Long.valueOf(newSnList.get(0).getId());
                productSnId = newSnId != null ? newSnId : 0L;
            }
        }

        // 写入过站记录
        processStationMapper.insertStationRecord(
                barcode,
                currentProductId,
                currentRoutingId,
                targetProcessId,
                equipmentId,
                finalResult,
                resultDetail,
                dataTraceId,
                now
        );

        // 将本次记录加入历史，重新计算所有工序通过状态

        StationRecord newRecord = new StationRecord();
        newRecord.setProcessId(targetProcessId);
        newRecord.setResult((int) finalResult);
        newRecord.setIsReworkVoid(false);
        newRecord.setCreateTime(now);

        List<StationRecord> targetRecords = recordsByProcess.get(targetProcessId);
        if (targetRecords == null) {
            targetRecords = new ArrayList<>();
            recordsByProcess.put(targetProcessId, targetRecords);
        }
        targetRecords.add(newRecord);

        Set<Integer> allPassedIds = new HashSet<>();

        for (ProductRoutingStep step : allSteps) {
            if (step == null || step.getProcessId() == null) {
                continue;
            }

            List<StationRecord> stepRecords = recordsByProcess.get(step.getProcessId());
            if (stepRecords == null) {
                stepRecords = Collections.emptyList();
            }

            if (isProcessPassed(stepRecords, step.getRequiredCount())) {
                allPassedIds.add(step.getProcessId());
            }
        }

        // 计算产品SN新状态和下一步工序

        int newStatus = 0;
        String nextProcessIdsStr = null;

        if (finalResult == RESULT_PASS) {

            Integer targetSequenceObj = targetStep.getSequence();

            // 如果当前工序是最后一道工序，则判断是否所有同级工序都通过
            if (targetSequenceObj != null && targetSequenceObj == maxSequence) {
                boolean allCurrentLevelPassed = true;
                for (ProductRoutingStep step : allSteps) {
                    if (step == null
                            || step.getSequence() == null
                            || step.getProcessId() == null) {
                        continue;
                    }
                    if (step.getSequence() == maxSequence
                            && !allPassedIds.contains(step.getProcessId())) {
                        allCurrentLevelPassed = false;
                        break;
                    }
                }
                if (allCurrentLevelPassed) {
                    newStatus = 1;
                }
            }

            // 当前层级是否还有未通过并行工序
            List<ProductRoutingStep> pendingParallelSteps = new ArrayList<>();

            if (targetSequenceObj != null) {
                int targetSequence = targetSequenceObj;
                for (ProductRoutingStep step : allSteps) {
                    if (step == null
                            || step.getSequence() == null
                            || step.getProcessId() == null) {
                        continue;
                    }
                    if (step.getSequence() == targetSequence
                            && !allPassedIds.contains(step.getProcessId())) {
                        pendingParallelSteps.add(step);
                    }
                }
            }
            if (!pendingParallelSteps.isEmpty()) {
                nextProcessIdsStr = pendingParallelSteps.stream()
                        .filter(s -> s != null && s.getProcessId() != null)
                        .map(s -> String.valueOf(s.getProcessId()))
                        .collect(Collectors.joining(","));
            } else if (targetSequenceObj != null) {
                int targetSequence = targetSequenceObj;
                int nextSeq = Integer.MAX_VALUE;
                for (ProductRoutingStep step : allSteps) {
                    if (step == null || step.getSequence() == null) {
                        continue;
                    }
                    if (step.getSequence() > targetSequence && step.getSequence() < nextSeq) {
                        nextSeq = step.getSequence();
                    }
                }
                if (nextSeq != Integer.MAX_VALUE) {
                    final int finalNextSeq = nextSeq;
                    List<ProductRoutingStep> nextLevelSteps = new ArrayList<>();
                    for (ProductRoutingStep step : allSteps) {
                        if (step == null
                                || step.getSequence() == null
                                || step.getProcessId() == null) {
                            continue;
                        }
                        if (step.getSequence() == finalNextSeq) {
                            nextLevelSteps.add(step);
                        }
                    }
                    if (!nextLevelSteps.isEmpty()) {
                        nextProcessIdsStr = nextLevelSteps.stream()
                                .filter(s -> s != null && s.getProcessId() != null)
                                .map(s -> String.valueOf(s.getProcessId()))
                                .collect(Collectors.joining(","));
                    }
                }
            }

        } else if (finalResult == RESULT_NG) {
            if (Boolean.TRUE.equals(targetStep.getDuplicateVerify())) {
                // NG 且不允许复测
                newStatus = 2;
                nextProcessIdsStr = null;
            } else {
                // NG 但允许复测
                newStatus = 0;
                List<ProductRoutingStep> pendingSteps = new ArrayList<>();
                Integer targetSequenceObj = targetStep.getSequence();
                if (targetSequenceObj != null) {
                    int targetSequence = targetSequenceObj;
                    for (ProductRoutingStep step : allSteps) {
                        if (step == null
                                || step.getSequence() == null
                                || step.getProcessId() == null) {
                            continue;
                        }
                        if (step.getSequence() == targetSequence
                                && !allPassedIds.contains(step.getProcessId())) {
                            pendingSteps.add(step);
                        }
                    }
                }

                if (!pendingSteps.isEmpty()) {
                    nextProcessIdsStr = pendingSteps.stream()
                            .filter(s -> s != null && s.getProcessId() != null)
                            .map(s -> String.valueOf(s.getProcessId()))
                            .collect(Collectors.joining(","));
                } else {
                    nextProcessIdsStr = String.valueOf(targetProcessId);
                }
            }

        } else if (finalResult == RESULT_BUSINESS_ERROR) {
            // 业务异常：标记异常
            newStatus = 2;
            nextProcessIdsStr = null;
        }

        // 更新产品SN状态
        processStationMapper.updateProductSN(
                productSnId,
                newStatus,
                targetProcessId,
                nextProcessIdsStr
        );
    }

    /**
     * 判断工序是否通过
     *
     * 规则：
     * 1. 存在未作废的特批记录 → 直接通过
     * 2. RequiredCount <= 1 → 最后一条有效记录为 Pass 即通过
     * 3. RequiredCount > 1 → 有效 Pass 记录数 >= RequiredCount 即通过
     */
    private boolean isProcessPassed(List<StationRecord> records, Integer requiredCount) {

        if (records == null || records.isEmpty()) {
            return false;
        }

        List<StationRecord> validRecords = new ArrayList<>();
        boolean hasSpecialApproval = false;
        int passCount = 0;

        for (StationRecord record : records) {
            if (record == null) {
                continue;
            }

            Boolean reworkVoid = record.getIsReworkVoid();
            if (reworkVoid != null && reworkVoid) {
                continue;
            }

            validRecords.add(record);

            Integer result = record.getResult();
            if (result != null) {
                if (result == RESULT_SPECIAL_APPROVAL) {
                    hasSpecialApproval = true;
                }

                if (result == RESULT_PASS) {
                    passCount++;
                }
            }
        }

        if (validRecords.isEmpty()) {
            return false;
        }

        // 特批短路：存在任意未作废特批记录即通过
        if (hasSpecialApproval) {
            return true;
        }

        int required = requiredCount == null ? 1 : requiredCount;

        if (required <= 1) {
            // 单次操作工序：取最后一条有效记录判定
            StationRecord lastRecord = validRecords.stream()
                    .sorted(Comparator.comparing(
                            StationRecord::getCreateTime,
                            Comparator.nullsFirst(Comparator.naturalOrder())
                    ))
                    .reduce((a, b) -> b)
                    .orElse(null);

            return lastRecord != null
                    && lastRecord.getResult() != null
                    && lastRecord.getResult() == RESULT_PASS;
        } else {
            // 多次操作工序：Pass 计数判定
            return passCount >= required;
        }
    }

    private String getStatusStr(Integer status) {
        switch (status) {
            case 1:
                return "完成";
            case 2:
                return "异常";
            case 3:
                return "报废";
            default:
                return "未知";
        }
    }

    private String isValidBarcode(String barcode, String barcodeRule) {
        try {
            Pattern pattern = Pattern.compile(barcodeRule);
            if (!pattern.matcher(barcode).matches()) {
                return "条码格式不符合规则";
            }
            return null;
        } catch (Exception e) {
            return "条码规则解析错误: " + e.getMessage();
        }
    }
}