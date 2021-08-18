package com.luna.common.encrypt.security;

import com.luna.common.constant.Constant;
import com.luna.common.constant.StrPoolConstant;
import com.luna.common.encrypt.EncryptUtils;

/**
 * @author luna
 */
public class SecurityManager {

    /**
     * 校验和的前缀，"盐"
     */
    private final static String CHECKSUM_PREFIX = "luna";

    /**
     * 校验和的后缀，"盐"
     */
    private final static String CHECKSUM_POSTFIX = "qwp";

    /**
     * 对称加解密的秘钥
     */
    private final static String SECRET_KEY = "3Td!O9d=P1a2423";

    public String buildCheckSum(String content) {
        String checkSum = EncryptUtils.md5Checksum(CHECKSUM_PREFIX, CHECKSUM_POSTFIX, content);
        return checkSum.replace(StrPoolConstant.DASHED, StrPoolConstant.EMPTY);
    }

    public String encrypt(String content) {
        return Blowfish.encrypt(content, SECRET_KEY);
    }

    public String decrypt(String content) {
        return Blowfish.decrypt(content, SECRET_KEY);
    }
}
