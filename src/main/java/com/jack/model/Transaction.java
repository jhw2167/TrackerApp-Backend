package com.jack.model;

//Java imports
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.*;

//Spring Imports


//JPA Imports


//Lombok Imports
import com.jack.model.dto.TransactionDto;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

//Project imports
import com.jack.utility.General;

/* Transaction model class for holding data in each transaction as read from the database
 * we will annotate our values with JPA to be persisted correctly

*
 	t_id 			INTEGER 		PRIMARY KEY,
	true_id			INTEGER 		NOT NULL,
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

	public static final Map<String, String> DEF_VALUES = new HashMap<String, String>() {{
		put("PAY_METHOD", "CASH");
		put("PAY_STATUS", "COMPLETE");
		put("CATEGORY", "MISC");
		put("BOUGHT_FOR", "PERSONAL");
	}};

	/* PERSISTED  STATE VARIABLES */

	@Id
	@Column(name = "true_id", columnDefinition="NUMERIC PRIMARY KEY")
	@JsonProperty("trueId")
	private long trueId;

	/* This addition prevents this table from being 3NF, as (tid, user_id) -> is a primary key,
	however, it makes our code implementation and queries much easier */
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", columnDefinition="VARCHAR NOT NULL")
	//@Column(name = "user_id", columnDefinition="VARCHAR NOT NULL DEFAULT '20230303JACKHENRYWELSH@GMAIL.COM'")
	@JsonProperty("userAccount")
	private UserAccount user;

	@JsonProperty("tid")
	@Column(name="t_id", columnDefinition="NUMERIC NOT NULL")
	private long tid;

	@JsonProperty("purchaseDate")
	@Column(name="purchase_date", columnDefinition="DATE NOT NULL DEFAULT CURRENT_DATE")
	private LocalDate purchaseDate;

	@JsonProperty("amount")
	@Column(name="amount", columnDefinition="NUMERIC(10, 2) NOT NULL DEFAULT '0' CHECK (amount>=0)")
	private double amount;

	@JsonProperty("vendor")
	@Column(name="vendor", columnDefinition="VARCHAR(50) NOT NULL")	//references vendors
	private String vendor;

	@JsonProperty("category")
	@Column(name="category", columnDefinition="VARCHAR(50) NOT NULL DEFAULT 'Misc'")
	private String category;

	@JsonProperty("boughtFor")
	@Column(name="bought_for", columnDefinition="VARCHAR(20) NOT NULL DEFAULT 'PERSONAL'")
	private String boughtFor;

//	@Transient
//	@JsonProperty("payMethod")
//	private String payMethodString;

	@ManyToOne
	@JoinColumn(name = "pm_id", referencedColumnName = "pm_id", columnDefinition="NUMERIC NOT NULL DEFAULT 0")
	//@Column(name = "pm_id", columnDefinition="NUMERIC NOT NULL DEFAULT 0")
	@JsonProperty("payMethod")
	private PayMethod payMethod;

	@JsonProperty("payStatus")
	@Column(name="pay_status", columnDefinition="VARCHAR(20) NOT NULL DEFAULT 'COMPLETE'")
	private String payStatus;

	@JsonProperty("income")
	@Column(name="is_income", columnDefinition="BOOLEAN DEFAULT FALSE")
	private boolean isIncome;

	@JsonProperty("reimburses")
	@Column(name="reimburses", columnDefinition="INTEGER REFERENCES transactions(t_id) DEFAULT 0")
	private long reimburses;

	@JsonProperty("postedDate")
	@Column(name="posted_date", columnDefinition="DATE NOT NULL DEFAULT CURRENT_DATE")
	private LocalDate postedDate;

	@JsonProperty("notes")
	@Column(name="notes", columnDefinition="VARCHAR(1024) DEFAULT NULL")
	private String notes;

	//End State Vars
	//###############################################
	
	//Constructors:
	public Transaction() {
		//Dummy constr
	}
	
	public Transaction(final Transaction t) {
		super();
		setTid(t.tid);
		setTrueId(t.trueId);
		setUser(t.user);
		setPurchaseDate(t.purchaseDate);
		setAmount(t.amount);

		setVendor(t.vendor);
		setCategory(t.category);
		setBoughtFor(t.boughtFor);
		//setPayMethodString(t.payMethodString);
		setPayMethod(t.payMethod);
		setPayStatus(t.payStatus);
		setIncome(t.isIncome);
		setReimburses(t.reimburses);
		setPostedDate(t.postedDate);
		setNotes(t.notes);
	}

	/*
		-Create transaction from immature transaction submitted in POST calls
		"Immature" transactions need their TIDs created
	 */
	public Transaction(final UserAccount u, final Transaction t, final long transOnDate) {
		this(t);
		//System.out.println("My Constructor");
		setUser(u);
		settId(t.purchaseDate.toString(), transOnDate);
		setTrueId(u.getUserId(), this.tid);
	}

	public Transaction(final TransactionDto t, final UserAccount u, final PayMethod pm) {
		setTid(t.getTid());
		setTrueId(t.getTrueId());
		setUser(u);
		setPurchaseDate(t.getPurchaseDate());
		setAmount(t.getAmount());

		setVendor(t.getVendor());
		setCategory(t.getCategory());
		setBoughtFor(t.getBoughtFor());
		setPayMethod(pm);
		setPayStatus(t.getPayStatus());
		setIncome(t.isIncome());
		setReimburses(t.getReimburses());
		setPostedDate(t.getPostedDate());
		setNotes(t.getNotes());
	}

	/* END CONSTRUCTORS */

	
	//Setters

	public void setTrueId(String userId, long tid) {
		this.trueId = General.mergeHash(userId.hashCode(), new Long(tid).hashCode());
		this.trueId = Math.abs(trueId);
		System.out.println("Merged id: " + trueId);
	}
	public void setTid(long tid) {
		this.tid = tid;
	}
	public void settId(String _purchaseDate, long transOnDate) {
		//parse date string into coherent string
		String date = _purchaseDate.replace("-", "");
		
		//add above result, buffered by "000"
		String id = date + String.format("%03d", transOnDate);
		this.tid = Long.parseLong(id);
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

	/*private void setPayMethodString(String pms) {
		this.payMethodString = pms.equals("") ? "CASH" : pms.toUpperCase();
	}
	*/

	private void setPayStatus(String ps) {
		this.payStatus = (ps==null || ps.equals("")) ? DEF_VALUES.get("PAY_STATUS")
				: ps.toUpperCase();
	}

	private void setBoughtFor(String bf) {
		this.boughtFor = bf.equals("") ? DEF_VALUES.get("BOUGHT_FOR") : bf.toUpperCase();
	}

	private void setCategory(String cat) {
		this.category = cat.equals("") ? DEF_VALUES.get("CATEGORY") : cat.toUpperCase();
	}
	
	//END SETTERS


	//Utility Methods
	public static boolean compareIds(Transaction a, Transaction b) {
		return (a.trueId==b.trueId) && (a.tid ==b.tid);
	}

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
