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
		return vr.findAllLikeVendorName("%" + vendorName + "%");	//adding % to SQL like query finds all partial matches
	}
	
	
	//get vendor by id
	public Vendor getVendorByID(String cc_id, String cc) {
		Optional<VendorMapper> vm =  vmr.findVendorByID(cc_id, cc);
		if(!vm.isPresent())
			return null;
		
		//set values in vendor
		Vendor internalVendor = vm.get().getVendor();
		internalVendor.setCc(vm.get().getCreditCard());
		internalVendor.setCc_id(vm.get().getCcId());
		
		return vm.get().getVendor();
	}
	//END GET VENDOR BY ID
	
	/* 
	 * POST METHODS 
	 * 
	 */
	
	/*
	 * 	saveVendor - typically used to save vendor-mapper value for auto-
	 * 	generating front-end values from Plaid vendors
	 * 
	 * 	We attempt to save vendor to vendors table first because
	 *  there is a foreign key constraint on vendorMapper.vendors -> vendor.vendors
	 */
	public Vendor saveVendor(final Vendor v) {
		VendorMapper saved = null;
		if(vr.findByVendor(v.getVendor()) != null) {
			vr.save(v);
		}
		//Foreign key constraint on  VendorMapper.localVendorName -> vendors.vendor
		//which means vendor needs to be saved first
					
		
		//now this vendor definitely exists in the table
		if(v.getCc() != null && v.getCc_id() != null) {
			saved = vmr.save(new VendorMapper(v));
			Vendor internalVendor = saved.getVendor();
			internalVendor.setCc(saved.getCreditCard());
			internalVendor.setCc_id(saved.getCcId());
		}
		 return saved.getVendor();					
	}
	
}
//END CLASS TRANSACTIONSERVICE