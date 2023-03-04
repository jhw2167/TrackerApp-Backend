package com.jack.model.submodel;

//Java Imports
import java.io.Serializable;

//Project imports
import com.jack.model.UserAccount;
import lombok.Data;

@Data
public class TransactionKey implements Serializable {
    private UserAccount user;
    private long tId;

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final TransactionKey tk = (TransactionKey) obj;
        return this.user.equals(tk.getUser()) && this.tId==tk.tId;
    }
}
