package com.luna.common.text;

import com.luna.common.constant.Constant;
import com.luna.common.constant.StrPoolConstant;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * 随机字符串
 * 
 * @author luna
 */
public class RandomStrUtil {

    private static final String SYMBOLS = Constant.SYMBOLS;

    private static final Random RANDOM  = new SecureRandom();

    public static String generateNonceStrWithUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll(StrPoolConstant.DASHED, StrPoolConstant.EMPTY).toUpperCase();
    }

    /**
     * 获取随机字符串 Nonce Str
     *
     * @return String 随机字符串
     */
    public static String generateNonceStr() {
        char[] nonceChars = new char[32];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }

    /**
     * 获取6位随机验证码
     *
     * @return
     */
    public static String getValidationCode() {
        return String.valueOf((new Random().nextInt(899999) + 100000));
    }
}
