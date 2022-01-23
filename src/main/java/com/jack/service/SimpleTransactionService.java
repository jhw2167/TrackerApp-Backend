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
public class SimpleTransactionService 
{

	/* State Variables */
	
	SimpleTransactionRepo repo;
	
	//END STATE VARS
	
	@Autowired
	SimpleTransactionService(SimpleTransactionRepo repo) {
		this.repo = repo;
	}
	
	/* UTILITY METHODS */
	
	//Return all transactions, unsorted
	public List<SimpleTransaction> getAllTransactions() {
		return repo.findAll();
	}
	
	
	//Return all Transactions sorted
	public List<SimpleTransaction> getAllTransactonsSorted() {
		return repo.findAllByOrderByPurchaseDateDesc();
	}
	
	
	//Return Transactions pageable by start, end
	public List<SimpleTransaction> getAllTransactionsPageable(Integer start, Integer end) {
		
		//throw an error if end < start
		return null;
	}
	
	
	//Save Data
	public void saveTransactions(final List<SimpleTransaction> tx) {
		repo.saveAll(tx);
	}
	
	//Delete Transaction by ID
	public void deleteTransactionById(final long id) {
		repo.deleteById(id);		
	}
	
}
//END CLASS TRANSACTIONSERVICE