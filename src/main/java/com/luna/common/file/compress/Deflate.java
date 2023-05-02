package com.luna.common.file.compress;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import com.luna.common.io.IoUtil;

/**
 * Deflate算法<br>
 * Deflate是同时使用了LZ77算法与哈夫曼编码（Huffman Coding）的一个无损数据压缩算法。
 *
 * @author looly
 * @since 5.7.8
 */
public class Deflate implements Closeable {

    private final InputStream source;
    private final boolean     nowrap;
    private OutputStream      target;

    /**
     * 构造
     *
     * @param source 源流
     * @param target 目标流
     * @param nowrap {@code true}表示兼容Gzip压缩
     */
    public Deflate(InputStream source, OutputStream target, boolean nowrap) {
        this.source = source;
        this.target = target;
        this.nowrap = nowrap;
    }

    /**
     * 创建Deflate
     *
     * @param source 源流
     * @param target 目标流
     * @param nowrap {@code true}表示兼容Gzip压缩
     * @return this
     */
    public static Deflate of(InputStream source, OutputStream target, boolean nowrap) {
        return new Deflate(source, target, nowrap);
    }

    /**
     * 获取目标流
     *
     * @return 目标流
     */
    public OutputStream getTarget() {
        return this.target;
    }

    /**
     * 将普通数据流压缩
     *
     * @param level 压缩级别，0~9
     * @return this
     */
    public Deflate deflater(int level) {
        target =
            (target instanceof DeflaterOutputStream) ? (DeflaterOutputStream)target : new DeflaterOutputStream(target, new Deflater(level, nowrap));
        try {
            IoUtil.copy(source, target);
            ((DeflaterOutputStream)target).finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * 将压缩流解压到target中
     *
     * @return this
     */
    public Deflate inflater() {
        target = (target instanceof InflaterOutputStream) ? (InflaterOutputStream)target : new InflaterOutputStream(target, new Inflater(nowrap));
        try {
            IoUtil.copy(source, target);
            ((InflaterOutputStream)target).finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public void close() {
        IoUtil.close(this.target);
        IoUtil.close(this.source);
    }
}
