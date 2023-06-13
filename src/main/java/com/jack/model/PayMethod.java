package com.jack.model;


//JPA Imports

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jack.utility.General;
import lombok.Data;

import javax.persistence.*;

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

	@ManyToOne
	@Column(name = "user_id", columnDefinition="VARCHAR NOT NULL DEFAULT '20230303JACKHENRYWELSH@GMAIL.COM'")
	@JsonProperty("userAccount")
	private UserAccount user;

	@JsonProperty("payMethod")
	@Column(name="pay_method", columnDefinition="VARCHAR NOT NULL DEFAULT 'CASH'")
	private String payMethodName;

	@JsonProperty("institutionType")
	@Column(name="institution_type", columnDefinition="VARCHAR NOT NULL DEFAULT 'SIMPLE'")
	private String institutionType;

	@JsonProperty("balance")
	@Column(name="balance", columnDefinition="NUMERIC")
	private Double balance;

	@JsonProperty("creditLine")
	@Column(name="credit_line", columnDefinition="NUMERIC")
	private Double creditLine;

	@JsonProperty("cashBack")
	@Column(name="cash_back", columnDefinition="NUMERIC")
	private Double cashBack;

	@JsonProperty("description")
	@Column(name="description", columnDefinition="VARCHAR")
	private String description;

	@JsonProperty("website")
	@Column(name="website", columnDefinition="VARCHAR")
	private String website;


	/* CONSTRUCTORS */
	public PayMethod() {
		super();		//spring needs default constructor
	}

	public PayMethod(PayMethod pm) {
		super();
		setPmId(pm.pmId);
		setPayMethodName(pm.payMethodName);
		setInstitutionType(pm.institutionType);
		setBalance(pm.balance);
		setCreditLine(pm.creditLine);
		setCashBack(pm.cashBack);
		setDescription(pm.description);
		setWebsite(pm.website);
	}


	//For generating a new pm_id
	public PayMethod(PayMethod pm, UserAccount u) {
		this(pm);
		this.pmId = generatePmId(pm.payMethodName, user.getUserId());
	}

	public PayMethod(String payMethod) {
		setPayMethodName(payMethod);
	}
	public PayMethod(UserAccount u, String payMethod) {
		this(u, payMethod, null);
	}

	public PayMethod(UserAccount u, String payMethod, String institutionType) {
		setPmId(generatePmId(payMethod, u.getUserId()));
		setPayMethodName(payMethod);
		setInstitutionType(institutionType);
	}

	/* END CONSTRUCTORS */


	//SETTERS

	public void setPayMethodName(String pm) {
		this.payMethodName = (pm==null || pm.isEmpty()) ? "CASH" : pm;
	}

	public void setInstitutionType(String instType) {
		this.payMethodName = (instType==null || instType.isEmpty()) ? "SIMPLE" : instType;
	}

	private long generatePmId(String pay_method, String userId) {
		return General.mergeHash(pay_method.hashCode(), userId.hashCode());
	}
}
