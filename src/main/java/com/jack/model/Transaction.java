package com.jack.model;

//Java imports
import java.time.LocalDate;

//Spring Imports


//JPA Imports
import javax.persistence.*;

import com.jack.utility.General;
import org.springframework.stereotype.Component;

//Lombok Imports
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

//Project imports


/* Transaction model class for holding data in each transaction as read from the database
 * we will annotate our values with JPA to be persisted correctly
 
 //I am committing to the camel case paradigm in SQL as it is in Java and JSON
 	SQL COL DEFINITIONS:
 
 	tId 			INTEGER 		PRIMARY KEY,
	purchase_date 	VARCHAR(12) 	NOT NULL DEFAULT CURRENT_DATE,
	amount 			NUMERIC(10, 2) 	NOT NULL DEFAULT '0' CHECK (amount>=0),
	vendor 			VARCHAR(50) 	REFERENCES vendors(vendor),
	category		VARCHAR(50)		NOT NULL DEFAULT 'Misc'
	pay_method 		VARCHAR(50) 	REFERENCES pay_methods(payMethod) NOT NULL DEFAULT 'CASH',
	bought_for 		RECIPIENT 		NOT NULL DEFAULT 'PERSONAL',
	pay_status 		STATUS_TYPE 	NOT NULL DEFAULT 'COMPLETE',
	is_income 		BOOLEAN 		DEFAULT FALSE,
	reimburses 		INTEGER 		REFERENCES transactions(tId) DEFAULT 0,
	posted_date 	VARCHAR(12) 	DEFAULT CURRENT_DATE,
	notes 			VARCHAR(1024) 	DEFAULT NULL
 
 * 
 */

@Data 									//We want lombok to write getters and setters
@Entity @Table(name="transactions")		//Want JPA to pick it up
public class Transaction {
	
	/* PERSISTED  STATE VARIABLES */

	@Id
	@Column(name = "true_id", columnDefinition="NUMERIC PRIMARY KEY")
	@JsonProperty("trueId")
	private long trueId;

	@JsonProperty("tid")
	@Column(name="t_id", columnDefinition="NUMERIC NOT NULL")
	private long tId;
	
	@Column(columnDefinition="DATE NOT NULL DEFAULT CURRENT_DATE")
	private LocalDate purchaseDate;
	
	@Column(columnDefinition="NUMERIC(10, 2) NOT NULL DEFAULT '0' CHECK (amount>=0)")
	private double amount;
	
	@Column(columnDefinition="VARCHAR(50) NOT NULL")	//references vendors
	private String vendor;
	
	@Column(columnDefinition="VARCHAR(50) NOT NULL DEFAULT 'Misc'")
	private String category;
	
	@Column(columnDefinition="VARCHAR(20) NOT NULL DEFAULT 'PERSONAL'")
	private String boughtFor;
	
	@Column(columnDefinition="VARCHAR(20) NOT NULL DEFAULT 'CASH'")	//references pay_methods
	private String payMethod;
	
	@Column(columnDefinition="VARCHAR(20) NOT NULL DEFAULT 'COMPLETE'")
	private String payStatus;
	
	@Column(columnDefinition="BOOLEAN DEFAULT FALSE")
	@JsonProperty("income")
	private boolean isIncome;
	
	@Column(columnDefinition="INTEGER REFERENCES transactions(t_id) DEFAULT 0")
	private long reimburses;
	
	@Column(columnDefinition="DATE NOT NULL DEFAULT CURRENT_DATE")
	private LocalDate postedDate;
	
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
		this.trueId=t.trueId;
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

	/*
		-Create transaction from immature transaction submitted in POST calls
		"Immature" transactions need their TIDs created
	 */
	public Transaction(final UserAccount u, final Transaction t, final long transOnDate) {
		super();
		//System.out.println("My Constructor");
		settId(t.purchaseDate.toString(), transOnDate);
		setTrueId(u.getUserId(), this.tId);
		this.purchaseDate = t.purchaseDate;
		this.amount = t.amount;

		setVendor(t.vendor);
		setCategory(t.category);
		setBoughtFor(t.boughtFor);
		setPayMethod(t.payMethod);
		setPayStatus(t.payStatus);
		setIncome(t.isIncome);
		this.reimburses = t.reimburses;
		setPostedDate(t.postedDate);
		this.notes = t.notes;
	}

	public void updateData(Transaction t) {
		this.setTrueId(t.getTrueId());
		this.setAmount(t.getAmount());
		this.setBoughtFor(t.getBoughtFor());
		this.setCategory(t.getCategory());
		this.setIncome(t.isIncome());
		this.setNotes(t.getNotes());
		this.setPayMethod(t.getPayMethod());
		this.setPayStatus(t.getPayStatus());
		this.setPurchaseDate(t.getPurchaseDate());
		this.setPostedDate(t.getPostedDate());
		this.setReimburses(t.getReimburses());
		this.setVendor(t.getVendor());
	}
	/* END CONSTRUCTORS */

	
	//Setters

	public void setTrueId(String userId, long tid) {
		this.trueId = General.mergeHash(userId.hashCode(), new Long(tid).hashCode());
	}
	public void settId(long tId) {
		this.tId = tId;
	}
	public void settId(String _purchaseDate, long transOnDate) {
		//parse date string into coherent string
		String date = _purchaseDate.replace("-", "");
		
		//add above result, buffered by "000"
		String id = date + String.format("%03d", transOnDate);
		this.tId = Long.parseLong(id);
		//System.out.println("Set id is: " + tId);
	}

	/*
		- Removes all instance of ' from vendor and sends to upper case
	 */
	private void setVendor(String vendor2) {
		this.vendor = vendor2.replace("'", "").toUpperCase();
	}
	
	private void setPostedDate(LocalDate postedDate2) {
		this.postedDate = postedDate2==null ? this.purchaseDate : postedDate2;		
	}

	private void setPayStatus(String payStatus2) {
		this.payStatus = payStatus2.equals("") ? "COMPLETE" : payStatus2.toUpperCase();
	}

	private void setPayMethod(String payMethod2) {
		this.payMethod = payMethod2.equals("") ? "CASH" : payMethod2.toUpperCase();
	}

	private void setBoughtFor(String boughtFor2) {
		this.boughtFor = boughtFor2.equals("") ? "PERSONAL" : boughtFor2.toUpperCase();
	}

	private void setCategory(String category2) {
		this.category = category2.equals("") ? "MISC" : category2.toUpperCase();	
	}
	
	//END SETTERS


	/* Overrides */


	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj.getClass() != this.getClass()) {
			return false;
		}

		final Transaction t = (Transaction) obj;
		return this.trueId==t.getTrueId();
	}
}
//END CLASS
