package com.jack.aspect;

//Spring Imports

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

//Loggers
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


//logging


/* REMEMBER TO ADD BEAN TO SPRING CONFIG CLASS */
@Aspect
public class LoggerAspect {

    //Vars
    private int CNTR_RQST_INDX = 0;

    //CONTROLLER LOGGING
    @Pointcut("within(com.jack.controller..*)")
    public void controllerPointcut(){}

    //SERVICE LOGGING
    @Pointcut("within(com.jack.service..*)")
    public void servicePointcut(){}

    //REPO LOGGING
    @Pointcut("execution(* com.jack.repository.*+.*(..)) && !execution(* java.lang.Object+.*(..))\n")
    public void repoPointcut(){}


    /*          ADVICE         */

    //AROUND advice for logging each entry/exit to relevant method
    @Around("servicePointcut() || repoPointcut()")
    public Object entryExitLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
        logger.info("ENTERING: " + joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        logger.info("EXITING: " + joinPoint.getSignature().getName());
        return result;
    }

    @Around("controllerPointcut()")
    public Object controllerExitEntryLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
        logger.info("ENTERING: " + joinPoint.getSignature().getName());

        Object result = null;
        try {

            Object[] args = joinPoint.getArgs();
            //System.out.println("At join point with: " + args[0].toString());
            if (args[CNTR_RQST_INDX] instanceof HttpServletRequest) {
                HttpServletRequest request = ((HttpServletRequest) args[CNTR_RQST_INDX]);
                String url = request.getRequestURL().toString();
                String queryString = request.getQueryString();
                logger.info("REQUEST: " + url + (queryString == null ? "" : "?" + queryString));
            }

            result = joinPoint.proceed();

        } catch (JsonParseException ex) {
            logger.error("JSONParseException Emitted from Controller " + joinPoint.getSignature().getName() +"\n"
            + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unkown error Emitted from Controller" + joinPoint.getSignature().getName() +"\n"
                    + ex.getMessage());
            throw ex;
        }

        logger.info("EXITING: " + joinPoint.getSignature().getName() + " with response: \n" + result.toString());
        return result;
    }

    /*          ADVICE         */
}
