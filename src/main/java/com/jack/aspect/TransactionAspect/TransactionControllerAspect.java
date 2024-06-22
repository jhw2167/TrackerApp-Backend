package com.jack.aspect.TransactionAspect;

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

    private int CNTR_USRID_INDX = 1;

    //Vars
    @Autowired
    UserAccountService us;

    @Autowired
    TransactionService ts;

    @Autowired
    TransactionRepo tRepo;


    @Pointcut("execution(* com.jack.controller.TransactionController.*(..))")
    public void transactionControllerPointCut(){}

    @Pointcut("execution(* com.jack.controller.VendorController.*(..))")
    public void vendorControllerPointCut(){}

    /*          ADVICE         */

    //AROUND to intercept any controller operation that takes a userId as string path variable
    @Around("transactionControllerPointCut() || vendorControllerPointCut()")
    private Object validateUserIdAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        //System.out.println("At join point with: " + args[CNTR_USRID_INDX].toString());
        if (args[CNTR_USRID_INDX] instanceof String) {
            args[CNTR_USRID_INDX] = ((String) args[CNTR_USRID_INDX]).toUpperCase();
            us.getUserAccountById((String) args[CNTR_USRID_INDX]);
        }
        
        //System.out.println("At join point 2 with: " + args[CNTR_USRID_INDX].toString());
        return joinPoint.proceed(args);
    }

    /*          ADVICE         */
}
