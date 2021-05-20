package com.luna.common.dto;

import com.luna.common.dto.constant.ResultCode;

/**
 * @author Luna
 */
public class ResultDTOUtils<T> {
    public static <T> T checkResultAndGetData(ResultDTO<T> resultDTO) {
        if (resultDTO.isSuccess() == false) {
            throw new RuntimeException("code=" + resultDTO.getCode() + ", message=" + resultDTO.getMessage());
        }
        return resultDTO.getData();
    }

    public static ResultDTO<Void> success() {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS);
    }

    public static ResultDTO<?> success(Object object) {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, object);
    }

    public static ResultDTO<Void> failure() {
        return new ResultDTO<>(false, ResultCode.ERROR_SYSTEM_EXCEPTION, ResultCode.MSG_ERROR_SYSTEM_EXCEPTION);
    }

    public static ResultDTO<Void> failure(int code, String message) {
        return new ResultDTO<>(false, code, message);
    }

    public static ResultDTO<Object> failure(int code, String message, Object object) {
        return new ResultDTO<>(false, code, message, object);
    }
}
