package com.miracle.extra.utils;

import com.miracle.extra.domain.TycoPacking.Label;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 新增：独立解析内外箱格式；不猜测日期码含义，不改变客户料号和流水标识大小写。 */
public final class TycoPackingRules {
    // 历史：private static final Pattern INNER = Pattern.compile("^(.+)-([0-9]+)-QTY:([1-9][0-9]{0,3})$");
    // 数量允许前导零，有效位仍最多四位且不能全为零。
    private static final String QUANTITY_DIGITS = "0*[1-9][0-9]{0,3}";
    // 防呆码由五个英文字母组成，保留原始大小写。旧标签可以省略此段。
    private static final Pattern INNER = Pattern.compile("^(.+?)(?:-([A-Za-z]{5}))?-([0-9]+)-QTY:(" + QUANTITY_DIGITS + ")$");
    private TycoPackingRules() { }
    public static String kind(String kind) {
        if (!"INNER".equals(kind) && !"OUTER".equals(kind)) throw new IllegalArgumentException("装箱类型无效");
        return kind;
    }
    private static int quantity(String text) {
        if (!text.matches(QUANTITY_DIGITS)) throw new IllegalArgumentException("装箱数量无效");
        // 内箱 QTY 和外箱 QT 共用十进制转换，01 和 1 均得到整数 1。
        int value = Integer.parseInt(text, 10);
        if (value > 5000) throw new IllegalArgumentException("装箱数量须为1至5000");
        return value;
    }
    public static Label parse(String kind, String raw) {
        return parse(kind, raw, null);
    }
    /** 新建内箱任务必须扫描带防呆码的标签，读取旧标签时仍支持原格式。 */
    public static Label parseForStart(String kind, String raw) {
        Label label = parse(kind, raw);
        if ("INNER".equals(kind) && label.getSnPrefix() == null)
            throw new IllegalArgumentException("内箱码格式不正确，应为客户料号-防呆码(五个英文字母)-流水标识-QTY:数量");
        return label;
    }
    /** 根据已知料号识别旧标签中恰好以连字符和五个字母结尾的料号。 */
    public static Label parseInnerForPart(String raw, String part) {
        return parse("INNER", raw, part);
    }
    /** 已持久化的原始箱码就是防呆码的来源，料号匹配可避免误读旧标签。 */
    public static String storedSnPrefix(Label label) {
        if (label.getRawCode() == null) return null;
        Matcher match = INNER.matcher(label.getRawCode().replace('：', ':'));
        return match.matches() && match.group(1).equals(label.getPart()) ? match.group(2) : null;
    }
    private static Label parse(String kind, String raw, String expectedPart) {
        kind(kind);
        if (raw == null || raw.length() > 2048 || raw.trim().isEmpty()) throw new IllegalArgumentException("箱码为空或过长");
        Label label = new Label(); label.setRawCode(raw.trim());
        if ("INNER".equals(kind)) {
            Matcher match = INNER.matcher(raw.trim().replace('：', ':'));
            if (!match.matches()) throw new IllegalArgumentException("内箱码格式不正确，请核对客户料号、流水标识和 QTY 数量");
            String part = match.group(1), snPrefix = match.group(2), serial = match.group(3);
            if (snPrefix != null && (part + "-" + snPrefix).equals(expectedPart)) {
                part = expectedPart;
                snPrefix = null;
            }
            if (part.length() > 128 || serial.length() > 128 || part.matches("(?s).*[\\s\\p{Z}\\p{Cntrl}].*"))
                throw new IllegalArgumentException("内箱客户料号或标识无效");
            // 内箱占用身份沿用客户料号和流水，防呆码改变时也不能新建重复任务。
            label.setPart(part); label.setIdentity(part + "-" + serial); label.setSnPrefix(snPrefix);
            label.setQuantity(quantity(match.group(4))); return label;
        }
        // 新增：仅规范化协议头的全角大于号；不能按连字符拆分客户料号。
        String[] tokens = raw.trim().replace('＞','>').split("[\\s\\p{Z}\\x1d\\x1e\\x1f\\x04]+");
        Map<String,String> fields = new LinkedHashMap<>();
        boolean header = tokens.length > 0 && ("[)>06".equals(tokens[0]) ||
                ("[)>".equals(tokens[0]) && tokens.length > 1 && "06".equals(tokens[1])));
        if (!header) throw new IllegalArgumentException("外箱码无效");
        for (String token : tokens) {
            if (token.length() < 2) continue;
            String prefix = token.substring(0,2);
            if (!java.util.Set.of("LT","PN","RV","QT","PO","BT","BX","DC").contains(prefix)) continue;
            String value = token.substring(2);
            if (value.isEmpty() || value.length() > 128 || fields.putIfAbsent(prefix,value) != null)
                throw new IllegalArgumentException("外箱字段缺值、过长或重复：" + prefix);
        }
        if (fields.size() != 8) throw new IllegalArgumentException("外箱码须包含LT、PN、RV、QT、PO、BT、BX、DC");
        label.setIdentity(fields.get("LT")); label.setTraceNo(fields.get("LT")); label.setPart(fields.get("PN"));
        label.setQuantity(quantity(fields.get("QT"))); label.setRevision(fields.get("RV"));
        label.setSalesOrder(fields.get("PO")); label.setProductionBatch(fields.get("BT"));
        label.setBoxSequence(fields.get("BX")); label.setDateCode(fields.get("DC")); return label;
    }
}
