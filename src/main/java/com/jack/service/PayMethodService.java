package com.jack.service;

//Java Imports
import java.util.Optional;


//Spring Imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


//Project imports
import com.jack.repository.*;
import com.jack.model.*;



/* Service class for Pay Method relation handles all BUSINESS logic and is called from the
 * controller class
 *
 *
 */

@Service
public class PayMethodService {

    /* VARS */


    @Autowired
    PayMethodRepo repo;


    /* METHODS */

    public PayMethod savePayMethod(Transaction t, UserAccount u) {

        String providedPm = Transaction.DEF_VALUES.get("PAY_METHOD");
        try {
            providedPm = (String) t.getClass().getMethod("getPayMethodString").invoke(null);
        } catch (Exception reflectionException) { /* BLANK */ }

        //If transaction has provided pay method and it exists under this user
        Optional<PayMethod> pmById = repo.findByUserIdAndPmId(u.getUserId(), t.getPayMethod().getPmId());
        Optional<PayMethod> pmByName = repo.findByUserIdAndPayMethod(u.getUserId(), providedPm);
        PayMethod newPayMethod = null;
        if(pmById.isPresent()) {
            return pmById.get(); //All good
        } else if(pmByName.isPresent()) {
            t.setPayMethod(pmByName.get());
            return pmByName.get();
        } else {
            newPayMethod = new PayMethod(u, providedPm);
            repo.save(newPayMethod);
        }

        return newPayMethod;
    }

}
