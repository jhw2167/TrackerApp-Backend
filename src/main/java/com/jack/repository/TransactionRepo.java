package com.jack.repository;


import java.sql.Date;
import java.time.LocalDate;
//JAVA Imports
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Spring Imports
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

//Project Imports
import com.jack.model.*;


/* CRUD repository of spring provides basic "find by ___ criteria to search transactions in DB"
 * - we will also make use of sorted criteria to provide outgoing info in sorted format
 * - 
 */

public interface TransactionRepo extends JpaRepository<Transaction, Long>
{
	/*
		Base SP for creating view of user's transactions
	 */
	@Procedure(procedureName = "create_user_transactions_view")
	public void createUserTransactionsView(@Param("user_view_name") String userViewName,
										   @Param("user_view_name") String userId);

	@Procedure(procedureName = "drop_user_transactions_view")
	public void dropUserTransactionsView(@Param("user_view_name") String userViewName);


	//#0
	//simple find tranasction like id
	public Transaction findByTrueId(long trueId);

	//#0a Delete by trueId
	@Modifying
	@Query(value="DELETE FROM transactions WHERE true_id=:true_id", nativeQuery=true)
	public void deleteByTrueId(@Param("true_id") long trueId);

	/** @param userId - transformed into user_view_name via TransactionRepoAspect
	 *  @param trueId - true id of the transaction
	 */
	@Query(value = "SELECT * FROM :user_view_name T WHERE TRUE_ID = :true_id", nativeQuery = true)
	public Transaction findFullTransactionByTrueId(@Param("user_view_name") String userId,
												   @Param("true_id") long trueId);


	//#1
	//simple find all transactions by userId
	@Query(value = "SELECT * FROM :user_view_name t", nativeQuery = true)
	public List<Transaction> findAllByUserId(@Param("user_view_name") String userId);

	//#2
	//FindAll sorted by date YYYY-MM-DD format
	@Query(value = "SELECT * FROM :user_view_name t " +
			"ORDER BY T.PURCHASE_DATE DESC", nativeQuery = true)
	public List<Transaction> findAllByOrderByPurchaseDateDesc(@Param("user_view_name") String userId);

	//#3
	//FindAll between dates sorted by date YYYY-MM-DD format NOT INCLUSIVE TO END DATE
	@Query(value = "SELECT * FROM :user_view_name t WHERE " +
			"t.purchase_date >= :start AND " +
			"t.purchase_date < :end ORDER BY t.purchase_date DESC", nativeQuery = true)
	public List<Transaction> findAllBetweenPurchaseDatesOrderByPurchaseDateDesc(
			@Param("user_view_name") String userId, @Param("start") LocalDate from,
			@Param("end") LocalDate to);

	//#4
	//findAll transactions with matching purchaseDate
	@Query(value = "SELECT COUNT(*) FROM :user_view_name t " +
			"WHERE t.purchase_date=:purchase_date " +
			"ORDER BY t.purchase_date DESC", nativeQuery = true)
	public List<Transaction> findAllByPurchaseDate(@Param("user_view_name") String userId,
												   @Param("purchase_date") LocalDate purchaseDate);

	//#5
	//count transactions with matching purchaseDate
	@Query(value = "SELECT COUNT(*) FROM :user_view_name t " +
			"WHERE t.purchase_date=:purchase_date", nativeQuery = true)
	public long countByPurchaseDate(@Param("user_view_name") String userId,
			@Param("purchase_date") LocalDate purchaseDate);

	//#6a
	//find all transactions sorted between user_view_name and t_id OFFESET AND LIMIT
	@Query(value = "SELECT * FROM :user_view_name t " +
			"ORDER BY t.t_id DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
	public List<Transaction> findAllByOrderByTidDescPageable( @Param("user_view_name") String userId,
			@Param("limit") Long limit, @Param("offset") Long offset);


	//#7
	//find all that partially match name
	@Query(value = "SELECT * FROM :user_view_name t " +
			"WHERE t.vendor LIKE UPPER(:name)", nativeQuery=true)
	public List<Transaction> findAllLikeVendorName( @Param("user_view_name") String userId,
			@Param("name") String name);

	//#8
	//Find income tuple:
		//Vendor (source) - sum total - Cat1-Cat2...
		//Takes start and end date (typically a montly basis
	@Query(value = "SELECT * FROM BUILD_INCOME_EXPENSE_SUMMARY(:user_view_name, :start, :end, true)", nativeQuery=true)
	public List<Map<String, Object>> findIncomeAggregatedByVendorAndCategories( @Param("user_view_name") String userId,
			@Param("start") LocalDate from, @Param("end") LocalDate to);

	//#9
	//Find income tuple:
		//Vendor (source) - sum total - Cat1-Cat2...
		//Takes start and end date (typically a montly basis

	@Query(value = "SELECT * FROM BUILD_INCOME_EXPENSE_SUMMARY(:user_view_name, :start, :end, false)", nativeQuery=true)
	public List<Map<String, Object>> findExpensesAggregatedByCategoryAndBoughtFor( @Param("user_view_name") String userId,
			@Param("start") LocalDate from, @Param("end") LocalDate to);


	//#10
	//Find all categories in transactions table
	@Query(value = "SELECT t.category FROM :user_view_name t " +
			"GROUP BY category", nativeQuery = true)
	public List<String> findCategoryGroupByCategory(@Param("user_view_name") String userId);

	//#11 Find and Group By Pay Method
	@Query(value = "SELECT t.pay_method FROM :user_view_name t " +
			"GROUP BY pay_method", nativeQuery = true)
	public List<String> findPayMethodsGroupByPayMethod(@Param("user_view_name") String userId);

	//#12 Find and Group By Bought For
	@Query(value = "SELECT t.bought_for FROM :user_view_name t" +
			" GROUP BY t.bought_for", nativeQuery = true)
	public List<String> findBoughtForGroupByBoughtFor(@Param("user_view_name") String userId);

	//#13 Find and Group By Pay status
	@Query(value = "SELECT t.pay_status FROM :user_view_name t " +
			"GROUP BY t.pay_status", nativeQuery = true)
	public List<String> findPayStatusGroupByPayStatus(@Param("user_view_name") String userId);

	//#14 Find by userId and TrueId
	@Query(value = "SELECT * FROM :user_view_name t " +
			"WHERE T.true_Id=:true_id", nativeQuery = true)
	public Optional<Transaction> findByUserIdAndTrueId(@Param("user_view_name") String userId, @Param("true_id") long trueId);

	//#15 Find by userId and t_id
	@Query(value = "SELECT * FROM :user_view_name t " +
			"WHERE T.t_id=:t_id", nativeQuery = true)
	public Optional<Transaction> findByUserIdAndTid(@Param("user_view_name") String userId, @Param("t_id") long tid);
}
