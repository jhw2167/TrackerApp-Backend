package com.jack.controller;


//Spring Imports

import com.jack.model.Transaction;
import com.jack.model.UserAccount;
import com.jack.model.Vendor;
import com.jack.service.TransactionService;
import com.jack.service.UserAccountService;
import com.jack.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


//

/*
 * Table controller serves endpoint finances/tables/{transactions | users | vendors} for direct access
 * to tables, useful for debugging, not business purposes
 * @author Jack Welsh 6/8/2023
 */


//Combines @Controller and @ResponseBody annotations for a restful project
@RestController
@RequestMapping("api/finances/tables")
public class TableController {


	/* State Variables */
	@Autowired
	TransactionService ts;		//autowired with spring

	@Autowired
	VendorService vs;			//auto

	@Autowired
	UserAccountService us;


	/* Utility Methods */

	/* Get Methods */
	@RequestMapping(value="/transactions/{trueId}", method= RequestMethod.GET)
	public ResponseEntity<Transaction> getTransactionByTrueId(@PathVariable("trueId") final long id) {
		return new ResponseEntity<Transaction>(ts.getTransactionByTrueId(id), HttpStatus.OK);
	}

	/*********************
	 		ACCOUNTS
	 ********************/

	//Method to get all active accounts
	/**
	 * @return ResponseEntity<List<UserAccount>>
	 */
	@RequestMapping(value="users", method=RequestMethod.GET)
	public ResponseEntity<List<UserAccount>> getAllUsers(HttpServletRequest request)
	{
		return new ResponseEntity<List<UserAccount>>(us.getAllUserAccounts(), HttpStatus.OK);
	}

	/*********************
			VENDORS
	 ********************/

	/**
	 *	Get all vendors
	 *
	 * @return ResponseEntity<List<Vendor>>
	 */
	@RequestMapping(value="vendors", method=RequestMethod.GET)
	public ResponseEntity<List<Vendor>> getAllVendors(HttpServletRequest request)
	{
		return new ResponseEntity<List<Vendor>>(vs.getAllVendors(), HttpStatus.OK);
	}

	//Get vendors by searching vendor name
	/**
	 * @return ResponseEntity<List<Vendor>>
	 */
	@RequestMapping(value="vendors/query", params = {"name"}, method=RequestMethod.GET)
	public ResponseEntity<List<Vendor>> searchVendorsByName(HttpServletRequest request,
															@RequestParam final String name)
	{
		return new ResponseEntity<>(vs.searchVendors(name), HttpStatus.OK);
	}

	//Get vendors by searching vendor nam
	/**
	 * @return ResponseEntity<Vendor>
	 */
	@RequestMapping(value="vendors/query", params = {"id", "cc"}, method=RequestMethod.GET)
	public ResponseEntity<Vendor> getVendorByID( @RequestParam final String id,
												 @RequestParam final String cc)
	{
		return new ResponseEntity<>(vs.getVendorByID(id), HttpStatus.OK);
	}


	/* PUT METHODS */
	
	/* PATCH METHODS */
	
	/* DELETE METHODS */


}
//END USER CONTROLLERS