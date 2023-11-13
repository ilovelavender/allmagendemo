package com.olegrubin.allmagendemo.model.dto.common;

public class SuccessResponse<T> extends BaseResponse<T> {

    public SuccessResponse(T data) {
        super(true, data);
    }
}
