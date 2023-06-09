package com.jack.model.dto;

//Java Imports

//Spring Imports
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

//Project imports
import com.jack.model.Transaction;

   /*
    Transaction which contains all useful information utilized by the frontend,
    not conceptually linked to the db record
    */




@Data                                    //We want lombok to write getters and setters
public class TransactionDto extends Transaction {

    @JsonProperty("payMethod")
    private String payMethodString;

 /*  CONSTRUCTORS  */

    TransactionDto() {
        super();
        setPayMethodString(null);
    }

     /*  SETTERS  */

    private void setPayMethodString(String pms) {
		this.payMethodString = (pms==null || pms.equals("")) ? Transaction.DEF_VALUES.get("PAY_METHOD")
                : pms.toUpperCase();
	}


}
