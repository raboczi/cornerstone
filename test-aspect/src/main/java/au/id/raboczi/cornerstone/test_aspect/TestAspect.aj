package au.id.raboczi.cornerstone.test_aspect;

import org.aspectj.lang.reflect.MethodSignature;

public aspect TestAspect {

    pointcut log() : execution(* *(..)) && @annotation(Log);

    before() : log() {
        System.out.println("enter "
            + ((MethodSignature) thisJoinPoint.getSignature()).getMethod().getAnnotation(Log.class).prefix()
            + thisJoinPoint.getSignature());
    }

    after() : log() {
        System.out.println("exit "
            + ((MethodSignature) thisJoinPoint.getSignature()).getMethod().getAnnotation(Log.class).prefix()
            + thisJoinPoint.getSignature());
    }
}
