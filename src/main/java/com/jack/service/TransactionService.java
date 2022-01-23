package com.jack.service;

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
	
	public List<Transaction> getAllTransactionsByPurchaseDate(String purchaseDate) {
		return repo.findAllByPurchaseDate(purchaseDate);
	}
	
	public long countByPurchaseDate(String purchaseDate) {
		return repo.countByPurchaseDate(purchaseDate);
	}

	
	//########### END GET METHODS ############
	
	//Save Data
	public void saveTransactions(List<Transaction> tx) {
		repo.saveAll(tx);
	}
	
	//Delete Transaction by ID
	public void deleteTransactionById(final long id) {
		repo.deleteById(id);		
	}
	
}
//END CLASS TRANSACTIONSERVICE