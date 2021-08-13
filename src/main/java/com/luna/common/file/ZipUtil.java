package com.luna.common.file;


import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author luna
 */
public class ZipUtil {

    private static final int BUFFER_SIZE = 1024 * 5;



    /**
     * 压缩成ZIP 方法1
     * 
     * @param files 需要压缩的文件集合
     * @param zipFilePath 压缩文件路径
     * @throws Exception
     */
    public static void toZip(List<File> files, String zipFilePath) {
        ZipOutputStream zos = null;
        try {
            OutputStream out = new FileOutputStream(zipFilePath);
            zos = new ZipOutputStream(out);
            for (File file : files) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(file.getName()));
                FileInputStream in = new FileInputStream(file);
                int len;
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 压缩成ZIP 方法2
     * 
     * @param fileDirPath 压缩文件夹路径
     * @param zipFilePath 压缩文路径
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;false:所有文件跑到压缩包根目录下(注意：
     * 不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    public static void toZip(String fileDirPath, String zipFilePath, boolean keepDirStructure) {
        ZipOutputStream zos = null;
        try {
            OutputStream out = new FileOutputStream(zipFilePath);
            zos = new ZipOutputStream(out);
            File dirFile = new File(fileDirPath);
            compress(dirFile, zos, dirFile.getName(), keepDirStructure);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * 递归压缩方法
     * 
     * @param sourceFile 源文件
     * @param zos zip输出流
     * @param name 压缩后的文件夹/文件名称
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     * false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    public static void compress(File sourceFile, ZipOutputStream zos, String name, boolean keepDirStructure)
        throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            ZipEntry zipEntry = new ZipEntry(name);
            zos.putNextEntry(zipEntry);
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (keepDirStructure) {
                    // 空文件夹的处理
                    ZipEntry zipEntry = new ZipEntry(name + "/");
                    zos.putNextEntry(zipEntry);
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (keepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), keepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), keepDirStructure);
                    }
                }
            }
        }
    }
}