package com.jack.service;

//Java Imports
import java.util.List;
import java.util.Optional;

import ch.qos.logback.core.net.SyslogOutputStream;
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

	@Autowired
	VendorRepo repo;
	@Autowired
	VendorNamesRepo vnr;
	
	//END STATE VARS

	
	/* UTILITY METHODS */
	
	//Get all vendors by userId as list
	public List<Vendor> getAllVendors() {
		return repo.findAll();
	}
	public List<Vendor> getAllVendors(String userId) {
		return repo.findAllByUserUserId(userId);
	}
	
	public List<Vendor> searchVendors(String vendorName) {
		return repo.findAllLikeVendorName("%" + vendorName + "%");	//adding % to SQL like query finds all partial matches
	}

	public List<Vendor> searchVendors(String userId, String vendorName) {
		return repo.findAllByUserUserIdLikeVendorName(userId,"%" + vendorName + "%");	//adding % to SQL like query finds all partial matches
	}

	public Optional<Vendor> checkVendorExists(String vendorName) {
		List<Vendor> v = searchVendors(vendorName);
		return Optional.ofNullable( ( v.isEmpty() ) ? null : v.get(0) );
	}
	
	//get vendor by id
	public Vendor getVendorByID(String cc_id) {
		return null;
	}
	//END GET VENDOR BY ID
	
	/* 
	 * POST METHODS 
	 * 
	 */

	/**
	 * Checks to see a vendor exists before saving it to DB
	 * Returns searched vendor if found in the table
	 * @param v
	 * @return Vendor
	 *
	 */
	public Vendor saveVendor(final Vendor v) {
		Vendor searchedVendor = repo.findByUserUserIdAndVendorName(v.getUser().getUserId(), v.getVendorName());
		return (searchedVendor!=null) ? searchedVendor : repo.save(v);
	}

}
//END CLASS TRANSACTIONSERVICE