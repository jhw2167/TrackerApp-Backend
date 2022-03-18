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


//

/*
 * Transaction controller servs endpoint /transactions/*** to deliver data to the front end
 * based on endpoint call 
 * 
 * @author Jack Welsh 8/11/2021
 */


//Combines @Controller and @ResponseBody annotations for a restful project
@RestController
@RequestMapping("/transactions/vendors")
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
	 *		/transactions/vendor enpoints 
	 *		GET and POST information on vendors for POST endpoint autofill
	 */
	
	
	/**
	 *	Get all transactions, default sorted by date, descending (most recent to oldest) 
	 * 
	 * @return ResponseEntity<List<Transaction>>
	 */

	
	//basic /transactions
	@GetMapping
	@RequestMapping("/vendors")
	public ResponseEntity<List<Vendor>> getVendors() {
		return new ResponseEntity<List<Vendor>>(vs.getAllVendors(), HttpStatus.OK);
	}	
	//END GET ALL VENDORS
	
	
	//Get vendors by searching vendor nam
	/**
	 * @return ResponseEntity<List<Vendor>>
	 */
	@RequestMapping(value="/vendors", params = {"name"})
	public ResponseEntity<List<Vendor>> searchVendorsByName(@RequestParam final String name) {
		return new ResponseEntity<>(vs.searchVendors(name), HttpStatus.OK);
	}
	//END GET VENDOR SEARCH
	
	
	//Get vendors by searching vendor nam
	/**
	 * @return ResponseEntity<Vendor>
	 */
	@RequestMapping(value="/vendors", params = {"id", "cc"})
	public ResponseEntity<Vendor> getVendorByID(@RequestParam final String id, @RequestParam final String cc) {
		return new ResponseEntity<>(vs.getVendorByID(id, cc), HttpStatus.OK);
	}
	//END GET VENDOR SEARCH BY ID
	
	
	/* PUT METHODS */
	
	/* PATCH METHODS */
	
	/* DELETE METHODS */
	
	
	
	
	
	
	/*
	 * 
	 * 
	 */
	
	
	

}
