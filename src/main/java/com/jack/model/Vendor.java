package com.jack.model;


//JPA Imports
import javax.persistence.*;

import com.jack.model.dto.TransactionDto;
import com.jack.utility.General;

//Lombok Imports
import lombok.*;

import com.fasterxml.jackson.annotation.JsonProperty;
//Project imports


@Data
@Entity @Table(name="vendors")
public class Vendor {


	@Id
	@Column(name = "vendor")
	private String vendorName;

	@ManyToOne
	@Column(name = "user_id", columnDefinition="VARCHAR NOT NULL DEFAULT '20230303JACKHENRYWELSH@GMAIL.COM'")
	@JsonProperty("userAccount")
	private UserAccount user;
	
	@Column(columnDefinition="NUMERIC(10, 2) NOT NULL DEFAULT '0' CHECK (amount>=0)")
	private double amount;
	
	@Column(columnDefinition="VARCHAR(50) NOT NULL DEFAULT 'Misc'")
	private String category;
	
	@Column(columnDefinition="BOOLEAN DEFAULT FALSE")
	private boolean isTypicallyIncome;

	
	/* CONSTRUCTORS */
	public Vendor() {
		super();
	}
	
	public Vendor(Vendor v) {
		this(v.vendorName, v.amount, v.category, v.isTypicallyIncome);
		setUser(v.getUser());
	}

	public Vendor(UserAccount u, String vendorName) {
		this(vendorName, 0d, null, false);
		setUser(u);
	}

	public Vendor(String v, Double a, String c, Boolean inc) {
		super();
		setVendorName(v);
		setAmount(a);
		setCategory(c);
		setTypicallyIncome(inc);
	}

	public Vendor(UserAccount u, TransactionDto t) {
		this(t.getVendor(), 0.d, t.getCategory(), t.isIncome());
		setUser(u);
	}

	/* SETTERS */

	private long generateVendorId(String vendor, String userId) {
		return General.mergeHash(vendor.hashCode(), userId.hashCode());
	}

	public void setVendorName(String v) {
		if(v==null || v.isEmpty())
			v="UNKOWN";
		this.vendorName = v.replace("'", "").toUpperCase();
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
