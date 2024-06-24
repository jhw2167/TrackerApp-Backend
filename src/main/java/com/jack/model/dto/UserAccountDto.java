package com.jack.model.dto;

//Imports
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jack.model.UserAccount;
import com.jack.utility.Time;
import lombok.Data;

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
public class UserAccountDto {

    private String userId;

    private String createdDate;

    private String deactivatedDate;

    private String email;

    private String password;

    private String lastLoginDate;

    private String authToken;



    /* CONSTRUCTORS */

    public UserAccountDto() {
        super();
    }

    public UserAccountDto(UserAccount u) {
        this.userId = u.getUserId();
        this.createdDate = u.getCreatedDate().toString();
        this.deactivatedDate = (u.getDeactivatedDate()!=null) ? u.getDeactivatedDate().toString() : null;
        this.email = u.getEmail();
        this.password = null;       //We don't want to send the password back
        this.lastLoginDate = u.getLastLoginDate().toString();
        this.authToken = u.getAuthToken();
    }

    /* Utility Methods */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final UserAccountDto u = (UserAccountDto) obj;
        return this.userId.equals(u.getUserId());
    }


}
