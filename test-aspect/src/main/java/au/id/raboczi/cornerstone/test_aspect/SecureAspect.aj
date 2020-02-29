package au.id.raboczi.cornerstone.test_aspect;

import au.id.raboczi.cornerstone.Caller;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect SecureAspect {

    static Logger LOGGER = LoggerFactory.getLogger("SecureAspect");

    pointcut secure(Caller caller) : execution(* *(..)) && args(caller) && @annotation(Secure);

    before(Caller caller) : secure(caller) {
        MethodSignature ms = (MethodSignature) thisJoinPoint.getSignature();
        String value = ms.getMethod().getAnnotation(Secure.class).value();
        LOGGER.info("@Secure permission={} caller={}", value, caller);
    }
}
