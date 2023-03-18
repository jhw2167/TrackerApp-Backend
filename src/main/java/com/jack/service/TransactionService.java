package com.jack.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

//Java Imports
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

//Spring Imports


//Project imports
import com.jack.repository.*;
import com.jack.model.*;
import com.jack.model.submodel.*;

/* Service class for transactions handels all BUSINESS logic and is called from the 
 * controller class
 * 
 * 
 */

@Service
public class TransactionService 
{

	/* State Variables */

	@Autowired
	TransactionRepo repo;

	@Autowired
	TransactionKeyRepo keyRepo;

	@Autowired
	UserAccountRepo userRepo;
	
	//END STATE VARS

	/* UTILITY METHODS */
	
	//Return all transactions, unsorted
	public List<Transaction> getAllUserTransactions(final String userId) {
		return repo.findAllByUserId(userId);
	}
	
	
	//Return all Transactions sorted
	public List<Transaction> getAllTransactonsSorted(final String userId) {
		return repo.findAllByOrderByPurchaseDateDesc(userId);
	}
	
	
	//Return Transactions pageable by start, end
	public List<Transaction> getAllTransactionsPageableID(Long limit, Long offset) {
		return repo.findAllByOrderByTidDescPageable(limit, offset);
	}
	
	public List<Transaction> getAllTransactionsBetweenPurchaseDate(final String userId,
																   LocalDate from, LocalDate to) {
		return repo.findAllBetweenPurchaseDatesOrderByPurchaseDateDesc(userId, from, to);
	}
	
	public List<Transaction> getAllTransactionsByPurchaseDate(LocalDate purchaseDate) {
		return repo.findAllByPurchaseDate(purchaseDate);
	}
	
	public Transaction getTransactionByID(final String userId, final Long tId) {
		Optional<TransactionKey> tk = keyRepo.findByUserIdAndTid(userId, tId);
		if(tk.isPresent())
			return tk.get().getTransaction();
		else
			return null; //error stuff
	}

	public void postTransKeys(String userId) {
		List<Transaction> allTrans = repo.findAll();
		Optional<UserAccount> u = userRepo.findByUserId(userId);

		if (!u.isPresent())
			return;

		for (Transaction t : allTrans) {
				t.setTrueId(u.get().getUserId(), t.getTId());
				repo.save(t);
				TransactionKey tk = new TransactionKey(t, u.get());
				keyRepo.save(tk);
		} //END FOR
	}
	
	public List<Transaction> searchVendors(String name) {
		return repo.findAllLikeVendorName("%" + name + "%");
	}
	
	public long countByPurchaseDate(LocalDate purchaseDate) {		
		return repo.countByPurchaseDate(purchaseDate);
	}
	
	/* SIMPLE GETS FOR COL VALUES*/
	public List<String> getAllCategories() {
		return repo.findCategoryGroupByCategory();
	}
	
	public List<String> getPayMethods() {
		return repo.findPayMethodsGroupByPayMethod();
	}
	
	public List<String> getBoughtFor() {
		return repo.findBoughtForGroupByBoughtFor();
	}
	
	public List<String> getPayStatus() {
		return repo.findPayStatusGroupByPayStatus();
	}

	
	final String col1 = "aggregateCol"; final String col2 = "value"; final String col3 = "categories";  
	//Get income summary aggregated by vendor (source) and categories
	public List<SummaryTuple> getIncomeAggregatedInDateRange(LocalDate from, LocalDate to) {
		List<Map<String, Object>> data = repo.findIncomeAggregatedByVendorAndCategories(from, to);
		List<SummaryTuple> summary = new ArrayList<>();
		data.forEach((o) -> summary.add(new SummaryTuple( (String) o.get(col1), 
				(BigDecimal) o.get(col2), (String) o.get(col3))));
		return summary;
	}
	
	public List<SummaryTuple> getExpensesAggregatedInDateRange(LocalDate from, LocalDate to) {
		List<Map<String, Object>> data = repo.findExpensesAggregatedByCategoryAndBoughtFor(from, to);
		List<SummaryTuple> summary = new ArrayList<>();
		data.forEach((o) -> summary.add(new SummaryTuple( (String) o.get(col1), 
				(BigDecimal) o.get(col2), (String) o.get(col3))));
		return summary;
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
	
	public Transaction updateTransaction(Transaction tx) throws ResourceNotFoundException {
		System.out.println("Attempting to save: " + tx);
		if(!repo.existsById(tx.getTId()))
			throw new ResourceNotFoundException("No Transaction found with ID: " + tx.getTId());
		
		//old.get().updateData(tx);
		//return old.get();
		return repo.save(tx);
	}
}
//END CLASS TRANSACTIONSERVICE