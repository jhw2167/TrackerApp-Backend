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
	//basic /transactions
	public ResponseEntity<List<Transaction>> getTransactions() {
		return new ResponseEntity<List<Transaction>>(ts.getAllTransactions(), HttpStatus.OK);
	}
	//END GET TRANSACTIONS
	
	
	//Get Transactions pageanated from params [start-end)
	/**
	 * @return ResponseEntity<List<Transaction>>
	 */
	@GetMapping(params = {"start", "to"} )
	@RequestMapping("/dates")
	public ResponseEntity<List<Transaction>> getTransactionsPageanatedByDate(@RequestParam final String start,
			@RequestParam final String to) {
		List<Transaction> tx = ts.getAllTransactionsBetweenPurchaseDate(LocalDate.parse(start), LocalDate.parse(to));
		return new ResponseEntity<>(tx, HttpStatus.OK);
	}
	
	
	//Get Transactions pageanated from params [offset-limit)
	/**
	 * @return ResponseEntity<List<Transaction>>
	 */
	@GetMapping(params = {"limit", "offset"} )
	@RequestMapping("/recent")
	public ResponseEntity<List<Transaction>> getTransactionsPageanatedByDate(@RequestParam final Long limit,
			@RequestParam(required=false) final Long offset) {
		List<Transaction> tx = ts.getAllTransactionsPageableID(limit, (offset != null) ? offset : 0L);
		return new ResponseEntity<>(tx, HttpStatus.OK);
	}
	
	
	@GetMapping
	@RequestMapping("/categories")
	public ResponseEntity<List<String>> getCategories() {
		List<String> cats = ts.getAllCategories();
		return new ResponseEntity<List<String>>(cats, HttpStatus.OK);
	}
	//END GET Categories
	
	
	@GetMapping(params = {"start", "to"} )
	@RequestMapping("/income")
	public ResponseEntity<List<IncomeTuple>> getIncomeSummary(@RequestParam final String start,
			@RequestParam final String to) {
		List<IncomeTuple> tx = ts.getIncomeAggregatedInDateRange(LocalDate.parse(start), LocalDate.parse(to)) ;
		return new ResponseEntity<List<IncomeTuple>>(tx, HttpStatus.OK);
	}
	//END GET Categories
	
	
	//END GET METHODS
	
		/* POST METHODS */
	
	//Post new transactions to the database - data send in will be transaction objects
	@PostMapping
	public ResponseEntity<String> postTransactions(@RequestBody final List<Transaction> tx) 
	{
		List<Transaction> refined = new ArrayList<>();
		tx.forEach( (t) -> {
			refined.add(
						ts.saveTransaction( new Transaction(t,
											ts.countByPurchaseDate(
											t.getPurchaseDate() ) ) ) ); } ); 
		
		final StringBuilder body = new StringBuilder("Transactions successfully posted: \n"); 
		refined.forEach((trans) -> body.append(trans.getTId() + "\n"));
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
	
	
	/*
	 * 
	 * 
	 */
	
	
	
	
	
	
	
	
	
	
	
	
	

}
