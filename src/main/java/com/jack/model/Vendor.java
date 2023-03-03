package com.jack.model;


import javax.annotation.Resource;

//JPA Imports
import javax.persistence.*;

import org.springframework.stereotype.Component;

//Lombok Imports
import lombok.*;

import com.fasterxml.jackson.annotation.JsonProperty;
//Project imports
import com.jack.repository.*;



@Entity @Table(name="vendors")
@Data
public class Vendor {

	@Id
	private String vendor;
	
	@Column(columnDefinition="NUMERIC(10, 2) NOT NULL DEFAULT '0' CHECK (amount>=0)")
	private double amount;
	
	@Column(columnDefinition="VARCHAR(50) NOT NULL DEFAULT 'Misc'")
	private String category;
	
	@Column(columnDefinition="BOOLEAN DEFAULT FALSE")
	private boolean isTypicallyIncome;

	
	/* CONSTRUCTORS */
	public Vendor() {
		super();		//spring needs default constructor
	}
	
	public Vendor(Vendor v) {
		super();
		this.vendor = v.vendor;
		this.amount = v.amount;
		this.category = v.category;
		this.isTypicallyIncome = v.isTypicallyIncome;
	}

	public Vendor(String v) {
		super();
		setVendor(v.replace("'", "").toUpperCase());
		setAmount(0);
		setCategory("MISC");
		setTypicallyIncome(false);
	}

	public Vendor(String v, Double a, String c, Boolean inc) {
		super();
		setVendor(v.replace("'", "").toUpperCase());
		setAmount(a);
		setCategory(c);
		setTypicallyIncome(inc);
	}
	
	
	
}
