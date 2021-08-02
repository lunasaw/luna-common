package com.luna.common.encrypt.security;

import com.google.common.base.Preconditions;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class Blowfish {
    public static final String CIPHER_NAME = "Blowfish/CFB8/NoPadding";
    public static final String CHARSET     = "GBK";
    public static final int    KEY_LENGTH  = 8;

    public static String encrypt(String input, String key) {
        try {
            return encrypt(input.getBytes(CHARSET), key.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String input, String key) {
        try {
            return decrypt(input.getBytes(CHARSET), key.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(byte[] input, byte[] key) {
        Preconditions.checkNotNull(input, "input can't be null");
        Preconditions.checkNotNull(key, "key can't be null");

        Cipher cipher = newCipher(Cipher.ENCRYPT_MODE, key);

        try {
            byte[] enBytes = cipher.doFinal(input);
            byte[] base64Bytes = Base64.encodeBase64URLSafe(enBytes);

            return new String(base64Bytes, CHARSET);
        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(byte[] input, byte[] key) {
        Preconditions.checkNotNull(input, "input can't be null");
        Preconditions.checkNotNull(key, "key can't be null");

        Cipher cipher = newCipher(Cipher.DECRYPT_MODE, key);

        try {
            byte[] base64Bytes = Base64.decodeBase64(input);
            byte[] deBytes = cipher.doFinal(base64Bytes);

            return new String(deBytes, CHARSET);
        } catch (BadPaddingException | UnsupportedEncodingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    private static IvParameterSpec generateParamSpec(byte[] key) {
        String md5Key = DigestUtils.md5Hex(key);
        byte[] keyBytes = md5Key.getBytes();

        byte[] ivKeyBytes = new byte[KEY_LENGTH];
        System.arraycopy(keyBytes, 0, ivKeyBytes, 0, KEY_LENGTH);

        return new IvParameterSpec(ivKeyBytes);
    }

    private static Cipher newCipher(int mode, byte[] key) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "Blowfish");
        IvParameterSpec ivParameterSpec = generateParamSpec(key);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(mode, secretKeySpec, ivParameterSpec);

            return cipher;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
