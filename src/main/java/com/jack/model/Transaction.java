package com.jack.model;

//Spring Imports
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
//JPA Imports
import javax.persistence.*;

//Lombok Imports
import lombok.*;

//Project imports
import com.jack.repository.*;
import com.jack.service.TransactionService;


/* Transaction model class for holding data in each transaction as read from the database
 * we will annotate our values with JPA to be persisted correctly
 
 //I am committing to the camel case paradigm in SQL as it is in Java and JSON
 	SQL COL DEFINITIONS:
 
 	tId 			INTEGER 		PRIMARY KEY,
	purchaseDate 	VARCHAR(12) 	NOT NULL DEFAULT CURRENT_DATE,
	amount 			NUMERIC(10, 2) 	NOT NULL DEFAULT '0' CHECK (amount>=0),
	vendor 			VARCHAR(50) 	REFERENCES vendors(vendor),
	category		VARCHAR(50)		NOT NULL DEFAULT 'Misc'
	payMethod 		VARCHAR(50) 	REFERENCES pay_methods(payMethod) NOT NULL DEFAULT 'CASH',
	bought_for 		RECIPIENT 		NOT NULL DEFAULT 'PERSONAL',
	payStatus 		STATUS_TYPE 	NOT NULL DEFAULT 'COMPLETE', 
	isIncome 		BOOLEAN 		DEFAULT FALSE,
	reimburses 		INTEGER 		REFERENCES transactions(tId) DEFAULT 0,
	postedDate 	VARCHAR(12) 	DEFAULT CURRENT_DATE,
	notes 			VARCHAR(1024) 	DEFAULT NULL
 
 * 
 */


@Data 									//We want lombok to write getters and setters
@Entity @Table(name="transactions")		//Want JPA to pick it up
@Component										//We want spring to pick it up
public class Transaction {
	
	//Helper vars
	@Resource
	@Transient
	private TransactionService ts;
	
	/* PERSISTED  STATE VARIABLES */

	@Id
	private long tId;
	
	@Column(columnDefinition="VARCHAR(12) NOT NULL DEFAULT CURRENT_DATE")
	private String purchaseDate;
	
	@Column(columnDefinition="NUMERIC(10, 2) NOT NULL DEFAULT '0' CHECK (amount>=0)")
	private double amount;
	
	@Column(columnDefinition="VARCHAR(50) NOT NULL")	//references vendors
	private String vendor;
	
	@Column(columnDefinition="VARCHAR(50) NOT NULL DEFAULT 'Misc'")
	private String category;
	
	@Column(columnDefinition="VARCHAR(20) NOT NULL DEFAULT 'PERSONAL'")
	private String boughtFor;
	
	@Column(columnDefinition="VARCHAR(12) NOT NULL DEFAULT 'CASH'")	//references pay_methods
	private String payMethod;
	
	@Column(columnDefinition="VARCHAR(20) NOT NULL DEFAULT 'COMPLETE'")
	private String payStatus;
	
	@Column(columnDefinition="BOOLEAN DEFAULT FALSE")
	private Boolean isIncome;
	
	@Column(columnDefinition="INTEGER REFERENCES transactions(t_id) DEFAULT 0")
	private long reimburses;
	
	@Column(columnDefinition="VARCHAR(12) NOT NULL DEFAULT CURRENT_DATE")
	private String postedDate;
	
	@Column(columnDefinition="VARCHAR(1024) DEFAULT NULL")
	private String notes;
	
	//End State Vars
	//###############################################
	
	//Constructors:
	public Transaction() {
		//Dummy constr
	}
	
	public Transaction(final Transaction t) {
		super();
		this.tId = t.tId;
		this.purchaseDate = t.purchaseDate;
		this.amount = t.amount;
		this.vendor = t.vendor;
		this.category = t.category;
		this.boughtFor = t.boughtFor;
		this.payMethod = t.payMethod;
		this.payStatus = t.payStatus;
		this.isIncome = t.isIncome;
		this.reimburses = t.reimburses;
		this.postedDate = t.postedDate;
		this.notes = t.notes;
	}
	
	public Transaction(final Transaction t, long transOnDate) {
		super();
		System.out.println("My Constructor");
		settId(t.purchaseDate, transOnDate);
		this.purchaseDate = t.purchaseDate;
		this.amount = t.amount;
		this.vendor = t.vendor;
		this.category = t.category;
		this.boughtFor = t.boughtFor;
		this.payMethod = t.payMethod;
		this.payStatus = t.payStatus;
		this.isIncome = t.isIncome;
		this.reimburses = t.reimburses;
		this.postedDate = t.postedDate;
		this.notes = t.notes;
	}

	/* END CONSTRUCTORS */
	
	public void settId(long tId) {
		this.tId = tId;
	}
	
	public void settId(String _purchaseDate, long transOnDate) {
		//parse date string into coherent string
		String parsedDate = _purchaseDate.replace("-", "");
		
		//add above result, buffered by "000"
		String id = parsedDate + String.format("%03d", transOnDate);
		this.tId = Long.parseLong(id);
		System.out.println("Set id is: " + tId);
	}
	
	//Setters

}
