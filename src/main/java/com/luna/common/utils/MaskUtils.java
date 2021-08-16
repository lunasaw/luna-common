package com.luna.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Luna
 */
public class MaskUtils {
    /** 中国大陆手机号正则 */
    private static final String CHINA_MAINLAND_MOBILE_PHONE_REGEX =
        "^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$";

    /**
     * 邮箱有效性正则
     */
    private static final String EMAIL_PHONE_REGEX                 =
        "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /** 11位整数支持 */
    private static final String ELEVEN_NUMBER                     = "\\d{11}";

    /**
     * 判断外标是不是个邮箱
     *
     * @param email 邮箱
     * @return
     */
    public static boolean isEmailAddress(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    /**
     * 判断是不是邮箱.
     *
     * @param email 邮箱
     */
    public static boolean isEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        return email.matches(EMAIL_PHONE_REGEX);
    }

    /**
     * 判断是否是手机号
     * <p>
     * 目前只支持中国大陆手机号
     * </p>
     * 
     * @param telephone 手机号
     * @return
     */
    public static boolean isMobilePhoneNumber(String telephone) {
        if (StringUtils.isBlank(telephone)) {
            return false;
        }
        return telephone.matches(CHINA_MAINLAND_MOBILE_PHONE_REGEX);
    }

    /**
     * 手机号判断11位
     * 
     * @param telephone 手机号
     * @return
     */
    public static boolean checkPhone(String telephone) {
        if (StringUtils.isBlank(telephone)) {
            return false;
        }
        return telephone.matches(ELEVEN_NUMBER);
    }
}
