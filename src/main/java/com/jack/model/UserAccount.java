package com.jack.model;

//Imports
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jack.utility.Time;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

/*
        COL_NAME            TYPE            RESTRICTIONS
        u_id 			    VARCHAR      	PRIMARY KEY, created_date+email
        created_date 	    VARCHAR(12) 	DATE NOT NULL DEFAULT CURRENT_DATE,
        deactivated_date 	VARCHAR(12) 	DATE,
        email 			    VARCHAR 	    NOT NULL UNIQUE
        password		    VARCHAR		    NOT NULL
        auth_token 			VARCHAR 	    DEFAULT NULL
        last_login_date 	VARCHAR(12) 	DATE NOT NULL DEFAULT CURRENT_DATE,
*/


@Data                                    //We want lombok to write getters and setters
@Entity @Table(name="user_accounts")    		//Want JPA to pick it up
public class UserAccount {

    @Id
    @JsonProperty("userId")
    @Column(name = "user_id", columnDefinition="VARCHAR")
    private String userId;

    @Column(name = "created_date", columnDefinition="VARCHAR(12) NOT NULL DEFAULT CURRENT_DATE")
    private LocalDate createdDate;

    @Column(name = "deactivated_date", columnDefinition="VARCHAR(12)")
    private LocalDate deactivatedDate;

    @Column(name = "email", columnDefinition="VARCHAR UNIQUE NOT NULL")
    private String email;

    @Column(name = "password_", columnDefinition="VARCHAR NOT NULL")
    private String password;

    @Column(name = "last_login_date", columnDefinition="VARCHAR(12) NOT NULL DEFAULT CURRENT_DATE")
    private LocalDate lastLoginDate;

    @Column(name = "auth_token", columnDefinition="VARCHAR")
    private String authToken;


    /* CONSTRUCTORS */

    public UserAccount() {
        //Created Date is now
        this.createdDate = Time.today();
        this.lastLoginDate = Time.today();
        this.deactivatedDate=null;
    }

    public UserAccount(UserAccount u) {
        this.userId = setUserId(u.createdDate, u.email);
        this.createdDate = u.createdDate;
        this.deactivatedDate = u.deactivatedDate;
        this.email = u.email;
        this.password = u.password;
        this.lastLoginDate = u.lastLoginDate;
        this.authToken = u.authToken;
    }

    /*
        Creates user id as the date created appended to the unique email address
        - createdDate should be set to "today" if it is ever null
        - this should run after email has been successfully validated
     */
    public String setUserId(LocalDate createdDate, String email) {
        return createdDate.toString() + email;
    }


    //END SETTERS

    /* Utility Methods */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final UserAccount u = (UserAccount) obj;
        return this.userId.equals(u.getUserId());
    }


}
