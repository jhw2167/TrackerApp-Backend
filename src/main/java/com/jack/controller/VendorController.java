package com.jack.controller;


//Spring Imports
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
//Java Imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

//Project Imports
import com.jack.model.*;
import com.jack.service.*;

import javax.servlet.http.HttpServletRequest;


//

/*
 * Transaction controller servs endpoint /transactions/*** to deliver data to the front end
 * based on endpoint call 
 * 
 * @author Jack Welsh 8/11/2021
 */


//Combines @Controller and @ResponseBody annotations for a restful project
@RestController
@RequestMapping("api/finances/users/{userId}/vendors")
public class VendorController {
	
	
	/* State Variables */
	TransactionService ts;		//autowired with spring
	VendorService vs;			//auto
	
	
	/* End State Variable Declarations */
//##################################################################
	
	//Constructor Injection
	@Autowired
	VendorController(TransactionService ts, VendorService vs) {
		this.ts = ts;
		this.vs = vs;
	}
	
	
	/* Utility Methods */
	
	
	/*
	 *		 enpoints
	 *		GET and POST information on vendors for POST endpoint autofill
	 */
	
	


	/**
	 *	Get all vendors by user
	 *
	 * @return ResponseEntity<List<Vendor>>
	 */
	
	//basic /transactions
	@RequestMapping(value="", method=RequestMethod.GET)
	public ResponseEntity<List<Vendor>> getAllVendors(HttpServletRequest request,
													  @PathVariable("userId") final String userId)
	{
		return new ResponseEntity<List<Vendor>>(vs.getAllVendors(userId), HttpStatus.OK);
	}	
	//END GET ALL VENDORS


	@RequestMapping(value="/query", params = {"name"}, method=RequestMethod.GET)
	public ResponseEntity<List<Vendor>> searchVendorsByName(HttpServletRequest request,
													@PathVariable("userId") final String userId,
													@RequestParam final String name)
	{
		return new ResponseEntity<>(vs.searchVendors(userId, name), HttpStatus.OK);
	}
	//END GET VENDOR SEARCH
	

	@RequestMapping(value="/query", params = {"id", "cc"}, method=RequestMethod.GET)
	public ResponseEntity<Vendor> getVendorByID( @PathVariable("userId") final String userId,
			@RequestParam final String cc,
			@RequestParam final String id)
	{
		return new ResponseEntity<>(vs.getVendorByID(id), HttpStatus.OK);
	}
	//END GET VENDOR SEARCH BY ID
	
	/* POST METHODS */
	
	@PostMapping
	public ResponseEntity<Vendor> postTransactions(@PathVariable("userId") final String userId,
												   @RequestBody final Vendor vm) {

		Vendor o = null;
		try {
			o = vs.saveVendor(vm);
		}catch (Exception e) {
			System.out.println("Multi-save Exception: " + e.getMessage());
		}
		
		return new ResponseEntity<Vendor>(o, HttpStatus.OK);
	}
	
	/* PUT METHODS */
	
	/* PATCH METHODS */
	
	/* DELETE METHODS */
	
	
	
	
	
	
	/*
	 * 
	 * 
	 */
	
	
	

}
