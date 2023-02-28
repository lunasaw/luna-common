package com.luna.common.dto.constant;

import lombok.Getter;

/**
 * @author chenzhangyue
 * 2023/2/28
 */
@Getter
public enum ResultEnum {

    SUCCESS(ResultCode.SUCCESS, ResultCode.MSG_SUCCESS),

    PARAMETER_INVALID(ResultCode.PARAMETER_INVALID, ResultCode.MSG_PARAMETER_INVALID),

    INTERFACE_OFFLINE(ResultCode.INTERFACE_OFFLINE, ResultCode.MSG_INTERFACE_OFFLINE)
    ;

    private int errorCode;

    private String errorMsg;




    ResultEnum(Integer code, String msg) {
        this.errorCode = code;
        this.errorMsg = msg;
    }
}
