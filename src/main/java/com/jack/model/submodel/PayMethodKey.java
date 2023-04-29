/*
package com.jack.model.submodel;

//Java Imports

//Project imports

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jack.model.PayMethod;
import com.jack.model.Transaction;
import com.jack.model.UserAccount;
import lombok.Data;

import javax.persistence.*;


*/
/*
    Join Table/ID table for users and transactions allowing us to use one true id
    to uniquely identify each transaction as it is owned by each user. This avoids
    having to use a composite key on the transactions table

 *//*


@Data
@Entity @Table(name="pay_method_keys")
public class PayMethodKey {

    @Id
    @Column(name = "key_id", columnDefinition="NUMERIC")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long keyId;     //necessary dummy key to avoid annoying legacy composite key code

    //@OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    //@JoinColumn(name = "pm_id", referencedColumnName = "pm_id", columnDefinition="NUMERIC NOT NULL")
    @Column(name = "pm_id", columnDefinition="NUMERIC NOT NULL")
    @JsonProperty("pmId")
    private long payMethod;

    //@ManyToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name = "user_id", referencedColumnName = "user_id", columnDefinition="VARCHAR NOT NULL")
    @Column(name = "user_id", columnDefinition="VARCHAR NOT NULL")
    @JsonProperty("userId")
    private String user;

    public PayMethodKey() {
        //dummy constr
    }

    public PayMethodKey(long p, String u) {
        this.payMethod = p;
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

        final PayMethodKey tk = (PayMethodKey) obj;
        return this.payMethod==tk.getPayMethod();
    }
}
*/
