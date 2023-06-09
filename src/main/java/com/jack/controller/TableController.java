package com.jack.controller;


//Spring Imports

import com.jack.model.Transaction;
import com.jack.model.UserAccount;
import com.jack.service.TransactionService;
import com.jack.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


//

/*
 * Table controller serves endpoint finances/tables/{transactions | users | vendors} for direct access
 * to tables, useful for debugging, not business purposes
 * @author Jack Welsh 6/8/2023
 */


//Combines @Controller and @ResponseBody annotations for a restful project
@RestController
@RequestMapping("/finances/tables")
public class TableController {


	/* State Variables */
	@Autowired
	TransactionService ts;


	/* Utility Methods */

	/* Get Methods */
	@RequestMapping(value="/transactions/{trueId}", method= RequestMethod.GET)
	public ResponseEntity<Transaction> getTransactionByTrueId(@PathVariable("trueId") final long id) {
		return new ResponseEntity<Transaction>(ts.getTransactionByTrueId(id), HttpStatus.OK);
	}
	/* PUT METHODS */
	
	/* PATCH METHODS */
	
	/* DELETE METHODS */


}
//END USER CONTROLLERS