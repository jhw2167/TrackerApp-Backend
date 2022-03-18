package com.jack.service;

//Java Imports
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Spring Imports


//Project imports
import com.jack.repository.*;
import com.jack.model.*;

/* Service class for transactions handels all BUSINESS logic and is called from the 
 * controller class
 * 
 * 
 */

@Service
public class VendorService 
{

	/* State Variables */
	
	VendorRepo vr;
	VendorMapperRepo vmr;
	
	//END STATE VARS
	
	@Autowired
	VendorService(VendorRepo vendorRepo, VendorMapperRepo vendorMapperRepo) {
		this.vr = vendorRepo;
		this.vmr = vendorMapperRepo;
	}
	
	/* UTILITY METHODS */
	
	//Get all vendors as list
	public List<Vendor> getAllVendors() {
		return vr.findAll();
	}
	
	public List<Vendor> searchVendors(String vendorName) {
		return vr.findAllLikeVendorName(vendorName + "%");	//adding % to SQL like query finds all partial matches
	}
	
	
	//get vendor by id
	public Vendor getVendorByID(String cc_id, String cc) {
		Optional<VendorMapper> vm =  vmr.findVendorByID(cc_id, cc);
		if(vm.isEmpty())
			return null;
		
		return new Vendor(vm.get().getCcId(), vm.get().getCreditCard(),
				vr.findByVendor(vm.get().getLocalVendorName()));
	}
	//END GET VENDOR BY ID
	
}
//END CLASS TRANSACTIONSERVICE