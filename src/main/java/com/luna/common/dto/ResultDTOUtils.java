package com.luna.common.dto;

import java.util.Objects;

import com.luna.common.dto.constant.ResultCode;

/**
 * @author Luna
 */
public class ResultDTOUtils<T> {

    public static <T> T checkResultAndGetData(ResultDTO<T> resultDTO) {
        if (Objects.isNull(resultDTO) || !resultDTO.isSuccess()) {
            throw new RuntimeException("code=" + resultDTO.getCode() + ", message=" + resultDTO.getMessage());
        }
        return resultDTO.getData();
    }

    public static ResultDTO<Void> success() {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS);
    }

    public static <T> ResultDTO<T> success(T object) {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, object);
    }

    public static ResultDTO<Void> success(int code, String message) {
        return new ResultDTO<>(true, code, message);
    }

    public static <T> ResultDTO<T> success(int code, String message, T object) {
        return new ResultDTO<>(true, code, message, object);
    }

    public static <T> ResultDTO<T> failure(T object) {
        return new ResultDTO<>(false, ResultCode.ERROR_SYSTEM_EXCEPTION, ResultCode.MSG_ERROR_SYSTEM_EXCEPTION, object);
    }

    public static ResultDTO<Void> failure() {
        return new ResultDTO<>(false, ResultCode.ERROR_SYSTEM_EXCEPTION, ResultCode.MSG_ERROR_SYSTEM_EXCEPTION);
    }

    public static ResultDTO<Void> failure(int code, String message) {
        return new ResultDTO<>(false, code, message);
    }

    public static <T> ResultDTO<T> failure(int code, String message, T object) {
        return new ResultDTO<>(false, code, message, object);
    }
}
