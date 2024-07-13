package io.ocepgen.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 全局异步调用异常处理类
 * @author roxy
 */
@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {


    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("--------------------------------------------");
        log.error("Async method {} has uncaught exception {}", method.getName(), ex.getMessage());
        Arrays.stream(params).forEach(param -> log.error("Param: {}", param));
        log.error("--------------------------------------------");
    }
}
