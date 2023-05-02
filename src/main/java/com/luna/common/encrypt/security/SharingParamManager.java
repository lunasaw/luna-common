package com.luna.common.encrypt.security;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;
import com.luna.common.constant.StrPoolConstant;
import com.luna.common.net.HttpUtils;
import com.luna.common.text.CharsetUtil;

/**
 * @author luna
 */
public class SharingParamManager {

    /**
     * 当前协议默认的版本号
     */
    public final static String CURRENT_DEFAULT_VERSION = "00";

    private static final int   MIN_LENGTH              = 3;

    private static final int   MAX_LENGTH              = 4;

    /**
     * 协议版本号
     */
    private String             version;

    private String[]           param;

    /**
     * 依赖的基建接口
     */
    private SecurityManager    securityManager;

    public SharingParamManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public SharingParamManager(SecurityManager securityManager, String version, String... param) {
        this.version = version;
        this.param = param;
        this.securityManager = securityManager;
    }

    public SharingParamManager() {}

    /**
     * 生成分享参数
     * 如果分享者为null，分享参数为null
     *
     * @return
     */
    public String generate() {
        if (ObjectUtils.isEmpty(param)) {
            return null;
        }

        // 拼接字符串
        String plaintext = Joiner.on(StrPoolConstant.UNDERLINE).join(param);

        // 生成校验和
        String checkSum = securityManager.buildCheckSum(plaintext);
        // 报文加密
        String encrypted = securityManager.encrypt(plaintext);
        // 做urlencode
        String body = HttpUtils.urlEncode(encrypted + StrPoolConstant.DASHED + checkSum, CharsetUtil.UTF_8);

        return body;
    }

    /**
     * 解析分享参数
     *
     * @return true--校验成功，否则校验失败
     */
    public boolean parse(String body) {
        if (StringUtils.isBlank(body)) {
            return false;
        }

        try {
            String decode = URLDecoder.decode(body, CharsetUtil.UTF_8);
            int index = StringUtils.lastIndexOf(decode, StrPoolConstant.DASHED);
            if (-1 == index) {
                return false;
            }
            String[] array = new String[] {decode.substring(0, index), decode.substring(index + 1)};

            String decrypted = securityManager.decrypt(array[0]);
            String respectiveCheckSum = securityManager.buildCheckSum(decrypted);

            // 校验和不匹配，说明被篡改
            if (!respectiveCheckSum.equals(array[1])) {
                return false;
            }

            String[] elements =
                StringUtils.splitByWholeSeparatorPreserveAllTokens(decrypted, StrPoolConstant.UNDERLINE);

            if (StringUtils.isBlank(version)) {
                return false;
            }

            this.setVersion(version);
            this.param = elements;

            return true;
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getParam() {
        return param;
    }

    public void setParam(String[] param) {
        this.param = param;
    }
}
