package com.luna.common.dto;

import java.io.Serializable;

import com.luna.common.dto.constant.ResultCode;

/**
 * 返回值
 * 
 * @author luna
 *
 * @param <T>
 */
public class ResultDTO<T> implements Serializable {

    /** 是否调用以及过参数校验 */
    private Boolean success = false;
    /** 业务code，见{@link ResultCode} */
    private Integer code    = 0;
    /** 业务消息，见{@link ResultCode} */
    private String  message;
    /** 返回值 */
    private T       data;

    public ResultDTO() {}

    public ResultDTO(boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public ResultDTO(boolean success, Integer code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultDTO{" +
            "success=" + success +
            ", code=" + code +
            ", message='" + message + '\'' +
            ", data=" + data +
            '}';
    }
}
