package com.ddg.bank.application.advice;

import com.ddg.bank.application.exception.ApiRootException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApiRootExceptionMapper implements ExceptionMapper<ApiRootException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRootExceptionMapper.class);

    private static final String MAPPED_EXCEPTION_MESSAGE = "Mapped thrown exception with following message: {}";

    @Override
    public Response toResponse(ApiRootException exception) {
        LOGGER.error(MAPPED_EXCEPTION_MESSAGE, exception.getMessage(), exception);
        return Response.status(exception.getStatusErrorCode()).entity(exception.getMessage()).build();
    }

}
