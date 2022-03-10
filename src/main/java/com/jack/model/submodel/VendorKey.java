package com.jack.model.submodel;

import java.io.Serializable;
import lombok.Data;

@Data
public class VendorKey implements Serializable {
	private String ccId;
	private String ccName;
	private String vendor;
	
}
