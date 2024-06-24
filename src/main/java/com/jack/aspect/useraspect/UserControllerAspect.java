package com.jack.aspect.useraspect;

import com.jack.service.UserAccountService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;


@Aspect
public class UserControllerAspect {

    private int CNTR_USRID_INDX = 1;

    //Vars
    @Autowired
    UserAccountService us;


    /*
        * Pointcut to intercept any controller method thats first argument is a string
     */
    @Pointcut("execution(* com.jack.controller.UserController.*(..)) && args(request, userId,..)")
    public void userAccountControllerPointCut(HttpServletRequest request, String userId){}


    /*          ADVICE         */

    //AROUND to intercept any controller operation that takes a userId as string path variable
    @Around("userAccountControllerPointCut(request, userId)")
    private Object validateUserIdAdvice(ProceedingJoinPoint joinPoint, HttpServletRequest request, String userId) throws Throwable {

        Object[] args = joinPoint.getArgs();
        if (args[CNTR_USRID_INDX] instanceof String) {
            args[CNTR_USRID_INDX] = userId.toUpperCase();
            us.getUserAccountById((String) args[CNTR_USRID_INDX]);
        }

        return joinPoint.proceed(args);
    }
}
