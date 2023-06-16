package com.jack.aspect.TransactionAspect;

//Spring Imports
import com.jack.service.algorithms.VendorAlgorithms;
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
import com.jack.model.*;
import com.jack.service.algorithms.*;


/* REMEMBER TO ADD BEAN TO SPRING CONFIG CLASS */
@Aspect
public class TransactionRepoAspect {

    //Vars
    @Autowired
    UserAccountService us;

    @Autowired
    TransactionRepo repo;

    @Autowired
    VendorAlgorithms vendorAlgs;


    /*      CREATE VIEW FOR INCOME/EPENDITURE SUMMARY       */

    //All methods beloning to transaction repo interface except the method where we actually create the view
    @Pointcut("execution(* com.jack.repository.TransactionRepo+.*(..))  && args(String,..) &&" +
            " !execution(* com.jack.repository.TransactionRepo+.createUserTransactionsView(..))  &&" +
            " !execution(* com.jack.repository.TransactionRepo+.dropUserTransactionsView(..))")
    public void transactionRepoWithStringArgsPointcut(){}


    @Pointcut("execution(* com.jack.repository.TransactionRepo+.findIncomeAggregatedByCategoryAndBoughtFor(..)) ||" +
            " execution(* com.jack.repository.TransactionRepo+.findExpensesAggregatedByCategoryAndBoughtFor(..))")
    public void transactionRepoSummariesPointcut(){}

    /*          ADVICE         */

    //AROUND to intercept any controller operation that takes a userId as string path variable
    /*@Around("transactionRepoWithStringArgsPointcut()")*/
    @Around("transactionRepoSummariesPointcut()")
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
            logger.info(" ATTEMPTING CREATION OF USER TRANSACTIONS VIEW WITH NAME: "
                    + userTransViewName);
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
            logger.warn("ERROR: FAILED TO DROP USER TRANSACTIONS VIEW WITH NAME: "
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

        Regex describes the technical rules below
     */
    private static final int PSQL_VIEW_LENGTH_LIMIT = 63;
    private String cleanUserIdForViewName(String id) {
        id = "TRANSACTIONS_VIEW_" + id;
        id = id.replaceAll("[^a-zA-Z0-9_]|^(?![a-zA-Z0-9_]{0,63}$)", "");
        return id;
    }

    /*  *********************************************  */

    /*  TRIGGER VENDOR UPDATE ON INSERT/DELETE FROM TRANSACTIONS  */

        /* Pointcuts */

        //Update or Save Transaction
        @Pointcut("execution(* com.jack.repository.TransactionRepo+.save(..))")
        private void saveTransactionPointcut(){};

        //Delete Transaction
        @Pointcut("execution(* com.jack.repository.TransactionRepo+.deleteById(..))")
        private void deleteTransactionPointcut(){};

    /* Advice */
    @Around("saveTransactionPointcut() || deleteTransactionPointcut()")
    private Object triggerCalculateVendorAverageCostUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
        Object[] args = joinPoint.getArgs();
        Transaction toUpdate = null;

        if(args[0] instanceof Transaction) //We have a transaction
            toUpdate = (Transaction) args[0];
        else if (args[0] instanceof Long)
            toUpdate = repo.getById((Long) args[0]);
        else
            logger.warn("triggerCalculateVendorAverageCostUpdate() encountered unfamiliar " +
                    "transaction update method, vendor average cost will not be updated");
        Vendor v = toUpdate.getVendor();
        Object results = joinPoint.proceed(joinPoint.getArgs());
        vendorAlgs.calculateAverageCostOrIncomeByVendor(v);

        return results;
    }

    /*  *********************************************  */

}
