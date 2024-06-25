package com.luna.common.img;

import com.luna.common.check.Assert;
import com.luna.common.encrypt.Base64Util;
import com.luna.common.file.FileNameUtil;
import com.luna.common.file.FileNameUtils;
import com.luna.common.file.FileTools;
import com.luna.common.io.IoUtil;
import com.luna.common.math.NumberUtil;
import com.luna.common.text.StringTools;
import com.luna.common.utils.ObjectUtils;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;

/**
 * 图片处理工具类：<br>
 * 功能：缩放图像、切割图像、旋转、图像类型转换、彩色转黑白、文字水印、图片水印等 <br>
 * 参考：http://blog.csdn.net/zhangzhikaixinya/article/details/8459400
 *
 * @author Looly
 */
public class ImgUtil {

    // region ----- [const] image type
    /**
     * 图形交换格式：GIF
     */
    public static final String IMAGE_TYPE_GIF  = "gif";
    /**
     * 联合照片专家组：JPG
     */
    public static final String IMAGE_TYPE_JPG  = "jpg";
    /**
     * 联合照片专家组：JPEG
     */
    public static final String IMAGE_TYPE_JPEG = "jpeg";
    /**
     * 英文Bitmap（位图）的简写，它是Windows操作系统中的标准图像文件格式：BMP
     */
    public static final String IMAGE_TYPE_BMP  = "bmp";
    /**
     * 可移植网络图形：PNG
     */
    public static final String IMAGE_TYPE_PNG  = "png";
    /**
     * Photoshop的专用格式：PSD
     */
    public static final String IMAGE_TYPE_PSD  = "psd";
    // endregion

    // ----------------------------------------------------------------------------------------------------------------------
    // scale

