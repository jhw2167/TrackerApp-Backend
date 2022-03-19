package com.jack.model;

import javax.persistence.*;

import lombok.Data;

//Project imports
import com.jack.model.submodel.VendorKey;

@Entity
@IdClass(VendorKey.class)
@Table(name="vendor_mapper")
@Data 
public class VendorMapper {

	@Id
	private String ccId;
	
	@Id
	private String creditCard;
	
	/*Foreign key constraint on  VendorMapper.localVendorName -> vendors.vendor
	 * 	- each unique creditCard/cc_id id combo maps to a friendly (local) vendor name
	 * 	- each local vendor name matches only *one* unqiue vendor name in Vendors table which
	 * 		generates predictive metrics for that vendor  
	 */
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vendor", referencedColumnName = "vendor")
	private Vendor vendor;

	
	/* CONSTRUCTORS */
	public VendorMapper() {
		super();
	}
	
	public VendorMapper(Vendor v) {
		super();
		this.ccId = v.getCc_id();
		this.creditCard = v.getCc();
		this.vendor = v;
	}
	//END CONSTRUCTOR BUILD VENDOR_MAPPER FROM VENDOR
	
	
}
//END CLASS VENDORMAPPER