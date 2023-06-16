package com.jack.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

//Java Imports
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Spring Imports
import com.jack.model.dto.TransactionDto;
import com.jack.model.dto.mapper.TransactionMapper;
import com.jack.utility.HttpUnitResponse;
import com.jack.utility.HttpMultiStatusResponse;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


//Project imports
import com.jack.repository.*;
import com.jack.model.*;
import org.springframework.web.client.HttpClientErrorException;

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
	UserAccountService userService;

	@Autowired
	PayMethodService payMethodService;

	@Autowired
	VendorService vendorService;

	@Autowired
	TransactionMapper dtoMapper;
	
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
		return repo.findAllByUserUserIdAndVendorVendorNameLike(userId, "%" + name.toUpperCase() + "%");
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
	
	public Transaction updateTransaction(Transaction tx, UserAccount u) throws ResourceNotFoundException, IllegalArgumentException
	{
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
	public HttpUnitResponse deleteTransactionById(final String userId, final long tid) {
		Optional<Transaction> t = repo.findByUserUserIdAndTid(userId, tid);
		if(!t.isPresent())
			throw new ResourceNotFoundException("ERROR: No Transaction found with tid: " + tid);

		TransactionDto dto = dtoMapper.toDto(t.get());
		repo.deleteById(t.get().getTrueId());
		return new HttpUnitResponse(dto, dto.getTid(),
				"Transaction deleted successfully", HttpStatus.OK);
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

    public HttpMultiStatusResponse saveOrUpdateTransactions(String userId, List<TransactionDto> tx) {

		//Make sure user exists else exit
		UserAccount user = userService.getUserAccountById(userId);
		List<HttpUnitResponse> processedTransactions = new ArrayList<>();

		try {

			for (TransactionDto t : tx ) {
				Vendor v = vendorService.saveVendor(new Vendor(user, t));
				PayMethod pm = payMethodService.savePayMethod(new PayMethod(user, t.getPayMethod()));

				Transaction savableTransaction = new Transaction(t, user, pm, v,
						this.countByPurchaseDate(userId, t.getPurchaseDate())  );
				HttpUnitResponse response = new HttpUnitResponse(savableTransaction, null,
						"Transaction Posted Successfully", HttpStatus.OK);
				try {
					Transaction saved = null;

					//if transaction is not new, update it
					if(repo.existsById(savableTransaction.getTrueId()) ||
							repo.findByUserUserIdAndTid(userId, savableTransaction.getTid()).isPresent())
						 saved = this.updateTransaction(savableTransaction, user);
					 else
						 saved = this.saveTransaction( savableTransaction );

					response.setData(null);
					response.setId( saved.getTid() );

				} catch (HibernateException e) {
					response.setMessage(e.getMessage());
					response.setStatus(HttpStatus.BAD_REQUEST);
				} catch (ResourceNotFoundException e) {
					response.setMessage(e.getMessage());
					response.setStatus(HttpStatus.NOT_FOUND);
				} catch (IllegalArgumentException e) {
					response.setMessage(e.getMessage());
					response.setStatus(HttpStatus.BAD_REQUEST);
				} catch (DataIntegrityViolationException e) {
					response.setMessage(e.getMessage());
					response.setStatus(HttpStatus.CONFLICT);
				} catch (Exception e) {
					response.setMessage(e.getMessage());
					response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
				}

				processedTransactions.add(response);
			} //END FOR

			return new HttpMultiStatusResponse(processedTransactions,
					"Transactions processed with multiple statuses");

		} catch (Exception e) {
			throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
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