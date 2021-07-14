package com.luna.common.file;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.IOException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author chenzhangyue@weidian.com
 * 2021/7/13
 */
public class ZipFileUtils {

    /**
     * 没有使用Buffer压缩文件
     */
    public static void zipFileNoBuffer(String filePath, String outPath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            zipDirectoryNoBuffer(filePath, outPath);
            return;
        }
        zipFileNoBuffer(outPath, Sets.newHashSet(file));
    }

    /**
     * 没有使用Buffer压缩目录
     *
     * @param filePath 文件目录
     * @param outPath 输出路径
     */
    public static void zipDirectoryNoBuffer(String filePath, String outPath) {
        File file = new File(filePath);
        Collection<File> listFiles = FileUtils.listFiles(file, null, true);
        if (CollectionUtils.isEmpty(listFiles)) {
            return;
        }
        zipFileNoBuffer(outPath, listFiles);
    }

    /**
     * 压缩文件无缓冲
     *
     * @param outPath 输出目录 eg： ./hello/xxx.zip
     * @param files 待压缩文件列表
     */
    private static void zipFileNoBuffer(String outPath, Collection<File> files) {
        try {
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outPath));
            int temp = 0;
            for (File file : files) {
                InputStream input = new FileInputStream(file);
                zipOut.putNextEntry(new ZipEntry(file.getName()));
                while ((temp = input.read()) != -1) {
                    zipOut.write(temp);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用了Buffer压缩zip文件
     *
     * @param filePath 文件目录
     * @param outPath 输出目录
     */
    public static void zipFileBuffer(String filePath, String outPath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            zipDirectoryBuffer(filePath, outPath);
            return;
        }
        zipFileBuffer(outPath, Sets.newHashSet(file));
    }

    /**
     * 使用了Buffer压缩zip文件
     *
     * @param outPath 输出路径
     * @param files 文件列表
     */
    private static void zipFileBuffer(String outPath, Collection<File> files) {
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outPath));
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zipOut)) {
            int temp;
            for (File tempFile : files) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(tempFile));
                zipOut.putNextEntry(new ZipEntry(tempFile.getPath()));
                while ((temp = bufferedInputStream.read()) != -1) {
                    bufferedOutputStream.write(temp);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用了Buffer压缩目录
     *
     * @param filePath 文件目录
     * @param outPath 输出路径
     */
    public static void zipDirectoryBuffer(String filePath, String outPath) {
        File file = new File(filePath);
        Collection<File> listFiles = FileUtils.listFiles(file, null, true);
        if (CollectionUtils.isEmpty(listFiles)) {
            return;
        }
        zipFileBuffer(outPath, listFiles);
    }

    /**
     * 使用Channel压缩文件
     *
     * @param filePath 文件路径
     * @param outPath 输出路径
     */
    public static void zipFileChannel(String filePath, String outPath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            zipDirectoryChannel(filePath, outPath);
            return;
        }
        zipFileChannel(outPath, Sets.newHashSet(file));
    }

    /**
     * 使用Channel压缩目录
     *
     * @param filePath 目录路径
     * @param outPath 输出路径
     */
    public static void zipDirectoryChannel(String filePath, String outPath) {
        File file = new File(filePath);
        Collection<File> listFiles = FileUtils.listFiles(file, null, true);
        if (CollectionUtils.isEmpty(listFiles)) {
            return;
        }
        zipFileChannel(outPath, listFiles);
    }

    /**
     * 使用Channel压缩
     *
     * @param outPath 输出路径
     * @param files 文件列表
     */
    private static void zipFileChannel(String outPath, Collection<File> files) {
        try {
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outPath));
            WritableByteChannel writableByteChannel = Channels.newChannel(zipOut);
            for (File file : files) {
                try (FileChannel fileChannel = new FileInputStream(file).getChannel()) {
                    zipOut.putNextEntry(new ZipEntry(file.getPath()));
                    fileChannel.transferTo(0, file.length(), writableByteChannel);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用Map映射文件
     * @param filePath 文件路径
     * @param outPath 输出路径
     */
    public static void zipFileMap(String filePath, String outPath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            zipFileDirectoryMap(filePath, outPath);
            return;
        }
        zipFileMap(outPath, Sets.newHashSet(file));
    }

    public static void zipFileDirectoryMap(String filePath, String outPath) {
        File file = new File(filePath);
        Collection<File> listFiles = FileUtils.listFiles(file, null, true);
        if (CollectionUtils.isEmpty(listFiles)) {
            return;
        }
        zipFileMap(outPath, listFiles);
    }

    private static void zipFileMap(String outPath, Collection<File> files) {
        try {
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outPath));
            WritableByteChannel writableByteChannel = Channels.newChannel(zipOut);
            for (File file : files) {
                zipOut.putNextEntry(new ZipEntry(file.getPath()));
                // 内存中的映射文件
                MappedByteBuffer mappedByteBuffer = new RandomAccessFile(file.getPath(), "r").getChannel()
                        .map(FileChannel.MapMode.READ_ONLY, 0, file.length());
                writableByteChannel.write(mappedByteBuffer);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipFilePip(String filePath, String outPath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            zipFileDirectoryPip(filePath, outPath);
        }
        zipFilePip(outPath, Sets.newHashSet(file));
    }

    public static void zipFileDirectoryPip(String filePath, String outPath) {
        File file = new File(filePath);
        Collection<File> listFiles = FileUtils.listFiles(file, null, true);
        zipFilePip(outPath, listFiles);
    }

    private static void zipFilePip(String outPath, Collection<File> files) {
        try (WritableByteChannel out = Channels.newChannel(new FileOutputStream(outPath))) {
            Pipe pipe = Pipe.open();
            // 异步任务放入数据
            CompletableFuture.runAsync(() -> runTask(files, pipe));
            // 获取读通道
            ReadableByteChannel readableByteChannel = pipe.source();
            for (File file : files) {
                ByteBuffer buffer = ByteBuffer.allocate(((int)file.length()) * 10);
                while (readableByteChannel.read(buffer) >= 0) {
                    buffer.flip();
                    out.write(buffer);
                    buffer.clear();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用Pip+Map
     */
    public static void zipFilePipMap(String filePath, String outPath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            zipFileDirectoryPipMap(filePath, outPath);
        }
        zipFilePipMap(outPath, Sets.newHashSet(file));
    }

    public static void zipFileDirectoryPipMap(String filePath, String outPath) {
        File file = new File(filePath);
        Collection<File> listFiles = FileUtils.listFiles(file, null, true);
        zipFilePipMap(outPath, listFiles);
    }

    private static void zipFilePipMap(String outPath, Collection<File> files) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(outPath);
             WritableByteChannel out = Channels.newChannel(fileOutputStream)) {
            Pipe pipe = Pipe.open();
            // 异步任务往通道中塞入数据
            CompletableFuture.runAsync(() -> runTaskMap(files, pipe));
            // 读取数据
            ReadableByteChannel workerChannel = pipe.source();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (workerChannel.read(buffer) >= 0) {
                buffer.flip();
                out.write(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 异步任务
     */
    public static void runTask(Collection<File> files, Pipe pipe) {
        try (ZipOutputStream zos = new ZipOutputStream(Channels.newOutputStream(pipe.sink()));
             WritableByteChannel out = Channels.newChannel(zos)) {
            for (File file : files) {
                zos.putNextEntry(new ZipEntry(file.getPath()));
                FileChannel fileChannel = new FileInputStream(file).getChannel();
                fileChannel.transferTo(0, file.length(), out);
                fileChannel.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 异步任务
    public static void runTaskMap(Collection<File> files, Pipe pipe) {
        try (WritableByteChannel channel = pipe.sink();
             ZipOutputStream zos = new ZipOutputStream(Channels.newOutputStream(channel));
             WritableByteChannel out = Channels.newChannel(zos)) {
            for (File file : files) {
                zos.putNextEntry(new ZipEntry(file.getPath()));
                MappedByteBuffer mappedByteBuffer = new RandomAccessFile(file.getPath(), "r").getChannel()
                        .map(FileChannel.MapMode.READ_ONLY, 0, file.length());
                out.write(mappedByteBuffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printInfo(long beginTime, long fileSize) {
        // 耗时
        long timeConsum = (System.currentTimeMillis() - beginTime);
        System.out.println("fileSize:" + fileSize);
        System.out.println("consum time:" + timeConsum);
    }

}
