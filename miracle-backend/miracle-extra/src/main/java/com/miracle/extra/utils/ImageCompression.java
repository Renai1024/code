package com.miracle.extra.utils;

import com.miracle.extra.domain.enums.BucketType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;

@Component
public class ImageCompression {
    private static final long MAX_INPUT_BYTES = 10L * 1024 * 1024;
    // 历史像素上限保留：private static final long MAX_PIXELS = 12_000_000L;
    // 新增：按当前需求取消输入总像素上限，文件体积及压缩输出尺寸限制仍生效。

    public record ProcessedImage(byte[] bytes, String extension, String contentType) {}

    public ProcessedImage compress(MultipartFile file, BucketType type) throws IOException {
        if (file == null || file.isEmpty() || file.getSize() > MAX_INPUT_BYTES) {
            throw new IllegalArgumentException("图片为空或超过 10 MB");
        }

        try (InputStream raw = file.getInputStream();
             ImageInputStream input = ImageIO.createImageInputStream(raw)) {
            if (input == null) throw new IllegalArgumentException("无法读取图片");
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IllegalArgumentException("只支持 JPEG/PNG 图片");

            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                boolean jpeg = format.equals("jpeg") || format.equals("jpg");
                boolean png = format.equals("png");
                if (!jpeg && !png) throw new IllegalArgumentException("只支持 JPEG/PNG 图片");

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                // 历史校验：if (width <= 0 || height <= 0 || (long) width * height > MAX_PIXELS)
                // 历史提示：throw new IllegalArgumentException("图片像素超出限制");
                // 新增：只拒绝无效宽高，不再按输入图片总像素拒绝上传。
                if (width <= 0 || height <= 0) {
                    throw new IllegalArgumentException("图片宽高无效");
                }

                BufferedImage original = reader.read(0);
                // 新增：JPEG 拍摄方向先校正，原图与缩略图共享正确方向。
                int orientation = jpeg ? JpegOrientation.read(file.getBytes()) : 1;
                original = JpegOrientation.correct(original, orientation);
                width = original.getWidth();
                height = original.getHeight();
                int maxSide = maxSide(type);
                double scale = Math.min(1.0, (double) maxSide / Math.max(width, height));
                int targetWidth = Math.max(1, (int) Math.round(width * scale));
                int targetHeight = Math.max(1, (int) Math.round(height * scale));
                int imageType = jpeg ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
                BufferedImage resized = new BufferedImage(targetWidth, targetHeight, imageType);
                Graphics2D graphics = resized.createGraphics();
                try {
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    graphics.drawImage(original, 0, 0, targetWidth, targetHeight, null);
                } finally {
                    graphics.dispose();
                }

                // 新增：只决定一次输出格式并编码一次，不再产生候选文件或比较大小。
                return encodeStorageImage(resized, jpeg, type);
            } finally {
                reader.dispose();
            }
        }
    }

    /** 新增：主图单次有损处理；JPEG/不透明 PNG 转 JPEG，透明 PNG 量化后编码一次。 */
    private ProcessedImage encodeStorageImage(BufferedImage image, boolean jpeg, BucketType type) throws IOException {
        boolean transparent = false;
        if (!jpeg && image.getColorModel().hasAlpha()) {
            for (int y = 0; y < image.getHeight() && !transparent; y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if ((image.getRGB(x, y) >>> 24) != 255) { transparent = true; break; }
                }
            }
        }
        if (!transparent) {
            // 新增：无透明像素时固定使用 JPEG，不再先编码 PNG 或做体积比较。
            if (image.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage rgb = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = rgb.createGraphics();
                try { graphics.drawImage(image, 0, 0, null); } finally { graphics.dispose(); }
                image = rgb;
            }
            return encodeOnce(image, "jpeg", quality(type));
        }
        // 新增：直接量化缩放后的工作图，保留完整 alpha，避免额外创建候选图片副本。
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);
                int r = sixBit(pixel >>> 16 & 255), g = sixBit(pixel >>> 8 & 255), b = sixBit(pixel & 255);
                image.setRGB(x, y, (pixel & 0xff000000) | r << 16 | g << 8 | b);
            }
        }
        return encodeOnce(image, "png", 0);
    }

    /** 新增：每个颜色通道降为 64 级，误差最多 3/255，透明通道不变。 */
    private int sixBit(int value) { return (value >>> 2) * 255 / 63; }

    /** 新增：一次编码；PNG 使用默认编码参数，不额外尝试最高压缩等级。 */
    private ProcessedImage encodeOnce(BufferedImage image, String format, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
        if (!writers.hasNext()) throw new IOException("服务器不支持图片编码：" + format);
        ImageWriter writer = writers.next();
        boolean jpeg = format.equals("jpeg");
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ImageOutputStream output = ImageIO.createImageOutputStream(bytes)) {
            writer.setOutput(output);
            ImageWriteParam params = writer.getDefaultWriteParam();
            if (jpeg) {
                params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                params.setCompressionQuality(quality);
            }
            writer.write(null, new javax.imageio.IIOImage(image, null, null), params);
            output.flush();
            if (bytes.size() == 0) throw new IOException("图片编码结果为空");
            return new ProcessedImage(bytes.toByteArray(), jpeg ? "jpg" : "png", jpeg ? "image/jpeg" : "image/png");
        } finally { writer.dispose(); }
    }

    /** 新增：最长边 320px，不放大小图，透明 PNG 保留 alpha，JPEG 质量 0.72。 */
    public ProcessedImage thumbnail(ProcessedImage original) throws IOException {
        BufferedImage source = ImageIO.read(new java.io.ByteArrayInputStream(original.bytes()));
        if (source == null) throw new IOException("压缩后的图片无法解码");
        int max = Math.max(source.getWidth(), source.getHeight());
        double scale = Math.min(1.0, 320.0 / max);
        int width = Math.max(1, (int) Math.round(source.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(source.getHeight() * scale));
        boolean jpeg = "image/jpeg".equals(original.contentType());
        BufferedImage target = new BufferedImage(width, height,
                jpeg ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = target.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(source, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        String format = jpeg ? "jpeg" : "png";
        java.util.Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
        if (!writers.hasNext()) throw new IOException("服务器不支持图片编码：" + format);
        ImageWriter writer = writers.next();
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ImageOutputStream output = ImageIO.createImageOutputStream(bytes)) {
            writer.setOutput(output);
            ImageWriteParam params = writer.getDefaultWriteParam();
            if (jpeg) {
                params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                params.setCompressionQuality(0.72f);
            }
            writer.write(null, new javax.imageio.IIOImage(target, null, null), params);
            output.flush();
            return new ProcessedImage(bytes.toByteArray(),
                    original.extension(), original.contentType());
        } finally {
            writer.dispose();
        }
    }

    // 历史多候选编码、体积比较及原字节回退完整保留；现由单次编码替代。
    //                 String outputFormat = jpeg ? "jpeg" : "png";
    //                 Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(outputFormat);
    //                 if (!writers.hasNext()) throw new IOException("服务器不支持图片编码：" + outputFormat);
    //                 ImageWriter writer = writers.next();
    //                 try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    //                      ImageOutputStream output = ImageIO.createImageOutputStream(bytes)) {
    //                     writer.setOutput(output);
    //                     ImageWriteParam params = writer.getDefaultWriteParam();
    //                     if (jpeg) {
    //                         params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    //                         params.setCompressionQuality(quality(type));
    //                     }
    //                     writer.write(null, new javax.imageio.IIOImage(resized, null, null), params);
    //                     output.flush();
    //                     byte[] result = bytes.toByteArray();
    //                     if (result.length == 0) throw new IOException("图片编码结果为空");
    //                     // 新增：无需缩放的 PNG 重编码更大时保留原始字节，避免无意义体积增长。
    //                     // 历史体积保护：if (png && scale == 1.0 && result.length > file.getSize()) result = file.getBytes();
    //                     // 历史返回：return new ProcessedImage(result, jpeg ? "jpg" : "png", jpeg ? "image/jpeg" : "image/png");
    //                     // 新增：按实际字节数选择主图编码；尺寸上限与 EXIF 方向处理始终优先。
    //                     ProcessedImage best = new ProcessedImage(result, jpeg ? "jpg" : "png",
    //                             jpeg ? "image/jpeg" : "image/png");
    //                     if (png) best = smallerPng(resized, best, type);
    //                     // 新增：已优化小图不强行重编码变大；仅在无需缩放/方向校正时保留输入。
    //                     if (scale == 1.0 && orientation == 1 && best.bytes().length >= file.getSize()) {
    //                         return new ProcessedImage(file.getBytes(), jpeg ? "jpg" : "png",
    //                                 jpeg ? "image/jpeg" : "image/png");
    //                     }
    //                     return best;
    //                 } finally {
    //                     writer.dispose();
    //                 }
    // 
    //     /** 新增：按文件体积选择 PNG 主图方案；透明度保持原样，不额外缩小主图尺寸。 */
    //     private ProcessedImage smallerPng(BufferedImage source, ProcessedImage best, BucketType type) throws IOException {
    //         // 新增：先尝试无损优化，让文字/线条图片优先保留 PNG 编码的机会。
    //         ProcessedImage lossless = encodeCandidate(source, "png", 0);
    //         if (lossless.bytes().length < best.bytes().length) best = lossless;
    //         boolean transparent = false;
    //         for (int y = 0; y < source.getHeight() && !transparent; y++) {
    //             for (int x = 0; x < source.getWidth(); x++) {
    //                 if ((source.getRGB(x, y) >>> 24) != 255) { transparent = true; break; }
    //             }
    //         }
    //         if (!transparent) {
    //             // 新增：不透明照片允许转换 JPEG，扩展名/MIME 随实际编码返回，只有更小时采用。
    //             BufferedImage rgb = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
    //             Graphics2D graphics = rgb.createGraphics();
    //             try { graphics.drawImage(source, 0, 0, null); } finally { graphics.dispose(); }
    //             ProcessedImage candidate = encodeCandidate(rgb, "jpeg", quality(type));
    //             if (candidate.bytes().length < best.bytes().length) best = candidate;
    //         } else {
    //             // 新增：透明 PNG 使用 6 位 RGB 量化（每通道误差最多 3），完整保留 8 位 alpha。
    //             BufferedImage quantized = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
    //             for (int y = 0; y < source.getHeight(); y++) {
    //                 for (int x = 0; x < source.getWidth(); x++) {
    //                     int pixel = source.getRGB(x, y);
    //                     int r = sixBit(pixel >>> 16 & 255), g = sixBit(pixel >>> 8 & 255), b = sixBit(pixel & 255);
    //                     quantized.setRGB(x, y, (pixel & 0xff000000) | r << 16 | g << 8 | b);
    //                 }
    //             }
    //             ProcessedImage candidate = encodeCandidate(quantized, "png", 0);
    //             if (candidate.bytes().length < best.bytes().length) best = candidate;
    //         }
    //         return best;
    //     }
    // 
    //     /** 新增：将 256 级颜色映射到 64 级，端点仍为 0/255。 */
    //     private int sixBit(int value) { return (value >>> 2) * 255 / 63; }
    // 
    //     /** 新增：编码候选主图；PNG 使用最高无损压缩，JPEG 使用业务质量参数。 */
    //     private ProcessedImage encodeCandidate(BufferedImage image, String format, float quality) throws IOException {
    //         ImageWriter writer = ImageIO.getImageWritersByFormatName(format).next();
    //         try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    //              ImageOutputStream output = ImageIO.createImageOutputStream(bytes)) {
    //             writer.setOutput(output);
    //             ImageWriteParam params = writer.getDefaultWriteParam();
    //             params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    //             params.setCompressionQuality(quality);
    //             writer.write(null, new javax.imageio.IIOImage(image, null, null), params);
    //             output.flush();
    //             if (bytes.size() == 0) throw new IOException("图片编码结果为空");
    //             boolean jpeg = format.equals("jpeg");
    //             return new ProcessedImage(bytes.toByteArray(), jpeg ? "jpg" : "png", jpeg ? "image/jpeg" : "image/png");
    //         } finally { writer.dispose(); }
    //     }
    // 

    private int maxSide(BucketType type) {
        return switch (type) {
            case MATERIAL, GENERAL -> 1200;
            case PROD_ORDER -> 1600;
            case AVATAR -> 256;
        };
    }

    private float quality(BucketType type) {
        return switch (type) {
            case MATERIAL -> 0.82f;
            case PROD_ORDER, GENERAL -> 0.80f;
            case AVATAR -> 0.85f;
        };
    }
}
