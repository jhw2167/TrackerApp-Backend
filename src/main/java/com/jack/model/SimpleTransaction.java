package com.jack.model;

//Spring Imports
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

//JPA Imports
import javax.persistence.*;

//Lombok Imports
import lombok.*;



/* SimpleTransaction model class for testing
 
 
 	SQL COL DEFINITIONS:
 
 	t_id 			INTEGER 		PRIMARY KEY,
	purchase_date 	VARCHAR(12) 	NOT NULL DEFAULT CURRENT_DATE,
	amount 			NUMERIC(10, 2) 	NOT NULL DEFAULT '0' CHECK (amount>=0),
 * 
 */


@Data @AllArgsConstructor						//We want lombok to write getters and setters
@Entity @Table(name="simple_transactions")		//Want JPA to pick it up
@Component										//We want spring to pick it up
public class SimpleTransaction {
	
	/* PERSISTED  STATE VARIABLES */
	
	@Id
	@Column(name="tId")
	@JsonProperty(value="tId")
	private long tId;
	
	@Column(nullable=false)
	@JsonProperty(value="purchaseDate")
	private String purchaseDate;
	
	@Column(columnDefinition="NUMERIC(10, 2) NOT NULL DEFAULT '0' CHECK (amount>=0)")
	private double amount;
	
	//End State Vars
	//###############################################
	
	//Constructors:
	public SimpleTransaction() {
		//Dummy constr
	}
	
	
	
	
	

}
