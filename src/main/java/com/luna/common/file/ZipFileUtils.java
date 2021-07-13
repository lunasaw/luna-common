package com.luna.common.file;

import com.luna.common.text.ObjectUtils;
import org.apache.commons.collections.CollectionUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author chenzhangyue@weidian.com
 * 2021/7/13
 */
public class ZipFileUtils {

    /**
     * 没有使用Buffer
     */
    public static void zipFileNoBuffer(String filePath, String outPath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            zipDirectoryNoBuffer(filePath, outPath);
        }
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outPath))) {
            // 开始时间
            long beginTime = System.currentTimeMillis();
            try (InputStream input = new FileInputStream(file)) {
                zipOut.putNextEntry(new ZipEntry(file.getName()));
                int temp = 0;
                while ((temp = input.read()) != -1) {
                    zipOut.write(temp);
                }
            }
            printInfo(beginTime, new File(outPath).length());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipDirectoryNoBuffer(String filePath, String outPath) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        if (ObjectUtils.isEmpty(files)) {
            return;
        }
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outPath))) {
            // 开始时间
            long beginTime = System.currentTimeMillis();
            for (File fileTemp : files) {
                try (InputStream input = new FileInputStream(fileTemp)) {
                    zipOut.putNextEntry(new ZipEntry(fileTemp.getName()));
                    int temp = 0;
                    while ((temp = input.read()) != -1) {
                        zipOut.write(temp);
                    }
                }
            }
            printInfo(beginTime, new File(outPath).length());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用了Buffer
     */
    public static void zipFileBuffer(String filePath, String outPath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            zipDirectoryBuffer(filePath, outPath);
        }
        long beginTime = System.currentTimeMillis();
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outPath));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zipOut)) {
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath))) {
                zipOut.putNextEntry(new ZipEntry(filePath));
                int temp = 0;
                while ((temp = bufferedInputStream.read()) != -1) {
                    bufferedOutputStream.write(temp);
                }
            }
            printInfo(beginTime, new File(outPath).length());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipDirectoryBuffer(String filePath, String outPath) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        long beginTime = System.currentTimeMillis();
        if (ObjectUtils.isEmpty(files)) {
            return;
        }
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outPath));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zipOut)) {
            for (File tempFile : files) {
                try (BufferedInputStream bufferedInputStream =
                    new BufferedInputStream(new FileInputStream(tempFile))) {
                    zipOut.putNextEntry(new ZipEntry(tempFile.getPath()));
                    int temp = 0;
                    while ((temp = bufferedInputStream.read()) != -1) {
                        bufferedOutputStream.write(temp);
                    }
                }
            }
            printInfo(beginTime, new File(outPath).length());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用Channel
     */
    public static void zipFileChannel(String filePath, String outPath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            zipDirectoryChannel(filePath, outPath);
        }
        long beginTime = System.currentTimeMillis();
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outPath));
            WritableByteChannel writableByteChannel = Channels.newChannel(zipOut)) {
            try (FileChannel fileChannel = new FileInputStream(filePath).getChannel()) {
                zipOut.putNextEntry(new ZipEntry(filePath));
                fileChannel.transferTo(0, file.length(), writableByteChannel);
            }
            printInfo(beginTime, new File(outPath).length());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipDirectoryChannel(String filePath, String outPath) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        if (ObjectUtils.isEmpty(files)) {
            return;
        }
        long beginTime = System.currentTimeMillis();
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outPath));
            WritableByteChannel writableByteChannel = Channels.newChannel(zipOut)) {
            for (File tempFile : files) {
                try (FileChannel fileChannel = new FileInputStream(tempFile).getChannel()) {
                    zipOut.putNextEntry(new ZipEntry(tempFile.getPath()));
                    fileChannel.transferTo(0, file.length(), writableByteChannel);
                }
            }
            printInfo(beginTime, new File(outPath).length());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用Map映射文件
     */
    public static void zipFileMap(String filePath, String outPath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            zipDirectoryMap(filePath, outPath);
        }
        // 开始时间
        long beginTime = System.currentTimeMillis();
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outPath));
            WritableByteChannel writableByteChannel = Channels.newChannel(zipOut)) {
            zipOut.putNextEntry(new ZipEntry(file.getPath()));

            // 内存中的映射文件
            MappedByteBuffer mappedByteBuffer = new RandomAccessFile(filePath, "r").getChannel()
                .map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            writableByteChannel.write(mappedByteBuffer);
            printInfo(beginTime, new File(outPath).length());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipDirectoryMap(String filePath, String outPath) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        if (ObjectUtils.isEmpty(files)) {
            return;
        }
        long beginTime = System.currentTimeMillis();
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outPath));
            WritableByteChannel writableByteChannel = Channels.newChannel(zipOut)) {
            for (File tempFile : files) {
                zipOut.putNextEntry(new ZipEntry(tempFile.getPath()));

                // 内存中的映射文件
                MappedByteBuffer mappedByteBuffer = new RandomAccessFile(tempFile.getPath(), "r").getChannel()
                    .map(FileChannel.MapMode.READ_ONLY, 0, tempFile.length());
                writableByteChannel.write(mappedByteBuffer);
            }
            printInfo(beginTime, new File(outPath).length());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipFileOrDirectoryPip(String filePath, String outPath) {
        File file = new File(filePath);
        File[] files = new File[] {};
        if (!file.isFile()) {
            files = file.listFiles();
        } else {
            files[0] = file;
        }
        long beginTime = System.currentTimeMillis();
        try (WritableByteChannel out = Channels.newChannel(new FileOutputStream(outPath))) {
            Pipe pipe = Pipe.open();
            // 异步任务
            File[] finalFiles = files;
            CompletableFuture.runAsync(() -> runTask(finalFiles, pipe));
            // 获取读通道
            ReadableByteChannel readableByteChannel = pipe.source();
            for (File tempFile : files) {
                ByteBuffer buffer = ByteBuffer.allocate(((int)tempFile.length()) * 10);
                while (readableByteChannel.read(buffer) >= 0) {
                    buffer.flip();
                    out.write(buffer);
                    buffer.clear();
                }
            }
            printInfo(beginTime, new File(outPath).length());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用Pip+Map
     */
    public static void zipFileOrDirectoryPipMap(String filePath, String outPath) {
        File file = new File(filePath);
        File[] files = new File[] {};
        if (!file.isFile()) {
            files = file.listFiles();
        } else {
            files[0] = file;
        }
        long beginTime = System.currentTimeMillis();
        try (FileOutputStream fileOutputStream = new FileOutputStream(outPath);
            WritableByteChannel out = Channels.newChannel(fileOutputStream)) {
            Pipe pipe = Pipe.open();
            // 异步任务往通道中塞入数据
            File[] finalFiles = files;
            CompletableFuture.runAsync(() -> runTaskMap(finalFiles, pipe));
            // 读取数据
            ReadableByteChannel workerChannel = pipe.source();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (workerChannel.read(buffer) >= 0) {
                buffer.flip();
                out.write(buffer);
                buffer.clear();
            }
            printInfo(beginTime, new File(outPath).length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        zipFileOrDirectoryPipMap("/Users/luna/weidian/www/fx-mall", "fx-mall.zip");
    }

    /**
     * 异步任务
     */
    public static void runTask(File[] files, Pipe pipe) {
        try (ZipOutputStream zos = new ZipOutputStream(Channels.newOutputStream(pipe.sink()));
            WritableByteChannel out = Channels.newChannel(zos)) {
            for (File file : files) {
                zos.putNextEntry(new ZipEntry(file.getPath()));
                FileChannel jpgChannel = new FileInputStream(file).getChannel();
                jpgChannel.transferTo(0, file.length(), out);
                jpgChannel.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 异步任务
    public static void runTaskMap(File[] files, Pipe pipe) {
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
        System.out.println("fileSize:" + fileSize / 1024 / 1024 * 10 + "M");
        System.out.println("consum time:" + timeConsum);
    }

}
