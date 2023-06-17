package com.jack.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class SpringFrameworkControllerInterceptAspect {

  /*  //Intercepts all requests to the Spring framework servlet
    @Pointcut("execution(* *(javax.servlet.http.HttpServletRequest, ..))")
    public void processRequestPointcut() {}

    @Pointcut("within(com.jack.controller..*)")
    public void controllerPointcut(){ }

    @Before("controllerPointcut()")
    public void controllerExitEntryLogging() throws Throwable {
        System.out.println("Randome pointcut");
    }

   @Around("processRequestPointcut()")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
        Object[] args = joinPoint.getArgs();
        javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest) args[0];
        String url = request.getRequestURL().toString();
        logger.info("REQUEST: " + url);
        return joinPoint.proceed(joinPoint.getArgs());
    }
    */
}

