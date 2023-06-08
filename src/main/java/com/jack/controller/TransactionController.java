package com.jack.controller;


//Spring Imports
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

//Java Imports
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
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
	@Autowired
	TransactionService ts;
	@Autowired
	VendorService vs;

	@Autowired
	UserAccountService us;

	@Autowired
	PayMethodService pms;


	/* Static variables */
	static final ObjectMapper MAPPER = new ObjectMapper();
	static {
		MAPPER.registerModule(new JavaTimeModule());
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		MAPPER.setDateFormat(df);
	}
	
	/* End State Variable Declarations */
//##################################################################
	
	//Constructor Injection
	
	
	/* Utility Methods */
	/**
	 *	Get all transaction by userId and tid
	 * @return ResponseEntity<Transaction>
	 */
	@RequestMapping(value="/{tid}", method=RequestMethod.GET)
	public ResponseEntity<Transaction> getTransactionByID(@PathVariable("userId") final String userId, @PathVariable("tid") final long tId) {
		return new ResponseEntity<Transaction>(ts.getTransactionByID(userId, tId), HttpStatus.OK);
	}
	//END GET TRANSACTION BY ID


	/**
	 *	Get all transactions, default sorted by date, descending (most recent to oldest) 
	 * 
	 * @return ResponseEntity<List<Transaction>>
	 */
	@GetMapping
	//basic /transactions
	public ResponseEntity<List<Transaction>> getUserTransactions(@PathVariable("userId") final String userId) {
		return new ResponseEntity<List<Transaction>>(ts.getAllUserTransactions(userId), HttpStatus.OK);
	}
	//END GET TRANSACTIONS
	
	
	//Get vendors by searching vendor nam
	/**
	 * @return ResponseEntity<List<Vendor>>
	 */
	@RequestMapping(value="/query", params = {"name"}, method=RequestMethod.GET)
	public ResponseEntity<List<Transaction>> searchTransactionsByVendorName(@PathVariable("userId") final String userId,
			@RequestParam final String name) {
		return new ResponseEntity<>(ts.searchVendors(userId, name), HttpStatus.OK);
	}
	//END GET VENDOR SEARCH

	@RequestMapping(value="/updateKeys", method=RequestMethod.POST)
	public ResponseEntity postTransKeys(@PathVariable("userId") final String userId) {
		// **TURNED OFF**
		//ts.postTransKeys(userId);
		//ts.postPmKeys(userId.toUpperCase());
		return new ResponseEntity<String>("NOT IMPLEMENTED - ENABLE IN SOURCE", HttpStatus.BAD_REQUEST);
	}
	
	//Get Transactions pageanated from params [start-end)
	/**
	 * @return ResponseEntity<List<Transaction>>
	 */
	@GetMapping(params = {"start", "to"} )
	@RequestMapping("/dates")
	public ResponseEntity<List<Transaction>> getTransactionsPageanatedByDate(
			@PathVariable("userId") final String userId,
			@RequestParam final String start,
			@RequestParam final String to) {
		List<Transaction> tx = ts.getAllTransactionsBetweenPurchaseDate(userId,
				LocalDate.parse(start), LocalDate.parse(to));
		return new ResponseEntity<>(tx, HttpStatus.OK);
	}
	
	
	//Get Transactions pageanated from params [offset-limit)
	/**
	 * @return ResponseEntity<List<Transaction>>
	 */
	@GetMapping(params = {"limit", "offset"} )
	@RequestMapping("/recent")
	public ResponseEntity<List<Transaction>> getTransactionsPageanatedByDate(@PathVariable("userId") final String userId,
			@RequestParam final String start, @RequestParam final Integer limit, @RequestParam(required=false) final Integer offset) {
		List<Transaction> tx = ts.getAllTransactionsPageableID(userId, limit, (offset != null) ? offset : 0);
		return new ResponseEntity<>(tx, HttpStatus.OK);
	}
	
	
	
	@GetMapping(params = {"start", "to"} )
	@RequestMapping("/income")
	public ResponseEntity<List<SummaryTuple>> getIncomeSummary(@PathVariable("userId") final String userId,
										   @RequestParam final String start, @RequestParam final String to) {
		List<SummaryTuple> tx = ts.getIncomeAggregatedInDateRange(userId,
				LocalDate.parse(start), LocalDate.parse(to)) ;
		return new ResponseEntity<List<SummaryTuple>>(tx, HttpStatus.OK);
	}
	//END GET INCOME SUMMARY
	
	
	@GetMapping(params = {"start", "to"} )
	@RequestMapping("/expenses")
	public ResponseEntity<List<SummaryTuple>> getExpenseSummary(@PathVariable("userId") final String userId,
											@RequestParam final String start, @RequestParam final String to) {
		List<SummaryTuple> tx = ts.getExpensesAggregatedInDateRange(userId,
				LocalDate.parse(start), LocalDate.parse(to)) ;
		return new ResponseEntity<List<SummaryTuple>>(tx, HttpStatus.OK);
	}
	//END GET INCOME SUMMARY
	
	@GetMapping
	@RequestMapping("/categories")
	public ResponseEntity<List<String>> getCategories(@PathVariable("userId") final String userId) {
		List<String> cats = ts.getAllCategories(userId);
		return new ResponseEntity<List<String>>(cats, HttpStatus.OK);
	}
	//END GET Categories
	
	@GetMapping
	@RequestMapping("/payMethods")
	public ResponseEntity<List<PayMethod>> getPayMethods(@PathVariable("userId") final String userId) {
		List<PayMethod> vals = ts.getPayMethods(userId);
		return new ResponseEntity<>(vals, HttpStatus.OK);
	}
	//END GET Categories
	
	
	@GetMapping
	@RequestMapping("/boughtFor")
	public ResponseEntity<List<String>> getBoughtFor(@PathVariable("userId") final String userId) {
		List<String> vals = ts.getBoughtFor(userId);
		return new ResponseEntity<List<String>>(vals, HttpStatus.OK);
	}
	//END GET Categories
	
	
	@GetMapping
	@RequestMapping("/payStatus")
	public ResponseEntity<List<String>> getPayStatus(@PathVariable("userId") final String userId) {
		List<String> vals = ts.getPayStatus(userId);
		return new ResponseEntity<List<String>>(vals, HttpStatus.OK);
	}
	//END GET Categories
	
	
	//END GET METHODS
	
		/* POST METHODS */
	
	//Post new transactions to the database - data send in will be transaction objects
	@PostMapping
	public ResponseEntity<String> postTransactions(@PathVariable("userId") final String userId,
												   @RequestBody final List<Transaction> tx)
	{
		//Make sure user exists else exit
		UserAccount user = us.getUserAccountById(userId);
		HttpStatus status = HttpStatus.OK;
		final StringBuilder body = new StringBuilder();
		final StringBuilder errorBody = new StringBuilder();

		//Build Transactions to save
		List<Transaction> refined = new ArrayList<>();
		try {

			for (Transaction t : tx ) {
				UserAccount u = us.getUserAccountById(userId);
				vs.saveVendor(new Vendor(t));
				pms.savePayMethod(t, u);


				try {
					refined.add(ts.saveTransaction( new Transaction(user, t,
							ts.countByPurchaseDate(userId,
									t.getPurchaseDate() ) ), u ) );

				} catch (Exception e) {
					body.append("ERROR saving transaction: \n" + MAPPER.writeValueAsString(t));
					body.append("\nERROR reported as:  " + e.getLocalizedMessage());
					body.append(",\n");
					status = HttpStatus.INTERNAL_SERVER_ERROR;
				}
			} //END FOR

		} catch (JsonProcessingException e) {
			System.out.println("Error Proccessing Transaction in Batch");
		}

		body.append("\n\nSuccessfully posted transactions with tids: \n");
		refined.forEach((trans) -> body.append(trans.getTid() + "\n"));
		ResponseEntity<String> rsp = new ResponseEntity<>(body.toString(), status);

		return rsp;
	}
	
	
	
	/* PUT METHODS */
	
	/* PATCH METHODS */
	
	//Patch a list of transactions entry
	@PatchMapping
	public ResponseEntity<String> patchTransactions(@PathVariable("userId") final String userId,
			@RequestBody final List<Transaction> tx)
	{
		StringBuilder body = new StringBuilder("Patched Transactions: [\n");

		UserAccount u = us.getUserAccountById(userId);
		HttpStatus status = HttpStatus.OK;
		List<Transaction> refined = new ArrayList<>();
		try {
			for (Transaction t : tx ) {
				vs.saveVendor(new Vendor(t));
				pms.savePayMethod(t, u);
				refined.add(ts.updateTransaction(t, u));
			}
		} catch(ResourceNotFoundException e) {
			body = new StringBuilder(e.getMessage());
			body.append("\n\nTransactions successfully patched: \n");
			status = HttpStatus.BAD_REQUEST;
		} catch(IllegalArgumentException e) {
			body = new StringBuilder(e.getMessage());
			body.append("\n\nTransactions successfully patched: \n");
			status = HttpStatus.BAD_REQUEST;
		}

		String openBrace = "[";
		for(Transaction t : refined ) {
			try {
				body.append(openBrace); openBrace=","; //if multiple transactions, they will be seperated by commas, JSON format
				body.append(MAPPER.writeValueAsString(t) + "\n");
			} catch (JsonProcessingException e) {
				System.out.println(e);
				body.append("Error converting transaction to JSON with id: " + t.getTid());
			}
		}
		body.append("]"); //Closing brace for JSON
		ResponseEntity<String> rsp = new ResponseEntity<>(body.toString(), status);
		return rsp;
	}
	
	
	
	
	/* DELETE METHODS */
	
	/*	Delete transaction with existing ID from the database
	 * 
	 * 	EXCEPTIONS:
	 * 		- NoSuchIDException
	 */
	@DeleteMapping(value="/{tid}")
	@ResponseBody
	public ResponseEntity<String> deleteTransaction(@PathVariable("userId") final String userId,
													@PathVariable("tid") final Long tid) {
		System.out.println("ID to delete:  " + tid);
		ts.deleteTransactionById(userId, tid);
		
		final String body = "Transaction with id: " + tid + " deleted";
		ResponseEntity<String> rsp = new ResponseEntity<>(body, HttpStatus.OK);
		return rsp;
	}

	
	/*
	 * Non-restful, need a method for mass deletion in case of impropper additions
	 * 
	 */
	@PatchMapping(value="/deleteAll")
	public ResponseEntity<String> deleteTransactions(@PathVariable("userId") final String userId,
													 @RequestBody final List<Long> tids)
	{
		HttpStatus status = HttpStatus.OK;

		StringBuilder s = new StringBuilder();
		for(Long tid : tids)
		{
			try {
				s.append( deleteTransaction(userId, tid).getBody() );
			} catch(ResourceNotFoundException e) {
				s.append("Error deleting tid " + tid + ", tid not found");
				status = HttpStatus.BAD_REQUEST;
			} catch (Exception e) {
				s.append("Error deleting tid " + tid + ", may not exist");
				status = HttpStatus.BAD_REQUEST;
			}
			s.append("\n");
		}
		ResponseEntity<String> rsp = new ResponseEntity<>(s.toString(), status);
		return rsp;
	}
}
//END TRANSACTION CONTROLLER