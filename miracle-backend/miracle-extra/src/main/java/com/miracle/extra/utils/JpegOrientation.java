package com.miracle.extra.utils;

import java.awt.image.BufferedImage;

/** 新增：解析 JPEG APP1 中的 EXIF 方向，严格限制偏移边界，不引入上传元数据依赖。 */
public final class JpegOrientation {
    private JpegOrientation() {}

    public static int read(byte[] bytes) {
        if (bytes.length < 4 || u8(bytes, 0) != 255 || u8(bytes, 1) != 216) return 1;
        int offset = 2;
        while (offset + 4 <= bytes.length) {
            if (u8(bytes, offset) != 255) return 1;
            while (offset < bytes.length && u8(bytes, offset) == 255) offset++;
            if (offset >= bytes.length) return 1;
            int marker = u8(bytes, offset++);
            if (marker == 218 || marker == 217) return 1;
            if (marker == 1 || marker >= 208 && marker <= 215) continue;
            if (offset + 2 > bytes.length) return 1;
            int length = u8(bytes, offset) * 256 + u8(bytes, offset + 1);
            if (length < 2 || length > bytes.length - offset) return 1;
            int data = offset + 2;
            int end = offset + length;
            if (marker == 225 && end - data >= 14 && bytes[data] == 'E' && bytes[data+1] == 'x'
                    && bytes[data+2] == 'i' && bytes[data+3] == 'f' && bytes[data+4] == 0 && bytes[data+5] == 0) {
                int value = tiff(bytes, data + 6, end);
                if (value >= 1 && value <= 8) return value;
            }
            offset = end;
        }
        return 1;
    }

    /** TIFF IFD0 的 orientation 必须为 SHORT 类型单值，损坏 EXIF 回退方向 1。 */
    private static int tiff(byte[] bytes, int start, int end) {
        boolean little = bytes[start] == 'I' && bytes[start+1] == 'I';
        boolean big = bytes[start] == 'M' && bytes[start+1] == 'M';
        if ((!little && !big) || shortAt(bytes, start+2, little) != 42) return 1;
        long relative = intAt(bytes, start+4, little);
        if (relative < 8 || relative > end - start - 2) return 1;
        int ifd = start + (int) relative;
        int count = shortAt(bytes, ifd, little);
        if ((long) count * 12 > end - ifd - 2) return 1;
        for (int i = 0; i < count; i++) {
            int entry = ifd + 2 + i * 12;
            if (shortAt(bytes, entry, little) == 274 && shortAt(bytes, entry+2, little) == 3
                    && intAt(bytes, entry+4, little) == 1) return shortAt(bytes, entry+8, little);
        }
        return 1;
    }
    private static int u8(byte[] b, int i) { return b[i] & 255; }
    private static int shortAt(byte[] b, int i, boolean little) {
        return little ? u8(b,i) | u8(b,i+1)<<8 : u8(b,i)<<8 | u8(b,i+1);
    }
    private static long intAt(byte[] b, int i, boolean little) {
        long low = shortAt(b, little ? i : i+2, little);
        long high = shortAt(b, little ? i+2 : i, little);
        return low | high<<16;
    }

    /** 新增：一次像素映射覆盖 EXIF 的旋转和镜像八种方向，校正后再生成两份压缩图。 */
    public static BufferedImage correct(BufferedImage source, int orientation) {
        if (orientation <= 1 || orientation > 8) return source;
        int width = source.getWidth(), height = source.getHeight();
        boolean swap = orientation >= 5;
        BufferedImage target = new BufferedImage(swap ? height : width, swap ? width : height,
                source.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
            int tx, ty;
            switch (orientation) {
                case 2 -> { tx = width-1-x; ty = y; }
                case 3 -> { tx = width-1-x; ty = height-1-y; }
                case 4 -> { tx = x; ty = height-1-y; }
                case 5 -> { tx = y; ty = x; }
                case 6 -> { tx = height-1-y; ty = x; }
                case 7 -> { tx = height-1-y; ty = width-1-x; }
                case 8 -> { tx = y; ty = width-1-x; }
                default -> throw new IllegalArgumentException("无效 EXIF 方向");
            }
            target.setRGB(tx, ty, source.getRGB(x, y));
        }
        return target;
    }
}