    /**
     * 缩放图像（按比例缩放），目标文件的扩展名决定目标文件类型
     *
     * @param srcImageFile 源图像文件
     * @param destImageFile 缩放后的图像文件，扩展名决定目标类型
     * @param scale 缩放比例。比例大于1时为放大，小于1大于0为缩小
     */
    public static void scale(File srcImageFile, File destImageFile, float scale) {
        BufferedImage image = null;
        try {
            image = read(srcImageFile);
            scale(image, destImageFile, scale);
        } finally {
            flush(image);
        }
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcStream 源图像来源流
     * @param destStream 缩放后的图像写出到的流
     * @param scale 缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @since 3.0.9
     */
    public static void scale(InputStream srcStream, OutputStream destStream, float scale) {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            scale(image, destStream, scale);
        } finally {
            flush(image);
        }
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcStream 源图像来源流
     * @param destStream 缩放后的图像写出到的流
     * @param scale 缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @since 3.1.0
     */
    public static void scale(ImageInputStream srcStream, ImageOutputStream destStream, float scale) {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            scale(image, destStream, scale);
        } finally {
            flush(image);
        }
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcImg 源图像来源流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param destFile 缩放后的图像写出到的流
     * @param scale 缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void scale(Image srcImg, File destFile, float scale) throws RuntimeException {
        Img.from(srcImg).setTargetImageType(FileNameUtil.extName(destFile)).scale(scale).write(destFile);
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcImg 源图像来源流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param out 缩放后的图像写出到的流
     * @param scale 缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void scale(Image srcImg, OutputStream out, float scale) throws RuntimeException {
        scale(srcImg, getImageOutputStream(out), scale);
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcImg 源图像来源流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param destImageStream 缩放后的图像写出到的流
     * @param scale 缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @throws RuntimeException IO异常
     * @since 3.1.0
     */
    public static void scale(Image srcImg, ImageOutputStream destImageStream, float scale) throws RuntimeException {
        writeJpg(scale(srcImg, scale), destImageStream);
    }

    /**
     * 缩放图像（按比例缩放）
     *
     * @param srcImg 源图像来源流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param scale 缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @return {@link Image}
     * @since 3.1.0
     */
    public static Image scale(Image srcImg, float scale) {
        return Img.from(srcImg).scale(scale).getImg();
    }

    /**
     * 缩放图像（按长宽缩放）<br>
     * 注意：目标长宽与原图不成比例会变形
     *
     * @param srcImg 源图像来源流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param width 目标宽度
     * @param height 目标高度
     * @return {@link Image}
     * @since 3.1.0
     */
    public static Image scale(Image srcImg, int width, int height) {
        return Img.from(srcImg).scale(width, height).getImg();
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认格式与源图片相同，无法识别原图片默认JPG
     *
     * @param srcImageFile 源图像文件地址
     * @param destImageFile 缩放后的图像地址
     * @param width 缩放后的宽度
     * @param height 缩放后的高度
     * @param fixedColor 补充的颜色，不补充为{@code null}
     * @throws RuntimeException IO异常
     */
    public static void scale(File srcImageFile, File destImageFile, int width, int height, Color fixedColor) throws RuntimeException {
        Img img = null;
        try {
            img = Img.from(srcImageFile);
            img.setTargetImageType(FileNameUtil.extName(destImageFile))
                .scale(width, height, fixedColor)//
                .write(destImageFile);
        } finally {
            IoUtil.flush(img);
        }
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param destStream 缩放后的图像目标流
     * @param width 缩放后的宽度
     * @param height 缩放后的高度
     * @param fixedColor 比例不对时补充的颜色，不补充为{@code null}
     * @throws RuntimeException IO异常
     */
    public static void scale(InputStream srcStream, OutputStream destStream, int width, int height, Color fixedColor) throws RuntimeException {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            scale(image, getImageOutputStream(destStream), width, height, fixedColor);
        } finally {
            flush(image);
        }
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param destStream 缩放后的图像目标流
     * @param width 缩放后的宽度
     * @param height 缩放后的高度
     * @param fixedColor 比例不对时补充的颜色，不补充为{@code null}
     * @throws RuntimeException IO异常
     */
    public static void scale(ImageInputStream srcStream, ImageOutputStream destStream, int width, int height, Color fixedColor)
        throws RuntimeException {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            scale(image, destStream, width, height, fixedColor);
        } finally {
            flush(image);
        }
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcImage 源图像
     * @param destImageStream 缩放后的图像目标流
     * @param width 缩放后的宽度
     * @param height 缩放后的高度
     * @param fixedColor 比例不对时补充的颜色，不补充为{@code null}
     * @throws RuntimeException IO异常
     */
    public static void scale(Image srcImage, ImageOutputStream destImageStream, int width, int height, Color fixedColor) throws RuntimeException {
        writeJpg(scale(srcImage, width, height, fixedColor), destImageStream);
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认为jpeg格式
     *
     * @param srcImage 源图像
     * @param width 缩放后的宽度
     * @param height 缩放后的高度
     * @param fixedColor 比例不对时补充的颜色，不补充为{@code null}
     * @return {@link Image}
     */
    public static Image scale(Image srcImage, int width, int height, Color fixedColor) {
        return Img.from(srcImage).scale(width, height, fixedColor).getImg();
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // cut

    /**
     * 图像切割(按指定起点坐标和宽高切割)
     *
     * @param srcImgFile 源图像文件
     * @param destImgFile 切片后的图像文件
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     * @since 3.1.0
     */
    public static void cut(File srcImgFile, File destImgFile, Rectangle rectangle) {
        BufferedImage image = null;
        try {
            image = read(srcImgFile);
            cut(image, destImgFile, rectangle);
        } finally {
            flush(image);
        }
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param destStream 切片后的图像输出流
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     * @since 3.1.0
     */
    public static void cut(InputStream srcStream, OutputStream destStream, Rectangle rectangle) {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            cut(image, destStream, rectangle);
        } finally {
            flush(image);
        }
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param destStream 切片后的图像输出流
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     * @since 3.1.0
     */
    public static void cut(ImageInputStream srcStream, ImageOutputStream destStream, Rectangle rectangle) {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            cut(image, destStream, rectangle);
        } finally {
            flush(image);
        }
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param destFile 输出的文件
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void cut(Image srcImage, File destFile, Rectangle rectangle) throws RuntimeException {
        write(cut(srcImage, rectangle), destFile);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param out 切片后的图像输出流
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     * @throws RuntimeException IO异常
     * @since 3.1.0
     */
    public static void cut(Image srcImage, OutputStream out, Rectangle rectangle) throws RuntimeException {
        cut(srcImage, getImageOutputStream(out), rectangle);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param destImageStream 切片后的图像输出流
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     * @throws RuntimeException IO异常
     * @since 3.1.0
     */
    public static void cut(Image srcImage, ImageOutputStream destImageStream, Rectangle rectangle) throws RuntimeException {
        writeJpg(cut(srcImage, rectangle), destImageStream);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     * @return {@link BufferedImage}
     * @since 3.1.0
     */
    public static Image cut(Image srcImage, Rectangle rectangle) {
        return Img.from(srcImage).setPositionBaseCentre(false).cut(rectangle).getImg();
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，填充满整个图片（直径取长宽最小值）
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param x 原图的x坐标起始位置
     * @param y 原图的y坐标起始位置
     * @return {@link Image}
     * @since 4.1.15
     */
    public static Image cut(Image srcImage, int x, int y) {
        return cut(srcImage, x, y, -1);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param x 原图的x坐标起始位置
     * @param y 原图的y坐标起始位置
     * @param radius 半径，小于0表示填充满整个图片（直径取长宽最小值）
     * @return {@link Image}
     * @since 4.1.15
     */
    public static Image cut(Image srcImage, int x, int y, int radius) {
        return Img.from(srcImage).cut(x, y, radius).getImg();
    }

    /**
     * 图像切片（指定切片的宽度和高度）
     *
     * @param srcImageFile 源图像
     * @param descDir 切片目标文件夹
     * @param destWidth 目标切片宽度。默认200
     * @param destHeight 目标切片高度。默认150
     */
    public static void slice(File srcImageFile, File descDir, int destWidth, int destHeight) {
        BufferedImage image = null;
        try {
            image = read(srcImageFile);
            slice(image, descDir, destWidth, destHeight);
        } finally {
            flush(image);
        }
    }

    /**
     * 图像切片（指定切片的宽度和高度）
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param descDir 切片目标文件夹
     * @param destWidth 目标切片宽度。默认200
     * @param destHeight 目标切片高度。默认150
     */
    public static void slice(Image srcImage, File descDir, int destWidth, int destHeight) {
        if (destWidth <= 0) {
            destWidth = 200; // 切片宽度
        }
        if (destHeight <= 0) {
            destHeight = 150; // 切片高度
        }
        int srcWidth = srcImage.getWidth(null); // 源图宽度
        int srcHeight = srcImage.getHeight(null); // 源图高度

        if (srcWidth < destWidth) {
            destWidth = srcWidth;
        }
        if (srcHeight < destHeight) {
            destHeight = srcHeight;
        }

        int cols; // 切片横向数量
        int rows; // 切片纵向数量
        // 计算切片的横向和纵向数量
        if (srcWidth % destWidth == 0) {
            cols = srcWidth / destWidth;
        } else {
            cols = (int)Math.floor((double)srcWidth / destWidth) + 1;
        }
        if (srcHeight % destHeight == 0) {
            rows = srcHeight / destHeight;
        } else {
            rows = (int)Math.floor((double)srcHeight / destHeight) + 1;
        }
        // 循环建立切片
        Image tag;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // 四个参数分别为图像起点坐标和宽高
                // 即: CropImageFilter(int x,int y,int width,int height)
                tag = cut(srcImage, new Rectangle(j * destWidth, i * destHeight, destWidth, destHeight));
                // 输出为文件
                write(tag, FileTools.file(descDir, "_r" + i + "_c" + j + ".jpg"));
            }
        }
    }

    /**
     * 图像切割（指定切片的行数和列数）
     *
     * @param srcImageFile 源图像文件
     * @param destDir 切片目标文件夹
     * @param rows 目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols 目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public static void sliceByRowsAndCols(File srcImageFile, File destDir, int rows, int cols) {
        sliceByRowsAndCols(srcImageFile, destDir, IMAGE_TYPE_JPEG, rows, cols);
    }

    /**
     * 图像切割（指定切片的行数和列数）
     *
     * @param srcImageFile 源图像文件
     * @param destDir 切片目标文件夹
     * @param format 目标文件格式
     * @param rows 目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols 目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public static void sliceByRowsAndCols(File srcImageFile, File destDir, String format, int rows, int cols) {
        BufferedImage image = null;
        try {
            image = read(srcImageFile);
            sliceByRowsAndCols(image, destDir, format, rows, cols);
        } finally {
            flush(image);
        }
    }

    /**
     * 图像切割（指定切片的行数和列数），默认RGB模式
     *
     * @param srcImage 源图像，如果非{@link BufferedImage}，则默认使用RGB模式
     * @param destDir 切片目标文件夹
     * @param rows 目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols 目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public static void sliceByRowsAndCols(Image srcImage, File destDir, int rows, int cols) {
        sliceByRowsAndCols(srcImage, destDir, IMAGE_TYPE_JPEG, rows, cols);
    }

    /**
     * 图像切割（指定切片的行数和列数），默认RGB模式
     *
     * @param srcImage 源图像，如果非{@link BufferedImage}，则默认使用RGB模式
     * @param destDir 切片目标文件夹
     * @param format 目标文件格式
     * @param rows 目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols 目标切片列数。默认2，必须是范围 [1, 20] 之内
     * @since 5.8.6
     */
    public static void sliceByRowsAndCols(Image srcImage, File destDir, String format, int rows, int cols) {
        if (!destDir.exists()) {
            FileTools.mkdir(destDir);
        } else if (!destDir.isDirectory()) {
            throw new IllegalArgumentException("Destination Dir must be a Directory !");
        }

        if (rows <= 0 || rows > 20) {
            rows = 2; // 切片行数
        }
        if (cols <= 0 || cols > 20) {
            cols = 2; // 切片列数
        }
        // 读取源图像
        int srcWidth = srcImage.getWidth(null); // 源图宽度
        int srcHeight = srcImage.getHeight(null); // 源图高度

        int destWidth = NumberUtil.partValue(srcWidth, cols); // 每张切片的宽度
        int destHeight = NumberUtil.partValue(srcHeight, rows); // 每张切片的高度

        // 循环建立切片
        Image tag;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tag = cut(srcImage, new Rectangle(j * destWidth, i * destHeight, destWidth, destHeight));
                // 输出为文件
                write(tag, new File(destDir, "_r" + i + "_c" + j + "." + format));
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // convert

    /**
     * 图像类型转换：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG
     *
     * @param srcImageFile 源图像文件
     * @param destImageFile 目标图像文件
     */
    public static void convert(File srcImageFile, File destImageFile) {
        Assert.notNull(srcImageFile);
        Assert.notNull(destImageFile);
        Assert.isTrue(!srcImageFile.equals(destImageFile), "Src file is equals to dest file!");

        final String srcExtName = FileNameUtil.extName(srcImageFile);
        final String destExtName = FileNameUtil.extName(destImageFile);
        if (StringTools.equalsIgnoreCase(srcExtName, destExtName)) {
            // 扩展名相同直接复制文件
            try {
                FileTools.copy(srcImageFile, destImageFile, true, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Img img = null;
        try {
            img = Img.from(srcImageFile);
            img.write(destImageFile);
        } finally {
            IoUtil.flush(img);
        }
    }

    /**
     * 图像类型转换：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param formatName 包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destStream 目标图像输出流
     * @since 3.0.9
     */
    public static void convert(InputStream srcStream, String formatName, OutputStream destStream) {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            write(image, formatName, getImageOutputStream(destStream));
        } finally {
            flush(image);
        }
    }

    /**
     * 图像类型转换：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param formatName 包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destImageStream 目标图像输出流
     * @since 4.1.14
     */
    public static void convert(Image srcImage, String formatName, ImageOutputStream destImageStream) {
        Img.from(srcImage).setTargetImageType(formatName).write(destImageStream);
    }

    /**
     * 图像类型转换：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param formatName 包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destImageStream 目标图像输出流
     * @param isSrcPng 源图片是否为PNG格式（参数无效）
     * @since 4.1.14
     */
    @Deprecated
    public static void convert(Image srcImage, String formatName, ImageOutputStream destImageStream, boolean isSrcPng) {
        convert(srcImage, formatName, destImageStream);
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // grey

    /**
     * 彩色转为黑白
     *
     * @param srcImageFile 源图像地址
     * @param destImageFile 目标图像地址
     */
    public static void gray(File srcImageFile, File destImageFile) {
        BufferedImage image = null;
        try {
            image = read(srcImageFile);
            gray(image, destImageFile);
        } finally {
            flush(image);
        }
    }

    /**
     * 彩色转为黑白<br>
     * 此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param destStream 目标图像流
     * @since 3.0.9
     */
    public static void gray(InputStream srcStream, OutputStream destStream) {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            gray(image, destStream);
        } finally {
            flush(image);
        }
    }

    /**
     * 彩色转为黑白<br>
     * 此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param destStream 目标图像流
     * @since 3.0.9
     */
    public static void gray(ImageInputStream srcStream, ImageOutputStream destStream) {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            gray(image, destStream);
        } finally {
            flush(image);
        }
    }

    /**
     * 彩色转为黑白
     *
     * @param srcImage 源图像流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param outFile 目标文件
     * @since 3.2.2
     */
    public static void gray(Image srcImage, File outFile) {
        write(gray(srcImage), outFile);
    }

    /**
     * 彩色转为黑白<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param out 目标图像流
     * @since 3.2.2
     */
    public static void gray(Image srcImage, OutputStream out) {
        gray(srcImage, getImageOutputStream(out));
    }

    /**
     * 彩色转为黑白<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param destImageStream 目标图像流
     * @throws RuntimeException IO异常
     * @since 3.0.9
     */
    public static void gray(Image srcImage, ImageOutputStream destImageStream) throws RuntimeException {
        writeJpg(gray(srcImage), destImageStream);
    }

    /**
     * 彩色转为黑白
     *
     * @param srcImage 源图像流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @return {@link Image}灰度后的图片
     * @since 3.1.0
     */
    public static Image gray(Image srcImage) {
        return Img.from(srcImage).gray().getImg();
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // binary

    /**
     * 彩色转为黑白二值化图片，根据目标文件扩展名确定转换后的格式
     *
     * @param srcImageFile 源图像地址
     * @param destImageFile 目标图像地址
     */
    public static void binary(File srcImageFile, File destImageFile) {
        BufferedImage image = null;
        try {
            image = read(srcImageFile);
            binary(image, destImageFile);
        } finally {
            flush(image);
        }
    }

    /**
     * 彩色转为黑白二值化图片<br>
     * 此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param destStream 目标图像流
     * @param imageType 图片格式(扩展名)
     * @since 4.0.5
     */
    public static void binary(InputStream srcStream, OutputStream destStream, String imageType) {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            binary(image, getImageOutputStream(destStream), imageType);
        } finally {
            flush(image);
        }
    }

    /**
     * 彩色转为黑白黑白二值化图片<br>
     * 此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param destStream 目标图像流
     * @param imageType 图片格式(扩展名)
     * @since 4.0.5
     */
    public static void binary(ImageInputStream srcStream, ImageOutputStream destStream, String imageType) {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            binary(image, destStream, imageType);
        } finally {
            flush(image);
        }
    }

    /**
     * 彩色转为黑白二值化图片，根据目标文件扩展名确定转换后的格式
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param outFile 目标文件
     * @since 4.0.5
     */
    public static void binary(Image srcImage, File outFile) {
        write(binary(srcImage), outFile);
    }

    /**
     * 彩色转为黑白二值化图片<br>
     * 此方法并不关闭流，输出JPG格式
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param out 目标图像流
     * @param imageType 图片格式(扩展名)
     * @since 4.0.5
     */
    public static void binary(Image srcImage, OutputStream out, String imageType) {
        binary(srcImage, getImageOutputStream(out), imageType);
    }

    /**
     * 彩色转为黑白二值化图片<br>
     * 此方法并不关闭流，输出JPG格式
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param destImageStream 目标图像流
     * @param imageType 图片格式(扩展名)
     * @throws RuntimeException IO异常
     * @since 4.0.5
     */
    public static void binary(Image srcImage, ImageOutputStream destImageStream, String imageType) throws RuntimeException {
        write(binary(srcImage), imageType, destImageStream);
    }

    /**
     * 彩色转为黑白二值化图片
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @return {@link Image}二值化后的图片
     * @since 4.0.5
     */
    public static Image binary(Image srcImage) {
        return Img.from(srcImage).binary().getImg();
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // press

    /**
     * 给图片添加文字水印
     *
     * @param imageFile 源图像文件
     * @param destFile 目标图像文件
     * @param pressText 水印文字
     * @param color 水印的字体颜色
     * @param font {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(File imageFile, File destFile, String pressText, Color color, Font font, int x, int y, float alpha) {
        BufferedImage image = null;
        try {
            image = read(imageFile);
            pressText(image, destFile, pressText, color, font, x, y, alpha);
        } finally {
            flush(image);
        }
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param destStream 目标图像流
     * @param pressText 水印文字
     * @param color 水印的字体颜色
     * @param font {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(InputStream srcStream, OutputStream destStream, String pressText, Color color, Font font, int x, int y,
        float alpha) {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            pressText(image, getImageOutputStream(destStream), pressText, color, font, x, y, alpha);
        } finally {
            flush(image);
        }
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param destStream 目标图像流
     * @param pressText 水印文字
     * @param color 水印的字体颜色
     * @param font {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(ImageInputStream srcStream, ImageOutputStream destStream, String pressText, Color color, Font font, int x, int y,
        float alpha) {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            pressText(image, destStream, pressText, color, font, x, y, alpha);
        } finally {
            flush(image);
        }
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param destFile 目标流
     * @param pressText 水印文字
     * @param color 水印的字体颜色
     * @param font {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void pressText(Image srcImage, File destFile, String pressText, Color color, Font font, int x, int y, float alpha)
        throws RuntimeException {
        write(pressText(srcImage, pressText, color, font, x, y, alpha), destFile);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param to 目标流
     * @param pressText 水印文字
     * @param color 水印的字体颜色
     * @param font {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void pressText(Image srcImage, OutputStream to, String pressText, Color color, Font font, int x, int y, float alpha)
        throws RuntimeException {
        pressText(srcImage, getImageOutputStream(to), pressText, color, font, x, y, alpha);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param destImageStream 目标图像流
     * @param pressText 水印文字
     * @param color 水印的字体颜色
     * @param font {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws RuntimeException IO异常
     */
    public static void pressText(Image srcImage, ImageOutputStream destImageStream, String pressText, Color color, Font font, int x, int y,
        float alpha) throws RuntimeException {
        writeJpg(pressText(srcImage, pressText, color, font, x, y, alpha), destImageStream);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param pressText 水印文字
     * @param color 水印的字体颜色
     * @param font {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @return 处理后的图像
     * @since 3.2.2
     */
    public static Image pressText(Image srcImage, String pressText, Color color, Font font, int x, int y, float alpha) {
        return Img.from(srcImage).pressText(pressText, color, font, x, y, alpha).getImg();
    }

    /**
     * 给图片添加图片水印
     *
     * @param srcImageFile 源图像文件
     * @param destImageFile 目标图像文件
     * @param pressImg 水印图片
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressImage(File srcImageFile, File destImageFile, Image pressImg, int x, int y, float alpha) {
        BufferedImage image = null;
        try {
            image = read(srcImageFile);
            pressImage(image, destImageFile, pressImg, x, y, alpha);
        } finally {
            flush(image);
        }
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param destStream 目标图像流
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressImage(InputStream srcStream, OutputStream destStream, Image pressImg, int x, int y, float alpha) {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            pressImage(image, getImageOutputStream(destStream), pressImg, x, y, alpha);
        } finally {
            flush(image);
        }
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param destStream 目标图像流
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws RuntimeException IO异常
     */
    public static void pressImage(ImageInputStream srcStream, ImageOutputStream destStream, Image pressImg, int x, int y, float alpha)
        throws RuntimeException {
        BufferedImage image = null;
        try {
            image = read(srcStream);
            pressImage(image, destStream, pressImg, x, y, alpha);
        } finally {
            flush(image);
        }

    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param outFile 写出文件
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void pressImage(Image srcImage, File outFile, Image pressImg, int x, int y, float alpha) throws RuntimeException {
        write(pressImage(srcImage, pressImg, x, y, alpha), outFile);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param out 目标图像流
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void pressImage(Image srcImage, OutputStream out, Image pressImg, int x, int y, float alpha) throws RuntimeException {
        pressImage(srcImage, getImageOutputStream(out), pressImg, x, y, alpha);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param destImageStream 目标图像流
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws RuntimeException IO异常
     */
    public static void pressImage(Image srcImage, ImageOutputStream destImageStream, Image pressImg, int x, int y, float alpha)
        throws RuntimeException {
        writeJpg(pressImage(srcImage, pressImg, x, y, alpha), destImageStream);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x 修正值。 默认在中间，偏移量相对于中间偏移
     * @param y 修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @return 结果图片
     */
    public static Image pressImage(Image srcImage, Image pressImg, int x, int y, float alpha) {
        return Img.from(srcImage).pressImage(pressImg, x, y, alpha).getImg();
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height，x,y从背景图片中心计算
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @return 结果图片
     * @since 4.1.14
     */
    public static Image pressImage(Image srcImage, Image pressImg, Rectangle rectangle, float alpha) {
        return Img.from(srcImage).pressImage(pressImg, rectangle, alpha).getImg();
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // rotate

    /**
     * 旋转图片为指定角度<br>
     * 此方法不会关闭输出流
     *
     * @param imageFile 被旋转图像文件
     * @param degree 旋转角度
     * @param outFile 输出文件
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void rotate(File imageFile, int degree, File outFile) throws RuntimeException {
        BufferedImage image = null;
        try {
            image = read(imageFile);
            rotate(image, degree, outFile);
        } finally {
            flush(image);
        }
    }

    /**
     * 旋转图片为指定角度<br>
     * 此方法不会关闭输出流
     *
     * @param image 目标图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param degree 旋转角度
     * @param outFile 输出文件
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void rotate(Image image, int degree, File outFile) throws RuntimeException {
        write(rotate(image, degree), outFile);
    }

    /**
     * 旋转图片为指定角度<br>
     * 此方法不会关闭输出流
     *
     * @param image 目标图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param degree 旋转角度
     * @param out 输出流
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void rotate(Image image, int degree, OutputStream out) throws RuntimeException {
        writeJpg(rotate(image, degree), getImageOutputStream(out));
    }

    /**
     * 旋转图片为指定角度<br>
     * 此方法不会关闭输出流，输出格式为JPG
     *
     * @param image 图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param degree 旋转角度
     * @param out 输出图像流
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void rotate(Image image, int degree, ImageOutputStream out) throws RuntimeException {
        writeJpg(rotate(image, degree), out);
    }

    /**
     * 旋转图片为指定角度<br>
     * 来自：<a href="http://blog.51cto.com/cping1982/130066">http://blog.51cto.com/cping1982/130066</a>
     *
     * @param image 图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param degree 旋转角度
     * @return 旋转后的图片
     * @since 3.2.2
     */
    public static Image rotate(Image image, int degree) {
        return Img.from(image).rotate(degree).getImg();
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // flip

    /**
     * 水平翻转图像
     *
     * @param imageFile 图像文件
     * @param outFile 输出文件
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void flip(File imageFile, File outFile) throws RuntimeException {
        BufferedImage image = null;
        try {
            image = read(imageFile);
            flip(image, outFile);
        } finally {
            flush(image);
        }
    }

    /**
     * 水平翻转图像
     *
     * @param image 图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param outFile 输出文件
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void flip(Image image, File outFile) throws RuntimeException {
        write(flip(image), outFile);
    }

    /**
     * 水平翻转图像
     *
     * @param image 图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param out 输出
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void flip(Image image, OutputStream out) throws RuntimeException {
        flip(image, getImageOutputStream(out));
    }

    /**
     * 水平翻转图像，写出格式为JPG
     *
     * @param image 图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @param out 输出
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static void flip(Image image, ImageOutputStream out) throws RuntimeException {
        writeJpg(flip(image), out);
    }

    /**
     * 水平翻转图像
     *
     * @param image 图像，使用结束后需手动调用{@link #flush(Image)}释放资源
     * @return 翻转后的图片
     * @since 3.2.2
     */
    public static Image flip(Image image) {
        return Img.from(image).flip().getImg();
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // compress

    /**
     * 压缩图像，输出图像只支持jpg文件
     *
     * @param imageFile 图像文件
     * @param outFile 输出文件，只支持jpg文件
     * @param quality 压缩比例，必须为0~1
     * @throws RuntimeException IO异常
     * @since 4.3.2
     */
    public static void compress(File imageFile, File outFile, float quality) throws RuntimeException {
        Img img = null;
        try {
            img = Img.from(imageFile);
            img.setQuality(quality).write(outFile);
        } finally {
            IoUtil.flush(img);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // other

    /**
     * {@link Image} 转 {@link RenderedImage}<br>
     * 首先尝试强转，否则新建一个{@link BufferedImage}后重新绘制，使用 {@link BufferedImage#TYPE_INT_RGB} 模式。
     *
     * @param img {@link Image}
     * @return {@link BufferedImage}
     * @since 4.3.2
     * @deprecated 改用 {@link #castToRenderedImage(Image, String)}
     */
    @Deprecated
    public static RenderedImage toRenderedImage(Image img) {
        return castToRenderedImage(img, IMAGE_TYPE_JPG);
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 首先尝试强转，否则新建一个{@link BufferedImage}后重新绘制，使用 {@link BufferedImage#TYPE_INT_RGB} 模式
     *
     * @param img {@link Image}
     * @return {@link BufferedImage}
     * @deprecated 改用 {@link #castToBufferedImage(Image, String)}
     */
    @Deprecated
    public static BufferedImage toBufferedImage(Image img) {
        return castToBufferedImage(img, IMAGE_TYPE_JPG);
    }

    /**
     * {@link Image} 转 {@link RenderedImage}<br>
     * 首先尝试强转，否则新建一个{@link BufferedImage}后重新绘制，使用 {@link BufferedImage#TYPE_INT_RGB} 模式。
     *
     * @param img {@link Image}
     * @param imageType 目标图片类型，例如jpg或png等
     * @return {@link BufferedImage}
     * @since 4.3.2
     */
    public static RenderedImage castToRenderedImage(final Image img, final String imageType) {
        if (img instanceof RenderedImage) {
            return (RenderedImage)img;
        }

        return toBufferedImage(img, imageType);
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 首先尝试强转，否则新建一个{@link BufferedImage}后重新绘制，使用 imageType 模式
     *
     * @param img {@link Image}
     * @param imageType 目标图片类型，例如jpg或png等
     * @return {@link BufferedImage}
     */
    public static BufferedImage castToBufferedImage(final Image img, final String imageType) {
        if (img instanceof BufferedImage) {
            return (BufferedImage)img;
        }

        return toBufferedImage(img, imageType);
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 如果源图片的RGB模式与目标模式一致，则直接转换，否则重新绘制<br>
     * 默认的，png图片使用 {@link BufferedImage#TYPE_INT_ARGB}模式，其它使用 {@link BufferedImage#TYPE_INT_RGB} 模式
     *
     * @param image {@link Image}
     * @param imageType 目标图片类型，例如jpg或png等
     * @return {@link BufferedImage}
     * @since 4.3.2
     */
    public static BufferedImage toBufferedImage(Image image, String imageType) {
        return toBufferedImage(image, imageType, null);
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 如果源图片的RGB模式与目标模式一致，则直接转换，否则重新绘制<br>
     * 默认的，png图片使用 {@link BufferedImage#TYPE_INT_ARGB}模式，其它使用 {@link BufferedImage#TYPE_INT_RGB} 模式
     *
     * @param image {@link Image}
     * @param imageType 目标图片类型，例如jpg或png等
     * @param backgroundColor 背景色{@link Color}
     * @return {@link BufferedImage}
     * @since 4.3.2
     */
    public static BufferedImage toBufferedImage(Image image, String imageType, Color backgroundColor) {
        final int type = IMAGE_TYPE_PNG.equalsIgnoreCase(imageType)
            ? BufferedImage.TYPE_INT_ARGB
            : BufferedImage.TYPE_INT_RGB;
        return toBufferedImage(image, type, backgroundColor);
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 如果源图片的RGB模式与目标模式一致，则直接转换，否则重新绘制
     *
     * @param image {@link Image}
     * @param imageType 目标图片类型，{@link BufferedImage}中的常量，例如黑白等
     * @return {@link BufferedImage}
     * @since 5.4.7
     */
    public static BufferedImage toBufferedImage(Image image, int imageType) {
        BufferedImage bufferedImage;
        if (image instanceof BufferedImage) {
            bufferedImage = (BufferedImage)image;
            if (imageType != bufferedImage.getType()) {
                bufferedImage = copyImage(image, imageType);
            }
            return bufferedImage;
        }

        bufferedImage = copyImage(image, imageType);
        return bufferedImage;
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 如果源图片的RGB模式与目标模式一致，则直接转换，否则重新绘制
     *
     * @param image {@link Image}
     * @param imageType 目标图片类型，{@link BufferedImage}中的常量，例如黑白等
     * @param backgroundColor 背景色{@link Color}
     * @return {@link BufferedImage}
     * @since 5.4.7
     */
    public static BufferedImage toBufferedImage(Image image, int imageType, Color backgroundColor) {
        BufferedImage bufferedImage;
        if (image instanceof BufferedImage) {
            bufferedImage = (BufferedImage)image;
            if (imageType != bufferedImage.getType()) {
                bufferedImage = copyImage(image, imageType, backgroundColor);
            }
            return bufferedImage;
        }

        bufferedImage = copyImage(image, imageType, backgroundColor);
        return bufferedImage;
    }

    /**
     * 将已有Image复制新的一份出来
     *
     * @param img {@link Image}
     * @param imageType 目标图片类型，{@link BufferedImage}中的常量，例如黑白等
     * @return {@link BufferedImage}
     * @see BufferedImage#TYPE_INT_RGB
     * @see BufferedImage#TYPE_INT_ARGB
     * @see BufferedImage#TYPE_INT_ARGB_PRE
     * @see BufferedImage#TYPE_INT_BGR
     * @see BufferedImage#TYPE_3BYTE_BGR
     * @see BufferedImage#TYPE_4BYTE_ABGR
     * @see BufferedImage#TYPE_4BYTE_ABGR_PRE
     * @see BufferedImage#TYPE_BYTE_GRAY
     * @see BufferedImage#TYPE_USHORT_GRAY
     * @see BufferedImage#TYPE_BYTE_BINARY
     * @see BufferedImage#TYPE_BYTE_INDEXED
     * @see BufferedImage#TYPE_USHORT_565_RGB
     * @see BufferedImage#TYPE_USHORT_555_RGB
     */
    public static BufferedImage copyImage(Image img, int imageType) {
        return copyImage(img, imageType, null);
    }

    /**
     * 将已有Image复制新的一份出来
     *
     * @param img {@link Image}
     * @param imageType 目标图片类型，{@link BufferedImage}中的常量，例如黑白等
     * @param backgroundColor 背景色，{@code null} 表示默认背景色（黑色或者透明）
     * @return {@link BufferedImage}
     * @see BufferedImage#TYPE_INT_RGB
     * @see BufferedImage#TYPE_INT_ARGB
     * @see BufferedImage#TYPE_INT_ARGB_PRE
     * @see BufferedImage#TYPE_INT_BGR
     * @see BufferedImage#TYPE_3BYTE_BGR
     * @see BufferedImage#TYPE_4BYTE_ABGR
     * @see BufferedImage#TYPE_4BYTE_ABGR_PRE
     * @see BufferedImage#TYPE_BYTE_GRAY
     * @see BufferedImage#TYPE_USHORT_GRAY
     * @see BufferedImage#TYPE_BYTE_BINARY
     * @see BufferedImage#TYPE_BYTE_INDEXED
     * @see BufferedImage#TYPE_USHORT_565_RGB
     * @see BufferedImage#TYPE_USHORT_555_RGB
     * @since 4.5.17
     */
    public static BufferedImage copyImage(Image img, int imageType, Color backgroundColor) {
        // ensures that all the pixels loaded
        // issue#1821@Github
        img = new ImageIcon(img).getImage();

        final BufferedImage bimage = new BufferedImage(
            img.getWidth(null), img.getHeight(null), imageType);
        final Graphics2D bGr = GraphicsUtil.createGraphics(bimage, backgroundColor);
        try {
            bGr.drawImage(img, 0, 0, null);
        } finally {
            bGr.dispose();
        }

        return bimage;
    }

    /**
     * 创建与当前设备颜色模式兼容的 {@link BufferedImage}
     *
     * @param width 宽度
     * @param height 高度
     * @param transparency 透明模式，见 {@link Transparency}
     * @return {@link BufferedImage}
     * @since 5.7.13
     */
    public static BufferedImage createCompatibleImage(int width, int height, int transparency) throws HeadlessException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        return gc.createCompatibleImage(width, height, transparency);
    }

    /**
     * 将Base64编码的图像信息转为 {@link BufferedImage}
     *
     * @param base64 图像的Base64表示
     * @return {@link BufferedImage}
     * @throws RuntimeException IO异常
     */
    public static BufferedImage toImage(String base64) throws RuntimeException {
        return toImage(Base64Util.decodeBase64(base64));
    }

    /**
     * 将的图像bytes转为 {@link BufferedImage}
     *
     * @param imageBytes 图像bytes
     * @return {@link BufferedImage}
     * @throws RuntimeException IO异常
     */
    public static BufferedImage toImage(byte[] imageBytes) throws RuntimeException {
        return read(new ByteArrayInputStream(imageBytes));
    }

    /**
     * 将图片对象转换为InputStream形式
     *
     * @param image 图片对象
     * @param imageType 图片类型
     * @return Base64的字符串表现形式
     * @since 4.2.4
     */
    public static ByteArrayInputStream toStream(Image image, String imageType) {
        return IoUtil.toStream(toBytes(image, imageType));
    }

    /**
     * 将图片对象转换为Base64的Data URI形式，格式为：data:image/[imageType];base64,[data]
     *
     * @param image 图片对象
     * @param imageType 图片类型
     * @return Base64的字符串表现形式
     * @since 5.3.6
     */
    public static String toBase64DataUri(Image image, String imageType) {
        return getDataUri(
            "image/" + imageType, null, "base64",
            toBase64(image, imageType));
    }

    /**
     * 将图片对象转换为Base64形式
     *
     * @param image 图片对象
     * @param imageType 图片类型
     * @return Base64的字符串表现形式
     * @since 4.1.8
     */
    public static String toBase64(Image image, String imageType) {
        return Base64Util.encodeBase64(toBytes(image, imageType));
    }

    /**
     * 将图片对象转换为bytes形式
     *
     * @param image 图片对象
     * @param imageType 图片类型
     * @return Base64的字符串表现形式
     * @since 5.2.4
     */
    public static byte[] toBytes(Image image, String imageType) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(image, imageType, out);
        return out.toByteArray();
    }

    /**
     * 根据文字创建PNG图片
     *
     * @param str 文字
     * @param font 字体{@link Font}
     * @param backgroundColor 背景颜色，默认透明
     * @param fontColor 字体颜色，默认黑色
     * @param out 图片输出地
     * @throws RuntimeException IO异常
     */
    public static void createImage(String str, Font font, Color backgroundColor, Color fontColor, ImageOutputStream out) throws RuntimeException {
        writePng(createImage(str, font, backgroundColor, fontColor, BufferedImage.TYPE_INT_ARGB), out);
    }

    /**
     * 根据文字创建透明背景的PNG图片
     *
     * @param str 文字
     * @param font 字体{@link Font}
     * @param fontColor 字体颜色，默认黑色
     * @param out 图片输出地
     * @throws RuntimeException IO异常
     */
    public static void createTransparentImage(String str, Font font, Color fontColor, ImageOutputStream out) throws RuntimeException {
        writePng(createImage(str, font, null, fontColor, BufferedImage.TYPE_INT_ARGB), out);
    }

    /**
     * 根据文字创建图片
     *
     * @param str 文字
     * @param font 字体{@link Font}
     * @param backgroundColor 背景颜色，默认透明
     * @param fontColor 字体颜色，默认黑色
     * @param imageType 图片类型，见：{@link BufferedImage}
     * @return 图片
     * @throws RuntimeException IO异常
     */
    public static BufferedImage createImage(String str, Font font, Color backgroundColor, Color fontColor, int imageType) throws RuntimeException {
        // 获取font的样式应用在str上的整个矩形
        final Rectangle2D r = getRectangle(str, font);
        // 获取单个字符的高度
        int unitHeight = (int)Math.floor(r.getHeight());
        // 获取整个str用了font样式的宽度这里用四舍五入后+1保证宽度绝对能容纳这个字符串作为图片的宽度
        int width = (int)Math.round(r.getWidth()) + 1;
        // 把单个字符的高度+3保证高度绝对能容纳字符串作为图片的高度
        int height = unitHeight + 3;

        // 创建图片
        BufferedImage image = new BufferedImage(width, height, imageType);
        Graphics g = image.getGraphics();
        if (null != backgroundColor) {
            // 先用背景色填充整张图片,也就是背景
            g.setColor(backgroundColor);
            g.fillRect(0, 0, width, height);
        }

        g.setColor(ObjectUtils.defaultIfNull(fontColor, Color.BLACK));
        g.setFont(font);// 设置画笔字体
        g.drawString(str, 0, font.getSize());// 画出字符串
        g.dispose();

        return image;
    }

    /**
     * 获取font的样式应用在str上的整个矩形
     *
     * @param str 字符串，必须非空
     * @param font 字体，必须非空
     * @return {@link Rectangle2D}
     * @since 5.3.3
     */
    public static Rectangle2D getRectangle(String str, Font font) {
        return font.getStringBounds(str,
            new FontRenderContext(AffineTransform.getScaleInstance(1, 1),
                false,
                false));
    }

    /**
     * 根据文件创建字体<br>
     * 首先尝试创建{@link Font#TRUETYPE_FONT}字体，此类字体无效则创建{@link Font#TYPE1_FONT}
     *
     * @param fontFile 字体文件
     * @return {@link Font}
     * @since 3.0.9
     */
    public static Font createFont(File fontFile) {
        return FontUtil.createFont(fontFile);
    }

    /**
     * 根据文件创建字体<br>
     * 首先尝试创建{@link Font#TRUETYPE_FONT}字体，此类字体无效则创建{@link Font#TYPE1_FONT}
     *
     * @param fontStream 字体流
     * @return {@link Font}
     * @since 3.0.9
     */
    public static Font createFont(InputStream fontStream) {
        return FontUtil.createFont(fontStream);
    }

    /**
     * 创建{@link Graphics2D}
     *
     * @param image {@link BufferedImage}
     * @param color {@link Color}背景颜色以及当前画笔颜色
     * @return {@link Graphics2D}
     * @see GraphicsUtil#createGraphics(BufferedImage, Color)
     * @since 3.2.3
     */
    public static Graphics2D createGraphics(BufferedImage image, Color color) {
        return GraphicsUtil.createGraphics(image, color);
    }

    /**
     * 写出图像为JPG格式
     *
     * @param image {@link Image}
     * @param destImageStream 写出到的目标流
     * @throws RuntimeException IO异常
     */
    public static void writeJpg(Image image, ImageOutputStream destImageStream) throws RuntimeException {
        write(image, IMAGE_TYPE_JPG, destImageStream);
    }

    /**
     * 写出图像为PNG格式
     *
     * @param image {@link Image}
     * @param destImageStream 写出到的目标流
     * @throws RuntimeException IO异常
     */
    public static void writePng(Image image, ImageOutputStream destImageStream) throws RuntimeException {
        write(image, IMAGE_TYPE_PNG, destImageStream);
    }

    /**
     * 写出图像为JPG格式
     *
     * @param image {@link Image}
     * @param out 写出到的目标流
     * @throws RuntimeException IO异常
     * @since 4.0.10
     */
    public static void writeJpg(Image image, OutputStream out) throws RuntimeException {
        write(image, IMAGE_TYPE_JPG, out);
    }

    /**
     * 写出图像为PNG格式
     *
     * @param image {@link Image}
     * @param out 写出到的目标流
     * @throws RuntimeException IO异常
     * @since 4.0.10
     */
    public static void writePng(Image image, OutputStream out) throws RuntimeException {
        write(image, IMAGE_TYPE_PNG, out);
    }

    /**
     * 按照目标格式写出图像：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param srcStream 源图像流
     * @param formatName 包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destStream 目标图像输出流
     * @since 5.0.0
     */
    public static void write(ImageInputStream srcStream, String formatName, ImageOutputStream destStream) {
        write(read(srcStream), formatName, destStream);
    }

    /**
     * 写出图像：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param image {@link Image}
     * @param imageType 图片类型（图片扩展名）
     * @param out 写出到的目标流
     * @throws RuntimeException IO异常
     * @since 3.1.2
     */
    public static void write(Image image, String imageType, OutputStream out) throws RuntimeException {
        write(image, imageType, getImageOutputStream(out));
    }

    /**
     * 写出图像为指定格式：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param image {@link Image}
     * @param imageType 图片类型（图片扩展名）
     * @param destImageStream 写出到的目标流
     * @return 是否成功写出，如果返回false表示未找到合适的Writer
     * @throws RuntimeException IO异常
     * @since 3.1.2
     */
    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream) throws RuntimeException {
        return write(image, imageType, destImageStream, 1);
    }

    /**
     * 写出图像为指定格式
     *
     * @param image {@link Image}
     * @param imageType 图片类型（图片扩展名）
     * @param destImageStream 写出到的目标流
     * @param quality 质量，数字为0~1（不包括0和1）表示质量压缩比，除此数字外设置表示不压缩
     * @return 是否成功写出，如果返回false表示未找到合适的Writer
     * @throws RuntimeException IO异常
     * @since 4.3.2
     */
    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream, float quality) throws RuntimeException {
        return write(image, imageType, destImageStream, quality, null);
    }

    /**
     * 写出图像为指定格式
     *
     * @param image {@link Image}
     * @param imageType 图片类型（图片扩展名）
     * @param destImageStream 写出到的目标流
     * @param quality 质量，数字为0~1（不包括0和1）表示质量压缩比，除此数字外设置表示不压缩
     * @param backgroundColor 背景色{@link Color}
     * @return 是否成功写出，如果返回false表示未找到合适的Writer
     * @throws RuntimeException IO异常
     * @since 4.3.2
     */
    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream, float quality, Color backgroundColor)
        throws RuntimeException {
        if (StringTools.isBlank(imageType)) {
            imageType = IMAGE_TYPE_JPG;
        }

        final BufferedImage bufferedImage = toBufferedImage(image, imageType, backgroundColor);
        final ImageWriter writer = getWriter(bufferedImage, imageType);
        return write(bufferedImage, writer, destImageStream, quality);
    }

    /**
     * 写出图像为目标文件扩展名对应的格式
     *
     * @param image {@link Image}
     * @param targetFile 目标文件
     * @throws RuntimeException IO异常
     * @since 3.1.0
     */
    public static void write(Image image, File targetFile) throws RuntimeException {
        FileTools.touch(targetFile);
        ImageOutputStream out = null;
        try {
            out = getImageOutputStream(targetFile);
            write(image, FileNameUtil.extName(targetFile), out);
        } finally {
            IoUtil.close(out);
        }
    }

    /**
     * 通过{@link ImageWriter}写出图片到输出流
     *
     * @param image 图片
     * @param writer {@link ImageWriter}
     * @param output 输出的Image流{@link ImageOutputStream}
     * @param quality 质量，数字为0~1（不包括0和1）表示质量压缩比，除此数字外设置表示不压缩
     * @return 是否成功写出
     * @since 4.3.2
     */
    public static boolean write(Image image, ImageWriter writer, ImageOutputStream output, float quality) {
        if (writer == null) {
            return false;
        }

        writer.setOutput(output);
        final RenderedImage renderedImage = castToRenderedImage(image, IMAGE_TYPE_JPG);
        // 设置质量
        ImageWriteParam imgWriteParams = null;
        if (quality > 0 && quality < 1) {
            imgWriteParams = writer.getDefaultWriteParam();
            if (imgWriteParams.canWriteCompressed()) {
                imgWriteParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                imgWriteParams.setCompressionQuality(quality);
                final ColorModel colorModel = renderedImage.getColorModel();// ColorModel.getRGBdefault();
                imgWriteParams.setDestinationType(new ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
            }
        }

        try {
            if (null != imgWriteParams) {
                writer.write(null, new IIOImage(renderedImage, null, null), imgWriteParams);
            } else {
                writer.write(renderedImage);
            }
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            writer.dispose();
        }
        return true;
    }

    /**
     * 获得{@link ImageReader}
     *
     * @param type 图片文件类型，例如 "jpeg" 或 "tiff"
     * @return {@link ImageReader}
     */
    public static ImageReader getReader(String type) {
        final Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(type);
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    /**
     * 从文件中读取图片，请使用绝对路径，使用相对路径会相对于ClassPath
     *
     * @param imageFilePath 图片文件路径
     * @return 图片
     * @since 4.1.15
     */
    public static BufferedImage read(String imageFilePath) {
        return read(FileTools.file(imageFilePath));
    }

    /**
     * 从文件中读取图片
     *
     * @param imageFile 图片文件
     * @return 图片
     * @since 3.2.2
     */
    public static BufferedImage read(File imageFile) {
        BufferedImage result;
        try {
            result = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type of file [" + imageFile.getName() + "] is not supported!");
        }

        return result;
    }

    /**
     * 从URL中获取或读取图片对象
     *
     * @param url URL
     * @return {@link Image}
     * @since 5.5.8
     */
    public static Image getImage(URL url) {
        return Toolkit.getDefaultToolkit().getImage(url);
    }

    /**
     * 从流中读取图片
     *
     * @param imageStream 图片文件
     * @return 图片
     * @since 3.2.2
     */
    public static BufferedImage read(InputStream imageStream) {
        BufferedImage result;
        try {
            result = ImageIO.read(imageStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        }

        return result;
    }

    /**
     * 从图片流中读取图片
     *
     * @param imageStream 图片文件
     * @return 图片
     * @since 3.2.2
     */
    public static BufferedImage read(ImageInputStream imageStream) {
        BufferedImage result;
        try {
            result = ImageIO.read(imageStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        }

        return result;
    }

    /**
     * 从URL中读取图片
     *
     * @param imageUrl 图片文件
     * @return 图片
     * @since 3.2.2
     */
    public static BufferedImage read(URL imageUrl) {
        BufferedImage result;
        try {
            result = ImageIO.read(imageUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type of [" + imageUrl + "] is not supported!");
        }

        return result;
    }

    /**
     * 获取{@link ImageOutputStream}
     *
     * @param out {@link OutputStream}
     * @return {@link ImageOutputStream}
     * @throws RuntimeException IO异常
     * @since 3.1.2
     */
    public static ImageOutputStream getImageOutputStream(OutputStream out) throws RuntimeException {
        ImageOutputStream result;
        try {
            result = ImageIO.createImageOutputStream(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        }

        return result;
    }

    /**
     * 获取{@link ImageOutputStream}
     *
     * @param outFile {@link File}
     * @return {@link ImageOutputStream}
     * @throws RuntimeException IO异常
     * @since 3.2.2
     */
    public static ImageOutputStream getImageOutputStream(File outFile) throws RuntimeException {
        ImageOutputStream result;
        try {
            result = ImageIO.createImageOutputStream(outFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type of file [" + outFile.getName() + "] is not supported!");
        }

        return result;
    }

    /**
     * 获取{@link ImageInputStream}
     *
     * @param in {@link InputStream}
     * @return {@link ImageInputStream}
     * @throws RuntimeException IO异常
     * @since 3.1.2
     */
    public static ImageInputStream getImageInputStream(InputStream in) throws RuntimeException {
        ImageOutputStream result;
        try {
            result = ImageIO.createImageOutputStream(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        }

        return result;
    }

    /**
     * 根据给定的Image对象和格式获取对应的{@link ImageWriter}，如果未找到合适的Writer，返回null
     *
     * @param img {@link Image}
     * @param formatName 图片格式，例如"jpg"、"png"
     * @return {@link ImageWriter}
     * @since 4.3.2
     */
    public static ImageWriter getWriter(Image img, String formatName) {
        final ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(toBufferedImage(img, formatName));
        final Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, formatName);
        return iter.hasNext() ? iter.next() : null;
    }

    /**
     * 根据给定的图片格式或者扩展名获取{@link ImageWriter}，如果未找到合适的Writer，返回null
     *
     * @param formatName 图片格式或扩展名，例如"jpg"、"png"
     * @return {@link ImageWriter}
     * @since 4.3.2
     */
    public static ImageWriter getWriter(String formatName) {
        ImageWriter writer = null;
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(formatName);
        if (iter.hasNext()) {
            writer = iter.next();
        }
        if (null == writer) {
            // 尝试扩展名获取
            iter = ImageIO.getImageWritersBySuffix(formatName);
            if (iter.hasNext()) {
                writer = iter.next();
            }
        }
        return writer;
    }

    // --------------------------------------------------------------------------------------------------------------------
    // Color

    /**
     * Color对象转16进制表示，例如#fcf6d6
     *
     * @param color {@link Color}
     * @return 16进制的颜色值，例如#fcf6d6
     * @see ColorUtil#toHex(Color)
     * @since 4.1.14
     */
    public static String toHex(Color color) {
        return ColorUtil.toHex(color);
    }

    /**
     * RGB颜色值转换成十六进制颜色码
     *
     * @param r 红(R)
     * @param g 绿(G)
     * @param b 蓝(B)
     * @return 返回字符串形式的 十六进制颜色码
     * @see ColorUtil#toHex(int, int, int)
     */
    public static String toHex(int r, int g, int b) {
        return ColorUtil.toHex(r, g, b);
    }

    /**
     * 16进制的颜色值转换为Color对象，例如#fcf6d6
     *
     * @param hex 16进制的颜色值，例如#fcf6d6
     * @return {@link Color}
     * @since 4.1.14
     */
    public static Color hexToColor(String hex) {
        return ColorUtil.hexToColor(hex);
    }

    /**
     * 获取一个RGB值对应的颜色
     *
     * @param rgb RGB值
     * @return {@link Color}
     * @see ColorUtil#getColor(int)
     * @since 4.1.14
     */
    public static Color getColor(int rgb) {
        return ColorUtil.getColor(rgb);
    }

    /**
     * 将颜色值转换成具体的颜色类型 汇集了常用的颜色集，支持以下几种形式：
     *
     * <pre>
     * 1. 颜色的英文名（大小写皆可）
     * 2. 16进制表示，例如：#fcf6d6或者$fcf6d6
     * 3. RGB形式，例如：13,148,252
     * </pre>
     * <p>
     * 方法来自：com.lnwazg.kit
     *
     * @param colorName 颜色的英文名，16进制表示或RGB表示
     * @return {@link Color}
     * @see ColorUtil#getColor(String)
     * @since 4.1.14
     */
    public static Color getColor(String colorName) {
        return ColorUtil.getColor(colorName);
    }

    /**
     * 生成随机颜色
     *
     * @return 随机颜色
     * @see ColorUtil#randomColor()
     * @since 3.1.2
     */
    public static Color randomColor() {
        return ColorUtil.randomColor();
    }

    /**
     * 生成随机颜色
     *
     * @param random 随机对象 {@link Random}
     * @return 随机颜色
     * @see ColorUtil#randomColor(Random)
     * @since 3.1.2
     */
    public static Color randomColor(Random random) {
        return ColorUtil.randomColor(random);
    }

    /**
     * 获得修正后的矩形坐标位置，变为以背景中心为基准坐标（即x,y == 0,0时，处于背景正中）
     *
     * @param rectangle 矩形
     * @param backgroundWidth 参考宽（背景宽）
     * @param backgroundHeight 参考高（背景高）
     * @return 修正后的{@link Point}
     * @since 5.3.6
     */
    public static Point getPointBaseCentre(Rectangle rectangle, int backgroundWidth, int backgroundHeight) {
        return new Point(
            rectangle.x + (Math.abs(backgroundWidth - rectangle.width) / 2), //
            rectangle.y + (Math.abs(backgroundHeight - rectangle.height) / 2)//
        );
    }

    /**
     * 获取给定图片的主色调，背景填充用
     *
     * @param image {@link BufferedImage}
     * @param rgbFilters 过滤多种颜色
     * @return {@link String} #ffffff
     * @since 5.6.7
     */
    public static String getMainColor(BufferedImage image, int[]... rgbFilters) {
        return ColorUtil.getMainColor(image, rgbFilters);
    }
    // ------------------------------------------------------------------------------------------------------ 背景图换算

    /**
     * 背景移除
     * 图片去底工具
     * 将 "纯色背景的图片" 还原成 "透明背景的图片"
     * 将纯色背景的图片转成矢量图
     * 取图片边缘的像素点和获取到的图片主题色作为要替换的背景色
     * 再加入一定的容差值,然后将所有像素点与该颜色进行比较
     * 发现相同则将颜色不透明度设置为0,使颜色完全透明.
     *
     * @param inputPath 要处理图片的路径
     * @param outputPath 输出图片的路径
     * @param tolerance 容差值[根据图片的主题色,加入容差值,值的范围在0~255之间]
     * @return 返回处理结果 true:图片处理完成 false:图片处理失败
     */
    public static boolean backgroundRemoval(String inputPath, String outputPath, int tolerance) {
        return BackgroundRemoval.backgroundRemoval(inputPath, outputPath, tolerance);
    }

    /**
     * 背景移除
     * 图片去底工具
     * 将 "纯色背景的图片" 还原成 "透明背景的图片"
     * 将纯色背景的图片转成矢量图
     * 取图片边缘的像素点和获取到的图片主题色作为要替换的背景色
     * 再加入一定的容差值,然后将所有像素点与该颜色进行比较
     * 发现相同则将颜色不透明度设置为0,使颜色完全透明.
     *
     * @param input 需要进行操作的图片
     * @param output 最后输出的文件
     * @param tolerance 容差值[根据图片的主题色,加入容差值,值的取值范围在0~255之间]
     * @return 返回处理结果 true:图片处理完成 false:图片处理失败
     */
    public static boolean backgroundRemoval(File input, File output, int tolerance) {
        return BackgroundRemoval.backgroundRemoval(input, output, tolerance);
    }

    /**
     * 背景移除
     * 图片去底工具
     * 将 "纯色背景的图片" 还原成 "透明背景的图片"
     * 将纯色背景的图片转成矢量图
     * 取图片边缘的像素点和获取到的图片主题色作为要替换的背景色
     * 再加入一定的容差值,然后将所有像素点与该颜色进行比较
     * 发现相同则将颜色不透明度设置为0,使颜色完全透明.
     *
     * @param input 需要进行操作的图片
     * @param output 最后输出的文件
     * @param override 指定替换成的背景颜色 为null时背景为透明
     * @param tolerance 容差值[根据图片的主题色,加入容差值,值的取值范围在0~255之间]
     * @return 返回处理结果 true:图片处理完成 false:图片处理失败
     */
    public static boolean backgroundRemoval(File input, File output, Color override, int tolerance) {
        return BackgroundRemoval.backgroundRemoval(input, output, override, tolerance);
    }

    /**
     * 背景移除
     * 图片去底工具
     * 将 "纯色背景的图片" 还原成 "透明背景的图片"
     * 将纯色背景的图片转成矢量图
     * 取图片边缘的像素点和获取到的图片主题色作为要替换的背景色
     * 再加入一定的容差值,然后将所有像素点与该颜色进行比较
     * 发现相同则将颜色不透明度设置为0,使颜色完全透明.
     *
     * @param bufferedImage 需要进行处理的图片流
     * @param override 指定替换成的背景颜色 为null时背景为透明
     * @param tolerance 容差值[根据图片的主题色,加入容差值,值的取值范围在0~255之间]
     * @return 返回处理好的图片流
     */
    public static BufferedImage backgroundRemoval(BufferedImage bufferedImage, Color override, int tolerance) {
        return BackgroundRemoval.backgroundRemoval(bufferedImage, override, tolerance);
    }

    /**
     * 背景移除
     * 图片去底工具
     * 将 "纯色背景的图片" 还原成 "透明背景的图片"
     * 将纯色背景的图片转成矢量图
     * 取图片边缘的像素点和获取到的图片主题色作为要替换的背景色
     * 再加入一定的容差值,然后将所有像素点与该颜色进行比较
     * 发现相同则将颜色不透明度设置为0,使颜色完全透明.
     *
     * @param outputStream 需要进行处理的图片字节数组流
     * @param override 指定替换成的背景颜色 为null时背景为透明
     * @param tolerance 容差值[根据图片的主题色,加入容差值,值的取值范围在0~255之间]
     * @return 返回处理好的图片流
     */
    public static BufferedImage backgroundRemoval(ByteArrayOutputStream outputStream, Color override, int tolerance) {
        return BackgroundRemoval.backgroundRemoval(outputStream, override, tolerance);
    }

    /**
     * 图片颜色转换<br>
     * 可以使用灰度 (gray)等
     *
     * @param colorSpace 颜色模式，如灰度等
     * @param image 被转换的图片
     * @return 转换后的图片
     * @since 5.7.8
     */
    public static BufferedImage colorConvert(ColorSpace colorSpace, BufferedImage image) {
        return filter(new ColorConvertOp(colorSpace, null), image);
    }

    /**
     * 转换图片<br>
     * 可以使用一系列平移 (translation)、缩放 (scale)、翻转 (flip)、旋转 (rotation) 和错切 (shear) 来构造仿射变换。
     *
     * @param xform 2D仿射变换，它执行从 2D 坐标到其他 2D 坐标的线性映射，保留了线的“直线性”和“平行性”。
     * @param image 被转换的图片
     * @return 转换后的图片
     * @since 5.7.8
     */
    public static BufferedImage transform(AffineTransform xform, BufferedImage image) {
        return filter(new AffineTransformOp(xform, null), image);
    }

    /**
     * 图片过滤转换
     *
     * @param op 过滤操作实现，如二维转换可传入{@link AffineTransformOp}
     * @param image 原始图片
     * @return 过滤后的图片
     * @since 5.7.8
     */
    public static BufferedImage filter(BufferedImageOp op, BufferedImage image) {
        return op.filter(image, null);
    }

    /**
     * 图片滤镜，借助 {@link ImageFilter}实现，实现不同的图片滤镜
     *
     * @param filter 滤镜实现
     * @param image 图片
     * @return 滤镜后的图片
     * @since 5.7.8
     */
    public static Image filter(ImageFilter filter, Image image) {
        return Toolkit.getDefaultToolkit().createImage(
            new FilteredImageSource(image.getSource(), filter));
    }

    /**
     * 刷新和释放{@link Image} 资源
     *
     * @param image {@link Image}
     */
    public static void flush(Image image) {
        if (null != image) {
            image.flush();
        }
    }

    /**
     * Data URI Scheme封装。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br>
     * 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>
     * Data URI的格式规范：
     * 
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param charset 可选项（null表示无），源文本的字符集编码方式
     * @param encoding 数据编码方式（US-ASCII，BASE64等）
     * @param data 编码后的数据
     * @return Data URI字符串
     * @since 5.3.6
     */
    public static String getDataUri(String mimeType, Charset charset, String encoding, String data) {
        final StringBuilder builder = new StringBuilder("data:");
        if (StringTools.isNotBlank(mimeType)) {
            builder.append(mimeType);
        }
        if (null != charset) {
            builder.append(";charset=").append(charset.name());
        }
        if (StringTools.isNotBlank(encoding)) {
            builder.append(';').append(encoding);
        }
        builder.append(',').append(data);

        return builder.toString();
    }
}
