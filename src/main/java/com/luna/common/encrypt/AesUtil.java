package com.luna.common.encrypt;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AesUtil {

    public static final String  CHARSET            = "UTF-8";
    public static final String  CIPHER_ALGIROTHM   = "AesUtil/CBC/PKCS5Padding";

    private static final String ALGORITHM        = "AES";
    private static final int    KEY_SIZE         = 128;

    public static String generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE);
            SecretKey secretKey = keyGenerator.generateKey();
            // 将密钥转换为字符串
            String key = Base64Util.encodeBase64(secretKey.getEncoded());
            return key;
        } catch (NoSuchAlgorithmException e) {
            log.error("generateKey:: ", e);
            return null;
        }
    }

    /**
     * 加密(UTF8)
     * 
     * @param key 密码
     * @param data 待加密内容
     * @return base64处理后的密文
     */
    public static String encrypt(String data, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(CHARSET), ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGIROTHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(CHARSET));
            return Base64Util.encodeBase64(encryptedBytes);
        } catch (Exception e) {
            log.error("encrypt::data = {}, key = {} ", data, key, e);
            return null;
        }
    }

    /**
     * 解密(UTF8)
     * 
     * @param key 密码
     * @param encryptedData 密文
     * @return 原文
     */
    public static String decrypt(String encryptedData, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(CHARSET), ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGIROTHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = Base64Util.decodeBase64(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, CHARSET);
        } catch (Exception e) {
            log.error("decrypt::encryptedData = {}, key = {} ", encryptedData, key, e);
            return null;
        }
    }

    public static void main(String[] args) {
        String key = generateKey();
        String dbPassword = "10090909091";
        String encryptDbPwd = AesUtil.encrypt(key, dbPassword);
        System.out.println("encrypt: " + encryptDbPwd);
        System.out.println("encrypt length: " + encryptDbPwd.length());
        String decrypt = AesUtil.decrypt(key, encryptDbPwd);
        System.out.println("decrypt:" + decrypt);
    }
}
