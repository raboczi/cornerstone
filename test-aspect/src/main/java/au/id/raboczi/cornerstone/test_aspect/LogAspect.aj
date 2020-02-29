package au.id.raboczi.cornerstone.test_aspect;

import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect LogAspect {

    static Logger LOGGER = LoggerFactory.getLogger("LogAspect");

    pointcut log() : execution(* *(..)) && @annotation(Log);

    before() : log() {
        LOGGER.info("enter "
            + ((MethodSignature) thisJoinPoint.getSignature()).getMethod().getAnnotation(Log.class).prefix()
            + " "
            + thisJoinPoint.getSignature());
    }

    after() : log() {
        LOGGER.info("exit "
            + ((MethodSignature) thisJoinPoint.getSignature()).getMethod().getAnnotation(Log.class).prefix()
            + " "
            + thisJoinPoint.getSignature());
    }
}
