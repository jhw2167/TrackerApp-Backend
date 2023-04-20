package com.jack.model.submodel;

//Java Imports

//Project imports
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;

//Project Imports
import com.jack.model.Transaction;
import com.jack.model.UserAccount;


/*
    Join Table/ID table for users and transactions allowing us to use one true id
    to uniquely identify each transaction as it is owned by each user. This avoids
    having to use a composite key on the transactions table

 */

@Data
@Entity @Table(name="transaction_keys")
public class TransactionKey {

    @Id
    @Column(name = "key_id", columnDefinition="NUMERIC PRIMARY KEY")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long keyId;     //necessary dummy key to avoid annoying legacy composite key code


    @OneToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "true_id", referencedColumnName = "true_id", columnDefinition="NUMERIC NOT NULL")
    @JsonProperty("trueId")
    private Transaction transaction;

    @ManyToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", columnDefinition="VARCHAR NOT NULL")
    @JsonProperty("userId")
    private UserAccount user;

    public TransactionKey() {
        //dummy constr
    }

    public TransactionKey(Transaction t, UserAccount u) {
        this.transaction = t;
        this.user = u;
    }

    @Override
   public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final TransactionKey tk = (TransactionKey) obj;
        return this.transaction.equals(tk.getTransaction());
    }
}
