package com.luna.common.file;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author luna
 * 2021/6/13
 */
public class FileNameUtils {
    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();
    static {
        getAllFileType();
        // 初始化文件类型信息
    }

    /**
     * 将常见文件类型放入到map中
     */
    private static void getAllFileType() {
        FILE_TYPE_MAP.put("jpg", "FFD8FF");
        // JPEG (jpg)
        FILE_TYPE_MAP.put("png", "89504E47");
        // PNG (png)
        FILE_TYPE_MAP.put("gif", "47494638");
        // GIF (gif)
        FILE_TYPE_MAP.put("tif", "49492A00");
        // TIFF (tif)
        FILE_TYPE_MAP.put("bmp", "424D");
        // Windows Bitmap (bmp)
        FILE_TYPE_MAP.put("dwg", "41433130");
        // CAD (dwg)
        FILE_TYPE_MAP.put("html", "68746D6C3E");
        // HTML (html)
        FILE_TYPE_MAP.put("rtf", "7B5C727466");
        // Rich Text Format (rtf)
        FILE_TYPE_MAP.put("xml", "3C3F786D6C");
        FILE_TYPE_MAP.put("zip", "504B0304");
        FILE_TYPE_MAP.put("rar", "52617221");
        FILE_TYPE_MAP.put("psd", "38425053");
        // Photoshop (psd)
        FILE_TYPE_MAP.put("eml", "44656C69766572792D646174653A");
        // Email
        FILE_TYPE_MAP.put("dbx", "CFAD12FEC5FD746F");
        // Outlook Express (dbx)
        FILE_TYPE_MAP.put("pst", "2142444E");
        // Outlook (pst)
        FILE_TYPE_MAP.put("xls", "D0CF11E0");
        // MS Word
        FILE_TYPE_MAP.put("doc", "D0CF11E0");
        // MS Excel 注意：word 和 excel的文件头一样
        FILE_TYPE_MAP.put("mdb", "5374616E64617264204A");
        // MS Access (mdb)
        FILE_TYPE_MAP.put("wpd", "FF575043");
        // WordPerfect (wpd)
        FILE_TYPE_MAP.put("eps", "252150532D41646F6265");
        FILE_TYPE_MAP.put("ps", "252150532D41646F6265");
        FILE_TYPE_MAP.put("pdf", "255044462D312E");
        // Adobe Acrobat (pdf)
        FILE_TYPE_MAP.put("qdf", "AC9EBD8F");
        // Quicken (qdf)
        FILE_TYPE_MAP.put("pwl", "E3828596");
        // Windows Password (pwl)
        FILE_TYPE_MAP.put("wav", "57415645");
        // Wave (wav)
        FILE_TYPE_MAP.put("avi", "41564920");
        FILE_TYPE_MAP.put("ram", "2E7261FD");
        // Real Audio (ram)
        FILE_TYPE_MAP.put("rm", "2E524D46");
        // Real Media (rm)
        FILE_TYPE_MAP.put("mpg", "000001BA");
        //
        FILE_TYPE_MAP.put("mov", "6D6F6F76");
        // Quicktime (mov)
        FILE_TYPE_MAP.put("asf", "3026B2758E66CF11");
        // Windows Media (asf)
        FILE_TYPE_MAP.put("mid", "4D546864");
        // MIDI (mid)
    }

    /**
     * 获取文件类型,包括图片,若格式不是已配置的,则返回null
     */
    public static String getFileTypeByFile(File file) {
        try {
            byte[] b = new byte[50];
            InputStream is = new FileInputStream(file);
            is.read(b);
            String filetype = getFileTypeByStream(b);
            is.close();
            return filetype;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过字节流获得文件类型
     */
    public static String getFileTypeByStream(byte[] b) {
        String filetypeHex = String.valueOf(getFileHexString(b));
        for (Map.Entry<String, String> entry : FILE_TYPE_MAP
            .entrySet()) {
            String fileTypeHexValue = entry.getValue();
            if (filetypeHex.toUpperCase().startsWith(fileTypeHexValue)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 获得文件的16进制数据
     */
    public static String getFileHexString(byte[] b) {
        StringBuilder stringBuilder = new StringBuilder();
        if (b == null || b.length <= 0) {
            return null;
        }
        for (byte value : b) {
            int v = value & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
