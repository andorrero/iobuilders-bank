package com.ddg.bank.application.exception;

import javax.ws.rs.core.Response;

public class UnauthorizedException extends ApiRootException {

    public UnauthorizedException(final String message) {
        super(Response.Status.UNAUTHORIZED.getStatusCode(), message);
    }

}
