package com.jack.model.dto;

//Java Imports
import javax.persistence.*;

//Spring Imports
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

//Project imports
import com.jack.model.Transaction;
import org.springframework.security.core.Transient;

   /*
    Transaction which contains all useful information utilized by the frontend,
    not conceptually linked to the db record
    */




@Data                                    //We want lombok to write getters and setters
public class UserTransaction extends Transaction {

    @JsonProperty("payMethod")
    private String payMethod;

 /*  CONSTRUCTORS  */

    UserTransaction() {
        super();
        setPayMethodString(null);
    }

     /*  SETTERS  */

    private void setPayMethodString(String pms) {
		this.payMethod = (pms==null || pms.equals("")) ? Transaction.DEF_VALUES.get("PAY_METHOD")
                : pms.toUpperCase();
	}


}
