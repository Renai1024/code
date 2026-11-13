package com.miracle.extra.utils;

import com.miracle.extra.domain.TycoScan;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/** 新增：纯规则层兼容空格、NBSP和扫码枪GS/RS/EOT分隔符，不猜测PN含义。 */
public final class TycoScanRules {
    private TycoScanRules() {}
    public static TycoScan.Box parseBox(String raw) {
        if (raw == null || raw.length() > 2048) throw new IllegalArgumentException("箱码为空或超过2048字符");
        String box = raw.trim();
        Integer quantity = null;
        for (String token : box.split("[\\s\\p{Z}\\x1d\\x1e\\x1f\\x04]+")) {
            if (!token.startsWith("QT")) continue;
            if (quantity != null || !token.matches("QT[1-9][0-9]{0,3}"))
                throw new IllegalArgumentException("箱码必须包含且只包含一个有效QT数量");
            quantity = Integer.valueOf(token.substring(2));
        }
        if (quantity == null || quantity > 5000) throw new IllegalArgumentException("未识别QT数量，数量须为1至5000");
        return new TycoScan.Box(box, quantity);
    }
    /** 新增：不擅自改变产品码大小写；只移除扫描结束的首尾空白，拒绝内部空白/控制符。 */
    public static String product(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.isEmpty() || value.length() > 256 || value.matches("(?s).*[\\s\\p{Z}\\p{Cntrl}].*"))
            throw new IllegalArgumentException("产品码不能为空、含空白或超过256字符");
        return value;
    }
    public static List<String> products(TycoScan.Submit request) {
        int required = parseBox(request.boxCode()).quantity();
        if (request.productCodes() == null || request.productCodes().size() != required)
            throw new IllegalArgumentException("产品码数量必须与箱码QT数量完全一致");
        List<String> result = request.productCodes().stream().map(TycoScanRules::product).toList();
        if (new HashSet<>(result).size() != result.size()) throw new IllegalArgumentException("本箱存在重复产品码");
        return result;
    }
    public static String hash(String box, List<String> products) {
        try {
            // 新增：长度前缀消除分隔字符歧义，顺序属于提交内容，重复请求须内容完全一致。
            StringBuilder content = new StringBuilder(box.length() + ":" + box);
            for (String p : products) content.append(p.length()).append(':').append(p);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(content.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
}
