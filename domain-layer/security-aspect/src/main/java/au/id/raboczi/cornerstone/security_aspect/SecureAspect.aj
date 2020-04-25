package au.id.raboczi.cornerstone.security_aspect;

import au.id.raboczi.cornerstone.Caller;
import au.id.raboczi.cornerstone.CallerNotAuthorizedException;
import java.util.Arrays;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect SecureAspect {

    static Logger LOGGER = LoggerFactory.getLogger("SecureAspect");

    pointcut secure(Caller caller) : execution(* *(.., Caller)) && args(.., caller) && @annotation(Secure);

    before(Caller caller) throws CallerNotAuthorizedException : secure(caller) {
        MethodSignature ms = (MethodSignature) thisJoinPoint.getSignature();
        String value = ms.getMethod().getAnnotation(Secure.class).value();
        LOGGER.info("@Secure permission={} caller.roles={}", value, caller.authorization().getRoles());

        if (!Arrays.asList(caller.authorization().getRoles()).contains(value)) {
            throw new CallerNotAuthorizedException(caller, value);
        }
    }


    // Failsafe: throw an Error if @Secure is applied to a method without a trailing Caller parameter

    pointcut secureWithoutCaller() : execution(* *(..)) && @annotation(Secure) && !execution(* *(.., Caller));

    before() : secureWithoutCaller() {
        throw new Error("@Secure annotation can only be applied to methods with a Caller as the last parameter");
    }
}
