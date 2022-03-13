package com.jack.model;

import javax.persistence.*;

import lombok.Data;

//Project imports
import com.jack.model.submodel.VendorKey;

@Entity
@IdClass(VendorKey.class)
@Data 
public class VendorMapper {

	@Id
	private String ccId;
	
	@Id
	private String creditCard;
	
	@Id
	private String localVendorName;
	
}
