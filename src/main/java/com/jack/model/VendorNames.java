package com.jack.model;

import javax.persistence.*;

import lombok.Data;



@Entity
@Table(name="vendor_names")
@Data 
public class VendorNames {

	@Id
	@Column(name = "cc_id")
	private String ccId;

	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vendor", referencedColumnName = "vendor")
	private Vendor vendor;

	
	/* CONSTRUCTORS */
	public VendorNames() {
		super();
	}
	

	//END CONSTRUCTOR BUILD VENDOR_MAPPER FROM VENDOR
	//May need constructor to build VN from plaid_vendor object
	
	
}
//END CLASS VENDORMAPPER