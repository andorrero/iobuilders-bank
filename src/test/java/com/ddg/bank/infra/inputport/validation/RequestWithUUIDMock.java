package com.ddg.bank.infra.inputport.validation;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RequestWithUUIDMock {

    @UUID
    String uuid;

}
