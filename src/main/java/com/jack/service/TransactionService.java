package com.jack.service;

import java.math.BigDecimal;
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
import com.jack.repository.subrepo.*;

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

	@Autowired
	PayMethodRepo pmRepo;
	@Autowired
	PayMethodKeyRepo pmkRepo;
	
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
	public List<Transaction> getAllTransactionsPageableID(final String userId,
												  Long limit, Long offset) {
		return repo.findAllByOrderByTidDescPageable(userId, limit, offset);
	}
	
	public List<Transaction> getAllTransactionsBetweenPurchaseDate(final String userId,
																   LocalDate from, LocalDate to) {
		return repo.findAllBetweenPurchaseDatesOrderByPurchaseDateDesc(userId, from, to);
	}
	
	public List<Transaction> getAllTransactionsByPurchaseDate(final String userId,
															  LocalDate purchaseDate) {
		return repo.findAllByPurchaseDate(userId, purchaseDate);
	}
	
	public Transaction getTransactionByID(final String userId, final Long tId) {
		Optional<TransactionKey> tk = keyRepo.findByUserIdAndTid(userId, tId);
		if(tk.isPresent())
			return tk.get().getTransaction();
		else
			return null; //error stuff
	}
	
	public List<Transaction> searchVendors(final String userId, String name) {
		return repo.findAllLikeVendorName(userId, "%" + name + "%");
	}
	
	public long countByPurchaseDate(final String userId, LocalDate purchaseDate) {
		return repo.countByPurchaseDate(userId, purchaseDate);
	}
	
	/* SIMPLE GETS FOR COL VALUES*/
	public List<String> getAllCategories(final String userId) {
		return repo.findCategoryGroupByCategory(userId);
	}
	
	public List<String> getPayMethods(final String userId) {
		return repo.findPayMethodsGroupByPayMethod(userId);
	}
	
	public List<String> getBoughtFor(final String userId) {
		return repo.findBoughtForGroupByBoughtFor(userId);
	}
	
	public List<String> getPayStatus(final String userId) {
		return repo.findPayStatusGroupByPayStatus(userId);
	}

	
	final String col1 = "aggregateCol"; final String col2 = "value"; final String col3 = "categories";  
	//Get income summary aggregated by vendor (source) and categories
	public List<SummaryTuple> getIncomeAggregatedInDateRange(final String userId,
													 LocalDate from, LocalDate to) {
		List<Map<String, Object>> data = repo.findIncomeAggregatedByVendorAndCategories(userId, from, to);
		List<SummaryTuple> summary = new ArrayList<>();
		data.forEach((o) -> summary.add(new SummaryTuple( (String) o.get(col1), 
				(BigDecimal) o.get(col2), (String) o.get(col3))));
		return summary;
	}
	
	public List<SummaryTuple> getExpensesAggregatedInDateRange(final String userId,
													   LocalDate from, LocalDate to) {
		List<Map<String, Object>> data = repo.findExpensesAggregatedByCategoryAndBoughtFor(userId, from, to);
		List<SummaryTuple> summary = new ArrayList<>();
		data.forEach((o) -> summary.add(new SummaryTuple( (String) o.get(col1), 
				(BigDecimal) o.get(col2), (String) o.get(col3))));
		return summary;
	}
	
	//########### END GET METHODS ############
	
	//Save Data
	public Transaction saveTransaction(Transaction tx, UserAccount u) {
		keyRepo.save(new TransactionKey(tx, u));
		return repo.save(tx);
	}

	
	//Delete Transaction by ID
	public void deleteTransactionById(final String userId, final long tid) {
		Optional<TransactionKey> tk = keyRepo.findByUserIdAndTid(userId, tid);
		if(!tk.isPresent())
			throw new ResourceNotFoundException("ERROR: No Transaction found with True ID: " +
					tk.get().getTransaction().getTrueId());

		keyRepo.deleteByTrueId(tk.get().getTransaction().getTrueId());
		repo.deleteByTrueId(tk.get().getTransaction().getTrueId());
	}
	
	public Transaction updateTransaction(Transaction tx) throws ResourceNotFoundException {
		System.out.println("Attempting to save: " + tx);
		if(!repo.existsById(tx.getTrueId()))
			throw new ResourceNotFoundException("ERROR: No Transaction found with True ID: " + tx.getTrueId());

		Transaction oldTrans = repo.findByTrueId(tx.getTrueId());
		if(!Transaction.compareIds(tx, oldTrans))
			throw new IllegalArgumentException(String.format("ERROR: You may not change the tid or the trueId in a PATCH call" +
					" for transaction in database as tid: %s, trueId: %s", oldTrans.getTId(), oldTrans.getTrueId()));

		return repo.save(tx);
	}

	public long getDefaultReimburses(final String userId) {
		return this.getTransactionByID(userId, 0L).getTrueId();		//Each user has default transaction at tid==0
	}

	/*
		SPECIAL TRANS KEY METHOD FOR ONE TIME KEY GENERATION
    	WHEN SWITCHING TO USER ACCOUNT INTEGRATION
 	*/
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

	public void postPmKeys(String userId) {
		List<Transaction> allTrans = repo.findAllByUserId(userId);
		Optional<UserAccount> u = userRepo.findByUserId(userId);

		if (!u.isPresent())
			return;

		for (Transaction t : allTrans) {
			PayMethod pm = new PayMethod(userId, t.getPayMethodString(), "SIMPLE");
			PayMethodKey pmk = new PayMethodKey(pm, u.get());

			if(!pmkRepo.existsByPayMethod(pm)) {
				pmRepo.save(pm);
				pmkRepo.save(pmk);
			}

			t.setPayMethod(pm);
			repo.save(t);
		} //END FOR

	}


}
//END CLASS TRANSACTIONSERVICE