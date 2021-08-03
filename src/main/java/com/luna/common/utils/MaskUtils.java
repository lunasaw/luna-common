package com.luna.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Luna
 */
public class MaskUtils {
    /** 中国大陆手机号正则 */
    private static final String  CHINA_MAINLAND_MOBILE_PHONE_REGEX = "0?(13|14|15|17|18|19)[0-9]{9}";

    /** 11位整数支持 */
    private static final Pattern PHONE_PATTERN                     =
        Pattern.compile("\\d{11}", Pattern.CASE_INSENSITIVE);

    /**
     * 判断外标是不是个邮箱
     *
     * @param outUser
     * @return
     */
    public static boolean isEmailAddress(String outUser) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(outUser);
    }

    /**
     * 判断是否是手机号
     * <p>
     * 目前只支持中国大陆手机号
     * </p>
     * 
     * @param input
     * @return
     */
    public static boolean isMobilePhoneNumber(String input) {
        if (StringUtils.isBlank(input)) {
            return false;
        }
        return input.matches(CHINA_MAINLAND_MOBILE_PHONE_REGEX);
    }

    /**
     * 手机号判断11位
     * 
     * @param telephone
     * @return
     */
    public static boolean checkPhone(String telephone) {
        if (StringUtils.isBlank(telephone)) {
            return false;
        }
        Matcher matcher = PHONE_PATTERN.matcher(telephone);
        return matcher.matches();
    }
}
