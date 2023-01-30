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
	VendorNamesRepo vnr;
	
	//END STATE VARS
	
	@Autowired
	VendorService(VendorRepo vendorRepo, VendorNamesRepo vendorMapperRepo) {
		this.vr = vendorRepo;
		this.vnr = vendorMapperRepo;
	}
	
	/* UTILITY METHODS */
	
	//Get all vendors as list
	public List<Vendor> getAllVendors() {
		return vr.findAll();
	}
	
	public List<Vendor> searchVendors(String vendorName) {
		return vr.findAllLikeVendorName("%" + vendorName + "%");	//adding % to SQL like query finds all partial matches
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
		Optional<Vendor> searchedVendor = checkVendorExists(v.getVendor());
		return (searchedVendor.isPresent()) ? searchedVendor.get() : vr.save(v);
	}

}
//END CLASS TRANSACTIONSERVICE