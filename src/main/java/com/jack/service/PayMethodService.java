package com.jack.service;

//Java Imports

import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;

//Spring Imports


//Project imports
import com.jack.repository.*;
import com.jack.model.*;
import com.jack.model.submodel.*;
import com.jack.repository.subrepo.*;

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
    @Autowired
    PayMethodKeyRepo keyRepo;

    /* METHODS */

    public PayMethod savePayMethod(Transaction t, UserAccount u) {

        PayMethod pm = null;
        long pmId = t.getPayMethodId();
        String providedPm = t.getPayMethodString();

        if(pmId!=0)
            pmId = pm.getPmId();

        if(pmId==0) { //default, use provided pm string
            if(providedPm==null || providedPm.isEmpty()) {
                //nothing, use existing pm
            } else if(repo.findByMethodName(u.getUserId(), providedPm).isPresent()) {
                pm = repo.findByMethodName(u.getUserId(), providedPm).get();
            }
        } else {
            pm = repo.getById(pmId);
        }
        t.setPayMethodId(pm.getPmId());

        if(!repo.existsById(pm.getPmId()))
            keyRepo.save(new PayMethodKey(pm.getPmId(), u.getUserId()));
        return repo.save(pm);
    }

}
