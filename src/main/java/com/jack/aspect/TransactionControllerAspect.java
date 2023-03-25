package com.jack.aspect;

//Spring Imports
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

//Java Imports


//Project Imports

@Aspect
@Component
@EnableAspectJAutoProxy
public class TransactionControllerAspect {

    @Around("execution(public * *(..))")
    public void simpleCut() {
        System.out.println("here");
    }

    //AROUND to intercept any controller operation that takes a userId as string path variable
    @Around("execution(* com.jack.controller..*(..)) && args(String, ..)")
    private Object validateUserIdAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        System.out.println("At join point with: " + args[0].toString());
        if (args[0] instanceof String)
            args[0] = ((String) args[0]).toUpperCase();
        joinPoint.proceed(args);
        return null;
    }
}
