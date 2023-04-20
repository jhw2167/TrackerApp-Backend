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



@Data
@Entity @Table(name="vendors")
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
		setVendor(v.vendor);
		setAmount(v.amount);
		setCategory(v.category);
		setTypicallyIncome(v.isTypicallyIncome);
	}

	public Vendor(String v) {
		super();
		setVendor(v);
		setAmount(0d);
		setCategory("MISC");
		setTypicallyIncome(false);
	}

	public Vendor(String v, Double a, String c, Boolean inc) {
		super();
		setVendor(v);
		setAmount(a);
		setCategory(c);
		setTypicallyIncome(inc);
	}

	public Vendor(Transaction t) {
		this(t.getVendor(), 0.d, t.getCategory(), t.isIncome());
	}

	/* SETTERS */
	public void setVendor(String v) {
		if(v==null || v.isEmpty())
			v="UNKOWN";
		this.vendor = v.replace("'", "").toUpperCase();
	}

	public void setCategory(String c) {
		if(c==null || c.isEmpty())
			c="MISC";
		this.category = c.toUpperCase();
	}

	public void setAmount(Double d) {
		if(d==null)
			d=0d;
		this.amount = d;
	}

	public void setTypicallyIncome(Boolean inc) {
		if(inc==null)
			inc=false;
		this.isTypicallyIncome = inc;
	}

}
