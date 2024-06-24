package com.jack.model;

//Imports
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jack.model.dto.UserAccountDto;
import com.jack.utility.Time;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

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

    @Column(name = "created_date", columnDefinition="DATE NOT NULL DEFAULT CURRENT_DATE")
    private LocalDate createdDate;

    @Column(name = "deactivated_date", columnDefinition="DATE DEFAULT NULL")
    private LocalDate deactivatedDate;

    @Column(name = "email", columnDefinition="VARCHAR UNIQUE NOT NULL")
    private String email;

    @Column(name = "password_", columnDefinition="VARCHAR NOT NULL")
    @JsonIgnore
    private String password;

    @Column(name = "last_login_date", columnDefinition="DATE NOT NULL DEFAULT CURRENT_DATE")
    private LocalDate lastLoginDate;

    @Column(name = "auth_token", columnDefinition="VARCHAR")
    private String authToken;


    /* CONSTRUCTORS */

    public UserAccount() {
        super();
    }

    public UserAccount(UserAccount u) {
        this.userId = generateUserId(u.createdDate, u.email);
        this.createdDate = u.createdDate;
        this.deactivatedDate = u.deactivatedDate;
        this.email = u.email;
        this.password = u.password;
        this.lastLoginDate = u.lastLoginDate;
        this.authToken = u.authToken;
    }

    public UserAccount(UserAccountDto dto) {

        System.out.println("Creating user: " + dto.toString());

        this.createdDate = Time.today();
        this.deactivatedDate = (dto.getDeactivatedDate() != null) ? LocalDate.parse(dto.getDeactivatedDate()) : null;

        //null check on email
        if( dto.getEmail() != null )
            this.email = dto.getEmail();
        else
            throw new  HttpClientErrorException(HttpStatus.BAD_REQUEST, "User Email cannot be null");

        System.out.println("can we get here");
        if( dto.getPassword() != null )
            this.password = dto.getPassword();
        else
            throw new  HttpClientErrorException(HttpStatus.BAD_REQUEST, "User Password cannot be null");

        this.lastLoginDate = Time.today();
        this.authToken = dto.getAuthToken();

        //this.userId = generateUserId(this.createdDate, this.email);
        this.userId = dto.getUserId();
        System.out.println("Created user: " + this.toString());
    }

    /*
        Creates user id as the date created appended to the unique email address
        - createdDate should be set to "today" if it is ever null
        - this should run after email has been successfully validated
     */
    public static String generateUserId(LocalDate createdDate, String email) {
        return createdDate.toString() + email.toUpperCase();
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
