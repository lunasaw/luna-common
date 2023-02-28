package com.luna.common.encrypt;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AesUtil {

    private static final Logger LOG = LoggerFactory.getLogger(AesUtil.class);

    public static final String CHARSET = "UTF-8";
    public static final String KEY_ALGIROTHM = "AesUtil";
    public static final String CIPHER_ALGIROTHM = "AesUtil/CBC/PKCS5Padding";
    public static final String DEFAULT_SECRET_KEY = "uBdUx82vPHkDKb284d7NkjFoNcKWBuka";
    /**
     * 加密(UTF8)
     * @param key     密码
     * @param content 待加密内容
     * @return base64处理后的密文
     */
    public static String encrypt(String key, String content) {
        try {
            Key keySpec = new SecretKeySpec(key.getBytes(CHARSET), KEY_ALGIROTHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGIROTHM);
            byte[] iv = new byte[cipher.getBlockSize()];
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParams);
            byte[] encryptByte = cipher.doFinal(content.getBytes(CHARSET));
            return Base64.encodeBase64String(encryptByte);
        } catch (Exception e) {
            LOG.error("加密失败.", e);
        }
        return null;
    }

    /**
     * 解密(UTF8)
     * @param key       密码
     * @param encrypted 密文
     * @return 原文
     */
    public static String decrypt(String key, String encrypted) {
        byte[] encryptBytes = Base64.decodeBase64(encrypted);
        try {
            Key keySpec = new SecretKeySpec(key.getBytes(CHARSET), KEY_ALGIROTHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGIROTHM);
            byte[] ivByte = new byte[cipher.getBlockSize()];
            IvParameterSpec ivParamsSpec = new IvParameterSpec(ivByte);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamsSpec);
            byte[] content = cipher.doFinal(encryptBytes);
            return new String(content, CHARSET);
        } catch (Exception e) {
            LOG.error("解密失败.", e);
        }
        return null;
    }

    public static void main(String[] args) {
        String dbPassword = "10090909091";
        String encryptDbPwd = AesUtil.encrypt(DEFAULT_SECRET_KEY, dbPassword);
        System.out.println("encrypt: " + encryptDbPwd);
        System.out.println("encrypt length: " + encryptDbPwd.length());
        String decrypt = AesUtil.decrypt(DEFAULT_SECRET_KEY, encryptDbPwd);
        System.out.println("decrypt:" + decrypt);
    }
}
