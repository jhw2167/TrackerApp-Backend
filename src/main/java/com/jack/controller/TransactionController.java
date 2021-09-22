package com.jack.controller;


//Spring Imports
import org.springframework.web.bind.annotation.*;

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
import com.jack.service.TransactionService;


//

/*
 * Transaction controller servs endpoint /transactions/*** to deliver data to the front end
 * based on endpoint call 
 * 
 * @author Jack Welsh 8/11/2021
 */


//Combines @Controller and @ResponseBody annotations for a restful project
@RestController
@RequestMapping("/transactions")
public class TransactionController {
	
	
	/* State Variables */
	TransactionService ts;		//autowired with spring
	
	@Autowired
	TransactionController(TransactionService ts) {
		this.ts = ts;
	}
	
	/* End State Variable Declarations */
//##################################################################
	
	//Constructor Injection
	
	
	/* Utility Methods */
	
	/**
	 *	Get all transactions, default sorted by date, descending (most recent to oldest) 
	 * 
	 * @return ResponseEntity<List<Transaction>>
	 */
	@GetMapping
	public ResponseEntity<List<Transaction>> getTransactions() {
		
		//get data
		List<Transaction> tx = ts.getAllTransactions();

		//Build Response entity
		return new ResponseEntity<List<Transaction>>(tx, HttpStatus.OK);
	}
	//END GET TRANSACTIONS
	
	
	//Get Transactions pageanated from params start-end inclusive
	/**
	 * @return ResponseEntity<List<Transaction>>
	 */
	@GetMapping(params = {"start", "to"} )
	public ResponseEntity<List<Transaction>> getTransactionsPageanatedById() {
		return new ResponseEntity<>(new ArrayList<Transaction>(), HttpStatus.OK);
	}
	
	//END GET METHODS
	
		/* POST METHODS */
	
	//Post new transactions to the database - data send in will be transaction objects
	@PostMapping
	public ResponseEntity<String> postTransactions(@RequestBody final List<Transaction> tx) {
		System.out.println(tx);
		ts.saveTransactions(tx);
		
		final StringBuilder body = new StringBuilder("Transactions successfully posted: "); 
		tx.forEach((trans) -> body.append(trans.getTId() + "\n"));
		ResponseEntity<String> rsp = new ResponseEntity<>(body.toString(), HttpStatus.OK);
		return rsp;
	}
	
	
	
	/* PUT METHODS */
	
	/* PATCH METHODS */
	
	/* DELETE METHODS */
	
	/*	Delete transaction with existing ID from the database
	 * 
	 * 	EXCEPTIONS:
	 * 		- NoSuchIDException
	 */
	@DeleteMapping(value="transactions/{id}")
	public ResponseEntity<String> deleteTransaction(@RequestParam final long id) {
		System.out.println("ID to delete:  " + id);
		ts.deleteTransactionById(id);
		
		final String body = "Transaction with id: " + id + " deleted";
		ResponseEntity<String> rsp = new ResponseEntity<>(body, HttpStatus.OK);
		return rsp;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
