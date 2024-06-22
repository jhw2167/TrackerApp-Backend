package com.jack.aspect.VendorAspect;

//Spring Imports

import com.jack.repository.TransactionRepo;
import com.jack.service.TransactionService;
import com.jack.service.UserAccountService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;


/* REMEMBER TO ADD BEAN TO SPRING CONFIG CLASS */
@Aspect
public class VendorControllerAspect {

    private int CNTR_USRID_INDX = 1;

    //Vars
    @Autowired
    UserAccountService us;

    /*
        * Pointcut for all methods in the VendorController class that return
        * ResponseEntity<Vendor> or ResponseEntity<List<Vendor>>

     */
    // Pointcut for methods returning ResponseEntity<Vendor>
    @Pointcut("execution(public org.springframework.http.ResponseEntity<com.jack.model.Vendor> com.jack.controller.VendorController.*(..))")
    public void vendorMethods() {}

    // Pointcut for methods returning ResponseEntity<List<Vendor>>
    @Pointcut("execution(public org.springframework.http.ResponseEntity<java.util.List<com.jack.model.Vendor>> com.jack.controller.VendorController.*(..))")
    public void vendorListMethods() {}

    /*          ADVICE         */


    /*          ADVICE         */
}
