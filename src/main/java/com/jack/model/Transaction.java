package com.jack.model;

//Spring Imports
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

//JPA Imports
import javax.persistence.*;

//Lombok Imports
import lombok.*;



/* Transaction model class for holding data in each transaction as read from the database
 * we will annotate our values with JPA to be persisted correctly
 
 
 	SQL COL DEFINITIONS:
 
 	t_id 			INTEGER 		PRIMARY KEY,
	purchase_date 	VARCHAR(12) 	NOT NULL DEFAULT CURRENT_DATE,
	amount 			NUMERIC(10, 2) 	NOT NULL DEFAULT '0' CHECK (amount>=0),
	vendor 			VARCHAR(50) 	REFERENCES vendors(vendor),
	category		VARCHAR(50)		NOT NULL DEFAULT 'Misc'
	pay_method 		VARCHAR(50) 	REFERENCES pay_methods(pay_method) NOT NULL DEFAULT 'CASH',
	bought_for 		RECIPIENT 		NOT NULL DEFAULT 'PERSONAL',
	pay_status 		STATUS_TYPE 	NOT NULL DEFAULT 'COMPLETE', 
	is_income 		BOOLEAN 		DEFAULT FALSE,
	reimburses 		INTEGER 		REFERENCES transactions(t_id) DEFAULT 0,
	posted_date 	VARCHAR(12) 	DEFAULT CURRENT_DATE,
	notes 			VARCHAR(1024) 	DEFAULT NULL
 
 * 
 */


@Data @AllArgsConstructor						//We want lombok to write getters and setters
@Entity @Table(name="simple_transactions")		//Want JPA to pick it up
@Component										//We want spring to pick it up
public class Transaction {
	
	/* PERSISTED  STATE VARIABLES */
	
	@Id
	@Column(name="tId")
	@JsonProperty(value="t_id")
	private long tId;
	
	@Column(nullable=false)
	@JsonProperty(value="purchase_date")
	private String purchaseDate;
	
	@Column(columnDefinition="NUMERIC(10, 2) NOT NULL DEFAULT '0' CHECK (amount>=0)")
	private double amount;
	
	//End State Vars
	//###############################################
	
	//Constructors:
	public Transaction() {
		//Dummy constr
	}
	
	
	
	
	

}
