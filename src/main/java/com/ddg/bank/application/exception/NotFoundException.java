package com.ddg.bank.application.exception;

import javax.ws.rs.core.Response;

public class NotFoundException extends ApiRootException {

    public NotFoundException(final String message) {
        super(Response.Status.NOT_FOUND.getStatusCode(), message);
    }

}
