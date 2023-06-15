package com.jack.service;

//Java Imports
import java.util.Optional;


//Spring Imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


//Project imports
import static com.jack.utility.General.*;
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

    public PayMethod savePayMethod(PayMethod pm)
    {
        UserAccount u = pm.getUser();
        String providedPm = (isEmpty(pm.getPayMethodName())) ?
                Transaction.DEF_VALUES.get("PAY_METHOD") :
                pm.getPayMethodName();

        //If transaction has provided pay method and it exists under this user
        Optional<PayMethod> pmById = repo.findByUserUserIdAndPmId(u.getUserId(), pm.getPmId());
        Optional<PayMethod> pmByName = repo.findByUserUserIdAndPayMethodName(u.getUserId(), providedPm);

        if(pmById.isPresent()) {
            return pmById.get(); //All good
        } else if(pmByName.isPresent()) {
            return pmByName.get();
        } else {
            return repo.save(new PayMethod(u, providedPm));
        }
        //END IF ELSE
    }


}
//END CLASS PayMethodService