package com.jack.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

//Java Imports
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Spring Imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;


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

	@Autowired
	TransactionRepo repo;

/*	@Autowired
	TransactionKeyRepo keyRepo;*/

	@Autowired
	UserAccountRepo userRepo;

	@Autowired
	PayMethodRepo pmRepo;

	/*@Autowired
	PayMethodKeyRepo pmkRepo;
	*/

	@Autowired
	PayMethodService pmService;
	
	//END STATE VARS

	/* UTILITY METHODS */
	
	//Return all transactions, unsorted
	public List<Transaction> getAllUserTransactions(final String userId) {
		return repo.findAllByUserUserId(userId);
	}
	
	
	//Return all Transactions sorted
	public List<Transaction> getAllTransactonsSorted(final String userId) {
		return repo.findByUserUserIdOrderByPurchaseDateDesc(userId);
	}
	
	
	//Return Transactions pageable by start, end
	public List<Transaction> getAllTransactionsPageableID(final String userId,
												  int limit, int offset) {
		Pageable pageable = PageRequest.of(offset, limit, Sort.by("tid").descending());
		return repo.findByUserUserIdOrderByTidDesc(userId, pageable);
	}
	
	public List<Transaction> getAllTransactionsBetweenPurchaseDate(final String userId,
																   LocalDate from, LocalDate to) {
		return repo.findByUserUserIdAndPurchaseDateBetweenOrderByPurchaseDateDesc(userId, from, to);
	}
	
	public List<Transaction> getAllTransactionsByPurchaseDate(final String userId,
															  LocalDate purchaseDate) {
		return repo.findByUserUserIdAndPurchaseDate(userId, purchaseDate);
	}
	
	public Transaction getTransactionByID(final String userId, final Long tId) throws ResourceNotFoundException {
		Optional<Transaction> t = repo.findByUserUserIdAndTid(userId, tId);

		if(t.isPresent())
			return t.get();
		else
			throw new ResourceNotFoundException(String.format("Could not find transaction: %s under user: %s", tId, userId));
	}

	public Transaction getTransactionByTrueId(final Long trueId) throws ResourceNotFoundException {
		Optional<Transaction> t = repo.findById(trueId);
		if(t.isPresent())
			return t.get();
		else
			throw new ResourceNotFoundException(String.format("Could not find transaction with id: %s", trueId));
	}
	
	public List<Transaction> searchVendors(final String userId, String name) {
		return repo.findAllByUserUserIdAndVendorLike(userId, "%" + name + "%");
	}
	
	public long countByPurchaseDate(final String userId, LocalDate purchaseDate) {
		/*return repo.countByPurchaseDate(userId, purchaseDate);*/
		return repo.countByUserUserIdAndPurchaseDate(userId, purchaseDate);
	}
	
	/* SIMPLE GETS FOR COL VALUES*/
	public List<String> getAllCategories(final String userId) {
		return repo.findDistinctCategoryByUserUserId(userId);
	}
	
	public List<PayMethod> getPayMethods(final String userId) {
		return repo.findDistinctPayMethodByUserUserId(userId);
	}
	
	public List<String> getBoughtFor(final String userId) {
		return repo.findDistinctBoughtForByUserUserId(userId);
	}
	
	public List<String> getPayStatus(final String userId) {
		return repo.findDistinctPayStatusByUserUserId(userId);
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
	public Transaction saveTransaction(Transaction tx) {
		setDefaultReimburses(tx);
		return repo.save(tx);
	}
	
	public Transaction updateTransaction(Transaction tx, UserAccount u) throws ResourceNotFoundException {
		//System.out.println("Attempting to save: " + tx);
		setDefaultReimburses(tx);
		if(!repo.existsById(tx.getTrueId()))
			throw new ResourceNotFoundException("ERROR: No Transaction found with True ID: " + tx.getTrueId());

		Transaction oldTrans = repo.findByTrueId(tx.getTrueId());
		if(!Transaction.compareIds(tx, oldTrans))
			throw new IllegalArgumentException(String.format("ERROR: You may not change the tid or the trueId in a PATCH call" +
					" for transaction in database as tid: %s, trueId: %s", oldTrans.getTid(), oldTrans.getTrueId()));

		return repo.save(tx);
	}

	//Delete Transaction by ID
	public void deleteTransactionById(final String userId, final long tid) {
		Optional<Transaction> t = repo.findByUserUserIdAndTid(userId, tid);
		if(!t.isPresent())
			throw new ResourceNotFoundException("ERROR: No Transaction found with tid: " + tid);

		repo.deleteById(t.get().getTrueId());
	}

	/* We make sure that the reimburses id is valid for this user, if not,
	it defaults to (userId, tid=0), the default base transaction in each user's account
	 */
	public void setDefaultReimburses(Transaction tx) {
		UserAccount u = tx.getUser();
		Optional<Transaction> reimbTrans = repo.findByUserUserIdAndTrueId(u.getUserId(), tx.getReimburses());
		if (!reimbTrans.isPresent()) {
			tx.setReimburses( 	this.getTransactionByID(u.getUserId(), 0L).getTrueId()	);//Each user has default transaction at tid==0
		}
	}

	/*
		SPECIAL TRANS KEY METHOD FOR ONE TIME KEY GENERATION
    	WHEN SWITCHING TO USER ACCOUNT INTEGRATION
 	//* /
	public void postTransKeys(String userId) {
		List<Transaction> allTrans = repo.findAll();
		Optional<UserAccount> u = userRepo.findByUserUserId(userId);

		if (!u.isPresent())
			return;

		for (Transaction t : allTrans) {
			t.setTrueId(u.get().getUserId(), t.getTId());
			repo.save(t);
		} //END FOR
	}
*/
	/*
		@deprecated Old method for refactoring tables, this one for pay method tables redo

	public void postPmKeys(String userId) {
		List<Transaction> allTrans = repo.findAllByUserUserId(userId);
		Optional<UserAccount> u = userRepo.findByUserUserId(userId);

		if (!u.isPresent())
			return;

		for (Transaction t : allTrans) {
			long pmId = 0;
			if(t.getPayMethod() != 0) {
				pmId = t.getPayMethod();
			} //else if (pmRepo.findByMethodName(userId, t.getPayMethodString()).isPresent()) {
				//pmId = pmRepo.findByMethodName(userId, t.getPayMethodString()).get().getPmId();
				//return; }
			else {
				pmId = pmService.savePayMethod(t, u.get()).getPmId();
			}
			t.setPayMethod(pmId);
			repo.save(t);
		} //END FOR

	}
	*/

}
//END CLASS TRANSACTIONSERVICE