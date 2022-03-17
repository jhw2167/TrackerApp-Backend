package com.jack.service;

//Java Imports
import java.util.List;

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
	
	SimpleTransactionRepo repo;
	
	//END STATE VARS
	
	@Autowired
	VendorService(SimpleTransactionRepo repo) {
		this.repo = repo;
	}
	
	/* UTILITY METHODS */
	
	//Get all vendors as list
	public List<Vendor> getAllVendors() {
		return null;
	}
	
	public List<Vendor> searchVendors(String vendorName) {
		return null;
	}
	
	
	//get vendor by id
	public Vendor getVendorByID(String cc_id, String cc) {
		return null;
	}
}
//END CLASS TRANSACTIONSERVICE