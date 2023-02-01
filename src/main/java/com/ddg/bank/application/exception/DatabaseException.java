package com.ddg.bank.application.exception;

import javax.ws.rs.core.Response;

public class DatabaseException extends ApiRootException {

    public DatabaseException(final Throwable cause, final String message) {
        super(cause, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message);
    }

}
