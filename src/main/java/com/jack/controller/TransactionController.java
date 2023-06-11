package com.jack.controller;


//Spring Imports
import com.jack.model.dto.TransactionDto;
import com.jack.utility.DataError;
import com.jack.utility.SuccessErrorMessage;
import org.apache.catalina.mapper.Mapper;
import org.hibernate.HibernateException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

//Java Imports
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.sql.SQLException;
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
import com.jack.model.*;
import com.jack.service.*;
import com.jack.model.dto.mapper.TransactionMapper;



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
	 *	Get all transaction by userId and tid
	 * @return ResponseEntity<TransactionDto>
	 */
	@RequestMapping(value="/{tid}", method=RequestMethod.GET)
	public ResponseEntity<TransactionDto> getTransactionByID(@PathVariable("userId") final String userId, @PathVariable("tid") final long tId) {
		return new ResponseEntity<>(
				transactionMapper.toDto( ts.getTransactionByID(userId, tId) ) ,
				HttpStatus.OK);
	}
	//END GET TRANSACTION BY ID


	/**
	 *	Get all transactions, default sorted by date, descending (most recent to oldest) 
	 * 
	 * @return ResponseEntity<List<TransactionDto>>
	 */
	@GetMapping
	//basic /transactions
	public ResponseEntity<List<TransactionDto>> getUserTransactions(@PathVariable("userId") final String userId) {
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
	public ResponseEntity<List<TransactionDto>> searchTransactionsByVendorName(@PathVariable("userId") final String userId,
			@RequestParam final String name) {
		List<Transaction> tx = ts.searchVendors(userId, name);
		List<TransactionDto> dtos = tx.stream().map(transactionMapper::toDto).collect(Collectors.toList());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
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
	public ResponseEntity<List<TransactionDto>> getTransactionsPageanatedByDate(@PathVariable("userId") final String userId,
			@RequestParam final Integer limit, @RequestParam(required=false) final Integer offset) {
		List<Transaction> tx = ts.getAllTransactionsPageableID(userId, limit, (offset != null) ? offset : 0);
		List<TransactionDto> dtos = tx.stream().map(transactionMapper::toDto).collect(Collectors.toList());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
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
	public ResponseEntity<SuccessErrorMessage> postTransactions(@PathVariable("userId") final String userId,
												   @RequestBody final List<Transaction> tx)
	{
		//Make sure user exists else exit
		UserAccount user = us.getUserAccountById(userId);
		HttpStatus status = HttpStatus.OK;
		List<Transaction> savedTransactions = new ArrayList<>();
		final List<DataError> errors = new ArrayList<>();

		try {

			for (Transaction t : tx ) {
				vs.saveVendor(new Vendor(t));
				pms.savePayMethod(t, user);

				Transaction savableTransaction = new Transaction(user, t,
						ts.countByPurchaseDate(userId, t.getPurchaseDate())  );
				try {
					savedTransactions.add(ts.saveTransaction( savableTransaction ) );

				} catch (HibernateException e) {
					DataError d = new DataError(savableTransaction, e.getLocalizedMessage());
					errors.add(d);
				}
				catch (Exception e) {
					DataError d = new DataError(savableTransaction, e.getLocalizedMessage());
					errors.add(d);
				}
			} //END FOR

			String savedMesssage = "Successfully saved transactions with the folowing tids: ";
			List<Long> savedData = savedTransactions.stream().map(Transaction::getTid).collect(Collectors.toList());
			SuccessErrorMessage response = new SuccessErrorMessage(savedMesssage, savedData);
			/* Check for Errors */
			if(!errors.isEmpty()) {
				response.setErrorMessage("Error saving the following transactions: ");
				response.setErrorData(errors);
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}

			return new ResponseEntity<>(response, status);

		} catch (Exception e) {
			throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

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