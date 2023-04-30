package com.jack.aspect;

//Spring Imports
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

//Loggers
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Project imports
import com.jack.repository.TransactionRepo;
import com.jack.service.UserAccountService;


@Aspect
public class TransactionRepoAspect {

    //Vars
    @Autowired
    UserAccountService us;

    @Autowired
    TransactionRepo repo;

    //All methods beloning to transaction repo interface except the method where we actually create the view
    @Pointcut("execution(* com.example.TransactionRepo+.*(..)) && args(String,..) &&" +
            " !execution(* com.example.TransactionRepo+.createUserTransactionsView(..))")
    public void transactionRepoWithStringArgsPointCut(){}

    /*          ADVICE         */

    //AROUND to intercept any controller operation that takes a userId as string path variable
    @Around("transactionRepoWithStringArgsPointCut()")
    private Object createUserTransactionView(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());

        Object[] args = joinPoint.getArgs();
        String userTransViewName = "";
        try {

            //validate userId
            String userId = "";
            if (args[0] instanceof String) {
                userId = us.getUserAccountById((String) args[0]).getUserId(); //Throws ResourceNotFound if DNE
                userTransViewName = cleanUserIdForViewName(userId);
            } else {
                throw new IllegalArgumentException();
            }

            //CREATE USER TRANSACTIONS TABLE
            repo.createUserTransactionsView(userTransViewName, userId);
            logger.info("CREATED USER TRANSACTIONS VIEW WITH NAME: "
                    + userTransViewName);

            //Value passed to repo "userId" field used to pull data from dynamic view
            args[0]=userTransViewName;

        } catch (IllegalArgumentException e) {
            //Not method we are targeting, exit
            return joinPoint.proceed(args);
        } catch (ResourceNotFoundException rnf) {
            //Not method we are targeting, exit
            return joinPoint.proceed(args);
        } catch (Exception e) {
            //Not method we are targeting, exit
            return joinPoint.proceed(args);
        }

        Object results = joinPoint.proceed(args);
        //DROP USER TRANSACTIONS TABLE
        try {
            repo.dropUserTransactionsView(userTransViewName);
            logger.info("DROPPED USER TRANSACTIONS VIEW WITH NAME: " + userTransViewName);
        } catch (Exception e) {
            logger.warn("ERROR: DROPPED USER TRANSACTIONS VIEW WITH NAME: "
                    + userTransViewName);
        }

        return results;
    }

    /* Rules:
        1. Must start with letter or underscore
        2. Only contain letters, numbers, underscores
        3. must nto exceed max char length of 63
        4. Must be unique in its schema
        5. Cannot be reserved keyword
     */
    private static final int PSQL_VIEW_LENGTH_LIMIT = 63;
    private String cleanUserIdForViewName(String id) {
        id = "TRANSACTIONS_VIEW_" + id;
        id = id.replaceAll("[^a-zA-Z0-9_]|^(.{0,63}).*$", "$1");
        return id;
    }

}
