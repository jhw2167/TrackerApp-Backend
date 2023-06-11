package com.jack.model.dto;

//Java Imports
import java.time.LocalDate;

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
public class TransactionDto {

    private long trueId;
    private String userId;
    private long tid;

    private LocalDate purchaseDate;
    private double amount;
    private String vendor;
    private String category;
    private String boughtFor;
    private String payMethod;
    private String payStatus;
    private boolean isIncome;
    private long reimburses;
    private LocalDate postedDate;
    private String notes;

    /*  CONSTRUCTORS  */

    public TransactionDto() {
        super();
    }

}
