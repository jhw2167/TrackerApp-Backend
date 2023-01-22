package com.jack.model;

import javax.persistence.*;

import lombok.Data;

//Project imports
import com.jack.model.submodel.VendorKey;

@Entity
@IdClass(VendorKey.class)
@Table(name="vendor_names")
@Data 
public class VendorNames {

	@Id
	@Column(name = "cc_id")
	private String ccId;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vendor", referencedColumnName = "vendor")
	private Vendor vendor;

	
	/* CONSTRUCTORS */
	public VendorNames() {
		super();
	}
	
	public VendorNames(Vendor v) {
		super();
		this.ccId = v.getCc_id();
		this.vendor = v;
	}
	//END CONSTRUCTOR BUILD VENDOR_MAPPER FROM VENDOR
	
	
}
//END CLASS VENDORMAPPER