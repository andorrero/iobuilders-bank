package com.ddg.bank.application.exception;

import javax.ws.rs.core.Response;

public class NonEmptyException extends ApiRootException {

    public NonEmptyException(final String message) {
        super(Response.Status.OK.getStatusCode(), message);
    }

}
