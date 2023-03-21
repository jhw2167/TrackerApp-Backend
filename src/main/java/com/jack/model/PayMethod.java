package com.jack.model;


//JPA Imports

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jack.utility.General;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//Project imports


/* Table Details

	pm_id 				INTEGER 		PRIMARY KEY,
	pay_method 			VARCHAR		 	NOT NULL DEFAULT CASH,
	institution_type	VARCHAR			NOT NULL DEFAULT SIMPLE,
	balance				NUMERIC
	credit_line			NUMERIC
	cash_back			NUMERIC
	description			VARCHAR
	date_added	 		VARCHAR(12) 	NOT NULL DEFAULT CURRENT_DATE,

 */


@Data
@Entity @Table(name="pay_methods")
public class PayMethod {

	@Id
	@Column(name="pm_id", columnDefinition="NUMERIC")
	@JsonProperty("pmId")
	private long pmId;

	@Column(columnDefinition="VARCHAR NOT NULL DEFAULT 'CASH'")
	private String pay_method;

	@Column(columnDefinition="VARCHAR NOT NULL DEFAULT 'SIMPLE'")
	private String institution_type;

	@Column(columnDefinition="NUMERIC")
	private Double balance;

	@Column(columnDefinition="NUMERIC")
	private Double credit_line;

	@Column(columnDefinition="NUMERIC")
	private Double cash_back;

	@Column(columnDefinition="VARCHAR")
	private String description;

	@Column(columnDefinition="VARCHAR")
	private String website;


	/* CONSTRUCTORS */
	public PayMethod() {
		super();		//spring needs default constructor
	}

	public PayMethod(PayMethod pm) {
		super();
		this.pmId = pm.pmId;
		this.pay_method = pm.pay_method;
		this.institution_type = pm.institution_type;
		this.balance = pm.balance;
		this.credit_line = pm.credit_line;
		this.cash_back = pm.cash_back;
		this.description = pm.description;
		this.website = pm.website;
	}

	//For generating a new pm_id
	public PayMethod(PayMethod pm, String userId) {
		super();
		this.pmId = generatePm_id(pm.pay_method, userId);
		this.pay_method = pm.pay_method;
		this.institution_type = pm.institution_type;
		this.balance = pm.balance;
		this.credit_line = pm.credit_line;
		this.cash_back = pm.cash_back;
		this.description = pm.description;
		this.website = pm.website;
	}

	public PayMethod(String userId, String pay_method, String institution_type) {
		super();
		this.pmId = generatePm_id(pay_method, userId);
		this.pay_method = pay_method;
		this.institution_type = institution_type;
	}

	/* END CONSTRUCTORS */


	//SETTERS

	private long generatePm_id(String pay_method, String userId) {
		return General.mergeHash(pay_method.hashCode(), userId.hashCode());
	}
}
