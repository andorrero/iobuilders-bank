package com.ddg.bank.application.exception;

import lombok.Getter;

public class ApiRootException extends RuntimeException {

    @Getter
    private final Integer statusErrorCode;

    public ApiRootException(final Integer statusErrorCode, final String message) {
        super(message);
        this.statusErrorCode = statusErrorCode;
    }

    public ApiRootException(final Throwable cause, final Integer statusErrorCode, final String message) {
        super(message, cause);
        this.statusErrorCode = statusErrorCode;
    }

}
