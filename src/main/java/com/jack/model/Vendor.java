package com.jack.model;


import javax.annotation.Resource;

//JPA Imports
import javax.persistence.*;

import org.springframework.stereotype.Component;

//Lombok Imports
import lombok.*;

import com.fasterxml.jackson.annotation.JsonProperty;
//Project imports
import com.jack.repository.*;
import com.jack.model.submodel.VendorKey;



@Entity @Table(name="vendors")
@Data
public class Vendor {
	
	@Id
	private String localName;

	@Column(columnDefinition="NUMERIC(10, 2) NOT NULL DEFAULT '0' CHECK (amount>=0)")
	private double amount;
	
	@Column(columnDefinition="VARCHAR(50) NOT NULL DEFAULT 'Misc'")
	private String category;
	
	@Column(columnDefinition="BOOLEAN DEFAULT FALSE")
	@JsonProperty
	private boolean isTypicallyIncome; 
	
}
