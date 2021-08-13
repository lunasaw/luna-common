package com.luna.common.net;

/**
 * @author chenzhangyue@weidian.com
 * 2021/8/13
 */
public enum HttpContentTypeEnum {

    /** JSON */
    CONTENT_TYPE_JSON(1, "content-type", HttpUtilsConstant.JSON),
    /** form-urlencoded */
    CONTENT_TYPE_X_WWW_FORM_URLENCODED(2, "content-type", HttpUtilsConstant.X_WWW_FORM_URLENCODED),
    /** form-data */
    CONTENT_TYPE_FORM_DATA(3, "content-type", HttpUtilsConstant.FORM_DATA),
    /** msexcel */
    CONTENT_TYPE_MSEXCEL(4, "content-type", HttpUtilsConstant.MSEXCEL),
    /** text */
    CONTENT_TYPE_TEXT(5, "content-type", HttpUtilsConstant.TEXT),
    /** octet-stream */
    CONTENT_OCTET_STREAM(6, "content-type", HttpUtilsConstant.OCTET_STREAM);

    /** 编号 */
    private Integer code;
    /** k */
    private String  key;
    /** v */
    private String  value;

    HttpContentTypeEnum(Integer code, String key, String value) {
        this.code = code;
        this.key = key;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
