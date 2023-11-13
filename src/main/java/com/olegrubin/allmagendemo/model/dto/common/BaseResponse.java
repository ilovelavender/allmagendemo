package com.olegrubin.allmagendemo.model.dto.common;

public abstract class BaseResponse<T> {

    private final boolean success;

    private final T data;

    protected BaseResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }
}
