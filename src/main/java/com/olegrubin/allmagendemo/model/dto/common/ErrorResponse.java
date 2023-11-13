package com.olegrubin.allmagendemo.model.dto.common;

public class ErrorResponse extends BaseResponse<String> {

    public ErrorResponse(String errorMessage) {
        super(false, errorMessage);
    }
}
