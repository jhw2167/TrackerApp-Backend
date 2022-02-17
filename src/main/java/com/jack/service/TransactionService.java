package com.jack.service;

import java.sql.Date;
import java.time.LocalDate;
//Java Imports
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Spring Imports


//Project imports
import com.jack.repository.*;
import com.jack.model.*;

/* Service class for transactions handels all BUSINESS logic and is called from the 
 * controller class
 * 
 * 
 */

@Service
public class TransactionService 
{

	/* State Variables */
	
	TransactionRepo repo;
	
	//END STATE VARS
	
	@Autowired
	TransactionService(TransactionRepo repo) {
		this.repo = repo;
	}
	
	/* UTILITY METHODS */
	
	//Return all transactions, unsorted
	public List<Transaction> getAllTransactions() {
		return repo.findAll();
	}
	
	
	//Return all Transactions sorted
	public List<Transaction> getAllTransactonsSorted() {
		return repo.findAllByOrderByPurchaseDateDesc();
	}
	
	
	//Return Transactions pageable by start, end
	public List<Transaction> getAllTransactionsPageable(Integer start, Integer end) {
		
		//throw an error if end < start
		return null;
	}
	
	public List<Transaction> getAllTransactionsBetweenPurchaseDate(LocalDate from, LocalDate to) {
		return repo.findAllBetweenPurchaseDatesOrderByPurchaseDateDesc(from, to);
	}
	
	public List<Transaction> getAllTransactionsByPurchaseDate(LocalDate purchaseDate) {
		return repo.findAllByPurchaseDate(purchaseDate);
	}
	
	public long countByPurchaseDate(LocalDate purchaseDate) {		
		return repo.countByPurchaseDate(purchaseDate);
	}
	
	public List<String> getAllCategories() {
		return repo.findCategoryGroupByCategory();
	}

	
	//########### END GET METHODS ############
	
	//Save Data
	public Transaction saveTransaction(Transaction tx) {
		return repo.save(tx);
	}
	
	public void saveTransactions(List<Transaction> tx) {
		repo.saveAll(tx);
	}
	
	//Delete Transaction by ID
	public void deleteTransactionById(final long id) {
		repo.deleteById(id);		
	}
	
}
//END CLASS TRANSACTIONSERVICE