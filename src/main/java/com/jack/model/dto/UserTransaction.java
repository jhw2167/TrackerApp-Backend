package com.jack.model.dto;

//Project imports
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jack.model.Transaction;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/*
    Transaction which contains all useful information utilized by the frontend,
    not conceptually linked to the db record

 */

@Data                                    //We want lombok to write getters and setters
@Entity
public class UserTransaction extends Transaction {

    @JsonProperty("payMethod")
    private String payMethodString;

    /* CONSTRUCTORS */
    UserTransaction() {
        super();
        setPayMethodString(null);
    }

    /* SETTERS */
    private void setPayMethodString(String pms) {
		this.payMethodString = (pms==null || pms.equals("")) ? Transaction.DEF_VALUES.get("PAY_METHOD")
                : pms.toUpperCase();
	}


}
