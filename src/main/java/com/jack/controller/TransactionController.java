package com.jack.controller;


//Spring Imports
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
//Java Imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
@RequestMapping("/finances/users/{userId}/transactions")
public class TransactionController {
	
	
	/* State Variables */
	TransactionService ts;		//autowired with spring
	VendorService vs;			//auto
	
	/* Static variables */
	static final ObjectMapper MAPPER = new ObjectMapper();
	static {
		MAPPER.registerModule(new JavaTimeModule());
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		MAPPER.setDateFormat(df);
	}
	
	@Autowired
	TransactionController(TransactionService ts, VendorService vs) {
		this.ts = ts;
		this.vs = vs;
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
	
	
	//Get vendors by searching vendor nam
	/**
	 * @return ResponseEntity<List<Vendor>>
	 */
	@RequestMapping(value="/query", params = {"name"}, method=RequestMethod.GET)
	public ResponseEntity<List<Transaction>> searchTransactionsByVendorName(@RequestParam final String name) {
		return new ResponseEntity<>(ts.searchVendors(name), HttpStatus.OK);
	}
	//END GET VENDOR SEARCH
	
	
	//Get vendors by searching vendor nam
	/**
	 * @return ResponseEntity<Vendor>
	 */
	@RequestMapping(value="/{tid}", method=RequestMethod.GET)
	public ResponseEntity<Transaction> getVendorByID(@PathVariable("userId") final String userId, @PathVariable("tid") final long tId) {
		return new ResponseEntity<>(ts.getTransactionByID(userId, tId), HttpStatus.OK);
	}
	//END GET VENDOR SEARCH BY ID
	
	
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
	
	
	
	@GetMapping(params = {"start", "to"} )
	@RequestMapping("/income")
	public ResponseEntity<List<SummaryTuple>> getIncomeSummary(@RequestParam final String start,
			@RequestParam final String to) {
		List<SummaryTuple> tx = ts.getIncomeAggregatedInDateRange(LocalDate.parse(start), LocalDate.parse(to)) ;
		return new ResponseEntity<List<SummaryTuple>>(tx, HttpStatus.OK);
	}
	//END GET INCOME SUMMARY
	
	
	@GetMapping(params = {"start", "to"} )
	@RequestMapping("/expenses")
	public ResponseEntity<List<SummaryTuple>> getExpenseSummary(@RequestParam final String start,
			@RequestParam final String to) {
		List<SummaryTuple> tx = ts.getExpensesAggregatedInDateRange(LocalDate.parse(start), LocalDate.parse(to)) ;
		return new ResponseEntity<List<SummaryTuple>>(tx, HttpStatus.OK);
	}
	//END GET INCOME SUMMARY
	
	@GetMapping
	@RequestMapping("/categories")
	public ResponseEntity<List<String>> getCategories() {
		List<String> cats = ts.getAllCategories();
		return new ResponseEntity<List<String>>(cats, HttpStatus.OK);
	}
	//END GET Categories
	
	@GetMapping
	@RequestMapping("/payMethods")
	public ResponseEntity<List<String>> getPayMethods() {
		List<String> vals = ts.getPayMethods();
		return new ResponseEntity<List<String>>(vals, HttpStatus.OK);
	}
	//END GET Categories
	
	
	@GetMapping
	@RequestMapping("/boughtFor")
	public ResponseEntity<List<String>> getBoughtFor() {
		List<String> vals = ts.getBoughtFor();
		return new ResponseEntity<List<String>>(vals, HttpStatus.OK);
	}
	//END GET Categories
	
	
	@GetMapping
	@RequestMapping("/payStatus")
	public ResponseEntity<List<String>> getPayStatus() {
		List<String> vals = ts.getPayStatus();
		return new ResponseEntity<List<String>>(vals, HttpStatus.OK);
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
			vs.saveVendor(new Vendor(t.getVendor(), 0d, t.getCategory(), t.isIncome()));
			refined.add(ts.saveTransaction( new Transaction(t,
											ts.countByPurchaseDate(
											t.getPurchaseDate() ) ) ) ); } ); 
		
		final StringBuilder body = new StringBuilder("Transactions successfully posted: \n"); 
		refined.forEach((trans) -> body.append(trans.getTId() + "\n"));
		ResponseEntity<String> rsp = new ResponseEntity<>(body.toString(), HttpStatus.OK);
		return rsp;
	}
	
	
	
	/* PUT METHODS */
	
	/* PATCH METHODS */
	
	//Patch a list of transactions entry
	@PatchMapping
	public ResponseEntity<String> patchTransactions(@RequestBody final List<Transaction> tx)
	{
		StringBuilder body = new StringBuilder("Patched Transactions: \n");
		HttpStatus status = HttpStatus.OK;
		List<Transaction> refined = new ArrayList<>();
		try {
			tx.forEach( (t) -> refined.add(ts.updateTransaction(t)) );
		} catch(ResourceNotFoundException e) {
			body = new StringBuilder(e.getMessage());
			body.append("\n\nTransactions successfully patched: \n");
			status = HttpStatus.NOT_FOUND;
		}
		
		for(Transaction t : refined ) {
			try {
				body.append(MAPPER.writeValueAsString(t) + "\n");
			} catch (JsonProcessingException e) {
				System.out.println(e);
				body.append("Error converting transaction to JSON with id: " + t.getTId());
			}
		}
		
		ResponseEntity<String> rsp = new ResponseEntity<>(body.toString(), status);
		return rsp;
	}
	
	
	
	
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
	 * Non-restful, need a method for mass deletion in case of impropper additions
	 * 
	 */
	@PostMapping(value="/deleteAll")
	public ResponseEntity<String> deleteTransactions(@RequestBody final List<Long> ids) 
	{
		StringBuilder s = new StringBuilder();
		for(Long id : ids) 
		{
			try {
				s.append( deleteTransaction(id).getBody() );
			} catch (Exception e) {
				s.append("Error deleting id " + id + ", may not exist");
			}
			s.append("\n");
		}
		ResponseEntity<String> rsp = new ResponseEntity<>(s.toString(), HttpStatus.OK);
		return rsp;
	}
}
//END TRANSACTION CONTROLLER