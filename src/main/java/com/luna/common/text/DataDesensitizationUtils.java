package com.luna.common.text;


/**
 * @author luna
 */
public class DataDesensitizationUtils {

    private static final int MAX_LENGTH = 11;

    private static final int ID_LENGTH  = 8;

    /**
     * 手机号码前三后四脱敏
     * 
     * @param mobile 手机号
     * @return String
     */
    public static String mobileEncrypt(String mobile) {
        if (StringTools.isEmpty(mobile) || (mobile.length() != MAX_LENGTH)) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 身份证前三后四脱敏
     * 
     * @param id 身份证号
     * @return String
     */
    public static String idEncrypt(String id) {
        if (StringTools.isEmpty(id) || (id.length() < ID_LENGTH)) {
            return id;
        }
        return id.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
    }

    /**
     * 护照前2后3位脱敏，护照一般为8或9位
     * 
     * @param id 护照号
     * @return String
     */
    public static String idPassport(String id) {
        if (StringTools.isEmpty(id) || (id.length() < ID_LENGTH)) {
            return id;
        }
        return id.substring(0, 2) + new String(new char[id.length() - 5]).replace("\0", "*")
            + id.substring(id.length() - 3);
    }

}