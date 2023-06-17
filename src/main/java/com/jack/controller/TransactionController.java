package com.jack.controller;


//Spring Imports
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

//Java Imports
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//Project Imports
import com.jack.model.dto.TransactionDto;
import com.jack.model.*;
import com.jack.service.*;
import com.jack.model.dto.mapper.TransactionMapper;
import com.jack.utility.HttpMultiStatusResponse;
import com.jack.utility.HttpUnitResponse;



//

/**
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

	//Other Useful classes
	@Autowired
	TransactionMapper transactionMapper;


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
	 * Get all transaction by userId and tid
	 *
	 * @return ResponseEntity<TransactionDto>
	 */
	@RequestMapping(value = "/{tid}", method = RequestMethod.GET)
	public ResponseEntity<TransactionDto> getTransactionByID(HttpServletRequest request,
															 @PathVariable("userId") final String userId,
															 @PathVariable("tid") final long tId) {
		return new ResponseEntity<>(
				transactionMapper.toDto(ts.getTransactionByID(userId, tId)),
				HttpStatus.OK);
	}
// END GET TRANSACTION BY ID



	/**
	 *	Get all transactions, default sorted by date, descending (most recent to oldest)
	 *
	 * @return ResponseEntity<List<TransactionDto>>
	 */
	@GetMapping
	//basic /transactions
	public ResponseEntity<List<TransactionDto>> getUserTransactions(HttpServletRequest request, @PathVariable("userId") final String userId) {
		List<Transaction> tx = ts.getAllUserTransactions(userId);
		List<TransactionDto> dtos = tx.stream().map(transactionMapper::toDto).collect(Collectors.toList());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}
	//END GET TRANSACTIONS


	//Get vendors by searching vendor nam
	/**
	 * @return ResponseEntity<List<Vendor>>
	 */
	@RequestMapping(value="/query", params = {"name"}, method=RequestMethod.GET)
	public ResponseEntity<List<TransactionDto>> searchTransactionsByVendorName(HttpServletRequest request, @PathVariable("userId") final String userId,
			@RequestParam final String name) {
		List<Transaction> tx = ts.searchVendors(userId, name);
		if(tx.isEmpty())
			throw new ResourceNotFoundException("No transactions found for vendor name: " + name);

		List<TransactionDto> dtos = tx.stream().map(transactionMapper::toDto).collect(Collectors.toList());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}
	//END GET VENDOR SEARCH

	@RequestMapping(value="/updateKeys", method=RequestMethod.POST)
	public ResponseEntity postTransKeys(HttpServletRequest request, @PathVariable("userId") final String userId) {
		// **TURNED OFF**
		//ts.postTransKeys(userId);
		//ts.postPmKeys(userId.toUpperCase());
		return new ResponseEntity<String>("NOT IMPLEMENTED - ENABLE IN SOURCE", HttpStatus.BAD_REQUEST);
	}

	//Get Transactions pageanated from params [start-end)
	/**
	 * @return ResponseEntity<List<TransactionDto>>
	 */
	@GetMapping(params = {"start", "to"} )
	@RequestMapping("/dates")
	public ResponseEntity<List<TransactionDto>> getTransactionsPageanatedByDate(
			@PathVariable("userId") final String userId,
			@RequestParam final String start,
			@RequestParam final String to) {
		List<Transaction> tx = ts.getAllTransactionsBetweenPurchaseDate(userId,
				LocalDate.parse(start), LocalDate.parse(to));
		List<TransactionDto> dtos = tx.stream().map(transactionMapper::toDto).collect(Collectors.toList());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}


	//Get Transactions pageanated from params [offset-limit)
	/**
	 * @return ResponseEntity<List<TransactionDto>>
	 */
	@GetMapping(params = {"limit", "offset"} )
	@RequestMapping("/recent")
	public ResponseEntity<List<TransactionDto>> getTransactionsPageanatedByRecent(HttpServletRequest request, @PathVariable("userId") final String userId,
			@RequestParam final Integer limit, @RequestParam(required=false) final Integer offset) {
		List<Transaction> tx = ts.getAllTransactionsPageableID(userId, limit, (offset != null) ? offset : 0);
		List<TransactionDto> dtos = tx.stream().map(transactionMapper::toDto).collect(Collectors.toList());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}



	@GetMapping(params = {"start", "to"} )
	@RequestMapping("/income")
	public ResponseEntity<List<SummaryTuple>> getIncomeSummary(HttpServletRequest request, @PathVariable("userId") final String userId,
										   @RequestParam final String start, @RequestParam final String to) {
		List<SummaryTuple> tx = ts.getIncomeAggregatedInDateRange(userId,
				LocalDate.parse(start), LocalDate.parse(to)) ;
		return new ResponseEntity<List<SummaryTuple>>(tx, HttpStatus.OK);
	}
	//END GET INCOME SUMMARY


	@GetMapping(params = {"start", "to"} )
	@RequestMapping("/expenses")
	public ResponseEntity<List<SummaryTuple>> getExpenseSummary(HttpServletRequest request, @PathVariable("userId") final String userId,
											@RequestParam final String start, @RequestParam final String to) {
		List<SummaryTuple> tx = ts.getExpensesAggregatedInDateRange(userId,
				LocalDate.parse(start), LocalDate.parse(to)) ;
		return new ResponseEntity<List<SummaryTuple>>(tx, HttpStatus.OK);
	}
	//END GET INCOME SUMMARY

	@GetMapping
	@RequestMapping("/categories")
	public ResponseEntity<List<String>> getCategories(HttpServletRequest request, @PathVariable("userId") final String userId) {
		List<String> cats = ts.getAllCategories(userId);
		return new ResponseEntity<List<String>>(cats, HttpStatus.OK);
	}
	//END GET Categories

	@GetMapping
	@RequestMapping("/payMethods")
	public ResponseEntity<List<PayMethod>> getPayMethods(HttpServletRequest request, @PathVariable("userId") final String userId) {
		List<PayMethod> vals = ts.getPayMethods(userId);
		return new ResponseEntity<>(vals, HttpStatus.OK);
	}
	//END GET Categories


	@GetMapping
	@RequestMapping("/boughtFor")
	public ResponseEntity<List<String>> getBoughtFor(HttpServletRequest request, @PathVariable("userId") final String userId) {
		List<String> vals = ts.getBoughtFor(userId);
		return new ResponseEntity<List<String>>(vals, HttpStatus.OK);
	}
	//END GET Categories


	@GetMapping
	@RequestMapping("/payStatus")
	public ResponseEntity<List<String>> getPayStatus(HttpServletRequest request, @PathVariable("userId") final String userId) {
		List<String> vals = ts.getPayStatus(userId);
		return new ResponseEntity<List<String>>(vals, HttpStatus.OK);
	}
	//END GET Categories


	//END GET METHODS

		/* POST METHODS */

	//Post new transactions to the database - data send in will be transaction objects
	@PostMapping
	public ResponseEntity<HttpMultiStatusResponse> postTransactions(HttpServletRequest request, @PathVariable("userId") final String userId,
																	@RequestBody final List<TransactionDto> tx)
	{
		try {
			HttpMultiStatusResponse response = ts.saveOrUpdateTransactions(userId, tx);
			return new ResponseEntity<>(response, HttpStatus.MULTI_STATUS);

		} catch (Exception e) {
			throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}



	/* PUT METHODS */

	/* PATCH METHODS */

	//Patch a list of transactions entry
	@PatchMapping
	public ResponseEntity<HttpMultiStatusResponse> patchTransactions(HttpServletRequest request, @PathVariable("userId") final String userId,
			@RequestBody final List<TransactionDto> tx)
	{
		try {
			HttpMultiStatusResponse response = ts.saveOrUpdateTransactions(userId, tx);
			return new ResponseEntity<>(response, HttpStatus.MULTI_STATUS);

		} catch (Exception e) {
			throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}




	/* DELETE METHODS */

	/*	Delete transaction with existing ID from the database
	 *
	 * 	EXCEPTIONS:
	 * 		- NoSuchIDException
	 */
	@DeleteMapping(value="/{tid}")
	@ResponseBody
	public ResponseEntity<HttpUnitResponse> deleteTransaction(HttpServletRequest request, @PathVariable("userId") final String userId,
													@PathVariable("tid") final Long tid) {
		HttpUnitResponse rsp = ts.deleteTransactionById(userId, tid);
		return new ResponseEntity<>(rsp, rsp.getStatus());
	}


	/*
	 * Non-restful, need a method for mass deletion in case of impropper additions
	 *
	 */
	@PatchMapping(value="/deleteAll")
	public ResponseEntity<String> deleteTransactions(HttpServletRequest request, @PathVariable("userId") final String userId,
													 @RequestBody final List<Long> tids)
	{
		//TODO: Implement
		ResponseEntity<String> rsp = new ResponseEntity<>("NOT IMPLEMENTED", HttpStatus.NOT_IMPLEMENTED);
		return rsp;
	}
}
//END TRANSACTION CONTROLLER