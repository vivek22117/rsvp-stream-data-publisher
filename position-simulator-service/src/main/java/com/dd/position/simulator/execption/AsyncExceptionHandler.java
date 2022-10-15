package com.dd.position.simulator.execption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
        log.debug("Exception Cause - " + throwable.getMessage());
        log.debug("Method name - " + method.getName());
        for (Object param : params) {
            log.debug("Parameter value - " + param);
        }
    }
}
