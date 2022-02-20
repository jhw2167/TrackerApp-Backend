package com.jack.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
//Java Imports
import java.util.List;
import java.util.Map;

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
	public List<Transaction> getAllTransactionsPageableID(Long limit, Long offset) {
		return repo.findAllByOrderByTidDescPageable(limit, offset);
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

	
	final String col1 = "vendor"; final String col2 = "netIncome"; final String col3 = "categories";  
	//Get income summary aggregated by vendor (source) and categories
	public List<IncomeTuple> getIncomeAggregatedInDateRange(LocalDate from, LocalDate to) {
		List<Map<String, Object>> data = repo.findIncomeAggregatedByVendorAndCategories(from, to);
		List<IncomeTuple> incs = new ArrayList<>();
		data.forEach((o) -> incs.add(new IncomeTuple( (String) o.get(col1), 
				(BigDecimal) o.get(col2), (String) o.get(col3))));
		return incs;
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