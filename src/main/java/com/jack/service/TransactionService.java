package com.jack.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

//Java Imports
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Spring Imports
import com.jack.TrackerSpringProperties;
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
public class TransactionService {

	/* State Variables */
	@Autowired
	TrackerSpringProperties properties;

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
	public List<Transaction> findAllTransactionsPageableID(final String userId,
														   int size, int page) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("purchaseDate").descending());
		return repo.findAllByUserUserIdOrderByTidDesc(userId, pageable);
	}

	public List<Transaction> getAllTransactionsQueryDates(final String userId, String start, String to, String on) {

		LocalDate startD = null;
		LocalDate toD = null;
		LocalDate onD = null;
		String errorDate = "start";
		try {
			if (start == null && to == null && on == null)
				throw new Exception();
			startD = (start != null) ? LocalDate.parse(start) : null;

			errorDate = "to";
			toD = (to != null) ? LocalDate.parse(to) : LocalDate.now();

			errorDate = "on";
			onD = (on != null) ? LocalDate.parse(on) : null;
		} catch (DateTimeParseException dtpe) {
			String error = "Error parsing date format for query parameter: "
					+ errorDate + " format must be YYYY-MM-DD";
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, error);
		} catch (Exception e) {
			String error = "Error parsing dates for query: use /dates?(start, to, on)=YYYY-MM-DD";
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, error);
		}

		//Now get the transactions
		List<Transaction> tx = new ArrayList<>();
		if (onD != null)
			tx = getAllTransactionsOnPurchaseDate(userId, onD);
		else
			tx = getAllTransactionsBetweenPurchaseDate(userId, startD, toD);

		return tx;
	}

	/* Minus 1 day from end so it is not inclusive of the end date */
	public List<Transaction> getAllTransactionsBetweenPurchaseDate(final String userId, LocalDate from, LocalDate to) {
		return repo.findByUserUserIdAndPurchaseDateBetweenOrderByPurchaseDateDesc(userId, from, to.minusDays(1L));
	}

	public List<Transaction> getAllTransactionsOnPurchaseDate(final String userId, LocalDate from) {
		return repo.findByUserUserIdAndPurchaseDateOrderByTidAsc(userId, from);
	}


	public Transaction getTransactionByID(final String userId, final Long tId) throws ResourceNotFoundException {

		//find the transaciton with userName = common
		if( tId == 0)
			return repo.findByUserUserIdAndTid( properties.get("database.constants.baseUserId"), 0).get();

		Optional<Transaction> t = repo.findByUserUserIdAndTid(userId, tId);

		if (t.isPresent())
			return t.get();
		else
			throw new ResourceNotFoundException(String.format("Could not find transaction: %s under user: %s", tId, userId));
	}

	public Transaction getTransactionByTrueId(final Long trueId) throws ResourceNotFoundException {
		Optional<Transaction> t = repo.findById(trueId);
		if (t.isPresent())
			return t.get();
		else
			throw new ResourceNotFoundException(String.format("Could not find transaction with id: %s", trueId));
	}

	public List<Transaction> searchVendors(final String userId, String name) {
		return repo.findAllByUserUserIdAndVendorVendorNameLike(userId, "%" + name.toUpperCase() + "%");
	}

	public long countByPurchaseDate(final String userId, LocalDate purchaseDate) {
		return repo.countByUserUserIdAndPurchaseDate(userId, purchaseDate);
	}

	public long findNextAvailibleTid(final String userId, LocalDate purchaseDate) {

		//Find greatest tid greater than min and less than max
		List<Transaction> t = repo.findByUserUserIdAndPurchaseDateOrderByTidAsc(userId, purchaseDate);

		if (t.isEmpty())
			return Transaction.generateTid(purchaseDate.toString(), 0);
		else
			return t.get(t.size()-1).getTid() + 1;
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


	final String col1 = "aggregateCol";
	final String col2 = "value";
	final String col3 = "categories";

	//Get income summary aggregated by vendor (source) and categories
	public List<SummaryTuple> getIncomeAggregatedInDateRange(final String userId,
															 LocalDate from, LocalDate to) {
		List<Map<String, Object>> data = repo.findIncomeAggregatedByVendorAndCategories(userId, from, to);
		List<SummaryTuple> summary = new ArrayList<>();
		data.forEach((o) -> summary.add(new SummaryTuple((String) o.get(col1),
				(BigDecimal) o.get(col2), (String) o.get(col3))));
		return summary;
	}

	public List<SummaryTuple> getExpensesAggregatedInDateRange(final String userId,
															   LocalDate from, LocalDate to) {
		List<Map<String, Object>> data = repo.findExpensesAggregatedByCategoryAndBoughtFor(userId, from, to);
		List<SummaryTuple> summary = new ArrayList<>();
		data.forEach((o) -> summary.add(new SummaryTuple((String) o.get(col1),
				(BigDecimal) o.get(col2), (String) o.get(col3))));
		return summary;
	}

	//########### END GET METHODS ############

	//Save Data
	public Transaction saveTransaction(Transaction tx) {
		setReimbursesFieldForRefundTransactions(tx);
		return repo.save(tx);
	}

	public Transaction updateTransaction(Transaction tx, UserAccount u) throws ResourceNotFoundException, IllegalArgumentException {
		if (tx.getTid() == 0)
			throw new IllegalArgumentException("ERROR: Attempt to update transaction with tid=0 rejected");

		setReimbursesFieldForRefundTransactions(tx);
		if (!repo.existsById(tx.getTrueId()))
			throw new ResourceNotFoundException("ERROR: No Transaction found with True ID: " + tx.getTrueId());

		Transaction oldTrans = repo.findByTrueId(tx.getTrueId());
		if (!Transaction.compareIds(tx, oldTrans))
			throw new IllegalArgumentException(String.format("ERROR: You may not change the tid or the trueId in a PATCH call" +
					" for transaction in database as tid: %s, trueId: %s", oldTrans.getTid(), oldTrans.getTrueId()));

		return repo.save(tx);
	}

	//Delete Transaction by ID
	public HttpUnitResponse deleteTransactionById(final String userId, final long tid) {
		Optional<Transaction> t = repo.findByUserUserIdAndTid(userId, tid);
		if (!t.isPresent())
			throw new ResourceNotFoundException("ERROR: No Transaction found with tid: " + tid);

		TransactionDto dto = dtoMapper.toDto(t.get());
		repo.deleteById(t.get().getTrueId());
		return new HttpUnitResponse(dto, dto.getTid(),
				"Transaction deleted successfully", HttpStatus.OK);
	}

	/* We make sure that the reimburses id is valid for this user, if not,
	it defaults to (userId, tid=0), the default base transaction in each user's account
	 */
	public void setReimbursesFieldForRefundTransactions(Transaction tx) {
		UserAccount u = tx.getUser();
		Optional<Transaction> searchByTrueId = repo.findByUserUserIdAndTrueId(u.getUserId(), tx.getReimburses());
		Optional<Transaction> searchByTid = repo.findByUserUserIdAndTrueId(u.getUserId(), tx.getReimburses());

		if (searchByTrueId.isPresent()) {
			tx.setReimburses(searchByTrueId.get().getTrueId());
		} else if (searchByTid.isPresent()) {
			tx.setReimburses(searchByTid.get().getTrueId());
		} else {
			tx.setReimburses(repo.findByUserUserIdAndTid( properties.get("database.constants.baseUserId"), 0L).get().getTrueId());
		}
	}

	/*
	   Description:
	   1. Define user account and list of completed processed transactions
	   2. For Each transaction
	   		3. Acquire or create new vendor and Pay Method as necessary
	   		4. Create a new HttpUnitResponse object to return data for failed or successful load
	   		5. Check if transaction exists by user and tid
	   		6. If transaction exists, update it, else save it
	   		7. Add the transaction id to the response object

	 */

	public HttpMultiStatusResponse saveOrUpdateTransactions(String userId, List<TransactionDto> tx, String httpverb) {

		//Make sure user exists else exit
		UserAccount user = userService.getUserAccountById(userId);
		List<HttpUnitResponse> processedTransactions = new ArrayList<>();

		try {

			for (TransactionDto t : tx) {
				Vendor v = vendorService.saveVendor(new Vendor(user, t));
				PayMethod pm = payMethodService.savePayMethod(new PayMethod(user, t.getPayMethod()));

				t.setUserId(userId);
				HttpUnitResponse response = new HttpUnitResponse(t, null,
						"Transaction Posted Successfully", HttpStatus.OK);
				try {

					Transaction saved = null;

					if( httpverb.equals("PATCH")  )
					{
						Optional<Transaction> savableTransaction = repo.findByUserUserIdAndTid(userId, t.getTid());
						if (savableTransaction.isPresent()) {
							saved = this.updateTransaction(new Transaction(t), user);
							response.setStatus(HttpStatus.OK);
						} else {
							String error = String.format("ERROR: Could not find transaction with tid: %s", t.getTid(), userId);
							throw new ResourceNotFoundException(error);
						}
					}
					else if( httpverb.equals("POST") )
					{
						long tid = findNextAvailibleTid(userId, t.getPurchaseDate());
						saved = this.saveTransaction(new Transaction(t, user, pm, v, tid));
						response.setStatus(HttpStatus.CREATED);
					}

					response.setId(saved.getTid());

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
}
///END CLASS TRANSACTIONSERVICE