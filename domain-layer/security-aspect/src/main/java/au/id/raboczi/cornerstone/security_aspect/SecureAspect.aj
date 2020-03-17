package au.id.raboczi.cornerstone.security_aspect;

import au.id.raboczi.cornerstone.Caller;
import au.id.raboczi.cornerstone.CallerNotAuthorizedException;
import java.util.Arrays;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect SecureAspect {

    static Logger LOGGER = LoggerFactory.getLogger("SecurityAspect");

    pointcut secure(Caller caller) : execution(* *(..)) && args(caller) && @annotation(Secure);

    before(Caller caller) throws CallerNotAuthorizedException : secure(caller) {
        MethodSignature ms = (MethodSignature) thisJoinPoint.getSignature();
        String value = ms.getMethod().getAnnotation(Secure.class).value();
        LOGGER.info("@Secure permission={} caller.roles={}", value, caller.authorization().getRoles());

        if (!Arrays.asList(caller.authorization().getRoles()).contains(value)) {
            throw new CallerNotAuthorizedException(caller, value);
        }
    }
}
