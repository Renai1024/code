package com.miracle.extra.service.impl;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.common.utils.SecurityUtils;
import com.miracle.extra.domain.BarcodeBindRecord;
import com.miracle.extra.domain.enums.BarcodeType;
import com.miracle.extra.mapper.BarcodeBindMapper;
import com.miracle.extra.service.IBarcodeBindService;
import com.miracle.extra.service.IProcessStationService;
import com.miracle.extra.validator.BarcodeValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class BarcodeBindServiceImpl implements IBarcodeBindService {

    @Autowired
    private BarcodeValidatorFactory validatorFactory;

    @Autowired
    private BarcodeBindMapper barcodeBindMapper;

    @Autowired
    private IProcessStationService processStationService;

    @Override
    public void validateBarcode(BarcodeType barcodeType, String barcode, String productCode, String deviceNo) {
        if (BarcodeType.PRODUCT_QR_CODE == barcodeType) {
            processStationService.verifyStation(barcode, deviceNo, productCode);
        } else {
            validatorFactory.validate(barcodeType, barcode, productCode);
        }
    }

    @Override
    public void validateDuplicate(BarcodeType barcodeType, String barcode) {
        List<BarcodeBindRecord> records = selectByBarcode(barcodeType, barcode);
        if (records != null && !records.isEmpty()) {
            throw new IllegalArgumentException("条码已被绑定: " + barcode);
        }
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE6)
    @Transactional(rollbackFor = Exception.class)
    public BarcodeBindRecord bind(BarcodeBindRecord.BindRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        
        if (request.getProductCode() == null || request.getProductCode().trim().isEmpty()) {
            throw new IllegalArgumentException("产品料号不能为空");
        }
        
        if (request.getDeviceNo() == null || request.getDeviceNo().trim().isEmpty()) {
            throw new IllegalArgumentException("设备编号不能为空");
        }
        
        if (request.getProductQrCode() == null || request.getProductQrCode().trim().isEmpty()) {
            throw new IllegalArgumentException("产品二维码不能为空");
        }
        
        if (request.getExtensionCode() == null || request.getExtensionCode().trim().isEmpty()) {
            throw new IllegalArgumentException("扩展码不能为空");
        }

        validateBarcode(BarcodeType.PRODUCT_QR_CODE, request.getProductQrCode(), request.getProductCode(), request.getDeviceNo());
        validateBarcode(BarcodeType.EXTENSION_CODE, request.getExtensionCode(), null, null);

        validateDuplicate(BarcodeType.PRODUCT_QR_CODE, request.getProductQrCode());
        validateDuplicate(BarcodeType.EXTENSION_CODE, request.getExtensionCode());

        String bindUuid = UUID.randomUUID().toString().replace("-","");
        
        BarcodeBindRecord record = new BarcodeBindRecord();
        record.setBindUuid(bindUuid);
        record.setProductCode(request.getProductCode());
        record.setBarcodeType1(BarcodeType.PRODUCT_QR_CODE.getCode());
        record.setBarcodeValue1(request.getProductQrCode());
        record.setBarcodeType2(BarcodeType.EXTENSION_CODE.getCode());
        record.setBarcodeValue2(request.getExtensionCode());
        record.setDeviceNo(request.getDeviceNo());
        record.setCreateTime(new Date());
        record.setCreateBy(SecurityUtils.getUsername());
        record.setDeleted(false);

        barcodeBindMapper.insertBarcodeBindRecord(record);
        
        processStationService.processStation(request.getProductQrCode(), request.getDeviceNo(), request.getProductCode(), bindUuid);
        
        return record;
    }

    @Override
    public BarcodeBindRecord getByBindUuid(String bindUuid) {
        return barcodeBindMapper.selectByBindUuid(bindUuid);
    }

    @Override
    public List<BarcodeBindRecord> selectBindRecordList(BarcodeBindRecord record) {
        return barcodeBindMapper.selectBarcodeBindRecordList(record);
    }

    @Override
    public int deleteBindRecordById(Long id) {
        return barcodeBindMapper.deleteBarcodeBindRecordById(id);
    }

    @Override
    public List<BarcodeBindRecord> selectByBarcode(BarcodeType barcodeType, String barcode) {
        return barcodeBindMapper.selectByBarcode(barcodeType.getCode(), barcode);
    }
}