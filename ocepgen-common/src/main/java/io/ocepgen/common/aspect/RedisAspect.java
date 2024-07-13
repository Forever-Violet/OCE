/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.ocepgen.common.aspect;

import io.ocepgen.common.exception.ErrorCode;
import io.ocepgen.common.exception.OcepgenException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Redis切面处理类
 *
 * @author Mark sunlightcs@gmail.com
 */
//@Aspect
//@Component  //暂时注释了，这个东西导致redisTemplate用不了
public class RedisAspect {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 是否开启redis缓存  true开启   false关闭
     */
    @Value("${renren.redis.open: false}")
    private boolean open;

    @Around("execution(* io.ocepgen.common.utils.RedisUtils.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        if (open) {
            try {
                result = point.proceed();
            } catch (Exception e) {
                logger.error("redis error", e);
                throw new OcepgenException(ErrorCode.REDIS_ERROR);
            }
        }
        return result;
    }
}
