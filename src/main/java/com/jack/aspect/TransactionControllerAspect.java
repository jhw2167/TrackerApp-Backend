package com.jack.aspect;

//Spring Imports
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

//Java Imports


//Project Imports
import com.jack.model.Transaction;
import com.jack.service.TransactionService;
import com.jack.service.UserAccountService;
import com.jack.repository.TransactionRepo;


/* REMEMBER TO ADD BEAN TO SPRING CONFIG CLASS */
@Aspect
public class TransactionControllerAspect {

    //Vars
    @Autowired
    UserAccountService us;

    @Autowired
    TransactionService ts;

    @Autowired
    TransactionRepo tRepo;


    @Pointcut("execution(* com.jack.controller.TransactionController.*(..)) && args(String, ..)")
    public void transactionControllerPointCut(){}

    @Pointcut("execution(* com.jack.controller.VendorController.*(..)) && args(String, ..)")
    public void vendorControllerPointCut(){}

    /*          ADVICE         */

    //AROUND to intercept any controller operation that takes a userId as string path variable
    @Around("transactionControllerPointCut() || vendorControllerPointCut()")
    private Object validateUserIdAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        //System.out.println("At join point with: " + args[0].toString());
        if (args[0] instanceof String) {
            args[0] = ((String) args[0]).toUpperCase();
            us.getUserAccountById((String) args[0]);
        }
        
        //System.out.println("At join point 2 with: " + args[0].toString());
        return joinPoint.proceed(args);
    }

    /*          ADVICE         */
}
