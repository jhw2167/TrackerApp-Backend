package com.jack.model;

import javax.persistence.*;

import lombok.Data;


@Data
@Entity @Table(name="vendor_names")
public class VendorNames {

	@Id
	@Column(name = "cc_id")
	private String ccId;

	//@ManyToOne
    //@JoinColumn(name = "vendor", referencedColumnName = "vendor")
	@Column(name = "vendor")
	private String vendor;

	
	/* CONSTRUCTORS */
	public VendorNames() {
		super();
	}
	

	//END CONSTRUCTOR BUILD VENDOR_MAPPER FROM VENDOR
	//May need constructor to build VN from plaid_vendor object
	
	
}
//END CLASS VENDORMAPPER