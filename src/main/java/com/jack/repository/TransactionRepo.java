package com.jack.repository;


import java.time.LocalDate;
//JAVA Imports
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Spring Imports
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

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
	Boolean createUserTransactionsView(String userViewName,
										   @Param("user_id") String userId);
	@Procedure(procedureName = "drop_user_transactions_view")
	Boolean dropUserTransactionsView(String userViewName);


	//#0
	//simple find tranasction like id
	Transaction findByTrueId(long trueId);

	//#0a Delete by trueId
	@Modifying
	@Query(value="DELETE FROM transactions WHERE true_id=:true_id", nativeQuery=true)
	public void deleteByTrueId(@Param("true_id") long trueId);

	//#1
	//simple find all transactions by userId
	//@Query(value = "SELECT * FROM :user_view_name t", nativeQuery = true)
	List<Transaction> findAllByUserUserId(String userId);

	//#2
	//FindAll sorted by date YYYY-MM-DD format
	List<Transaction> findByUserUserIdOrderByPurchaseDateDesc(String userId);

	//#3
	//FindAll between dates sorted by date YYYY-MM-DD format inclusive of end date
	List<Transaction> findByUserUserIdAndPurchaseDateBetweenOrderByPurchaseDateDesc(
			String userId, LocalDate from, LocalDate to);

	//#4
	//findAll transactions with matching purchaseDate
	List<Transaction> findByUserUserIdAndPurchaseDate(String userId, LocalDate purchaseDate);

	//#5
	//count transactions with matching purchaseDate
	Long countByUserUserIdAndPurchaseDate(String userId, LocalDate purchaseDate);


	//#6a
	//find all transactions sorted between user_view_name and t_id OFFESET AND LIMIT
	List<Transaction> findAllByUserUserIdOrderByTidDesc(String userId, Pageable pageable);



	//#7
	//find all that partially match name
	//@Query(value = "SELECT * FROM :user_view_name t " +
	//		"WHERE t.vendor LIKE UPPER(:name)", nativeQuery=true)
	public List<Transaction> findAllByUserUserIdAndVendorVendorNameLike(String userId, String vendorName);

	//#8
	//Find income tuple:
		//Vendor (source) - sum total - Cat1-Cat2...
		//Takes start and end date (typically a montly basis
	@Query(value = "SELECT * FROM BUILD_INCOME_EXPENSE_SUMMARY(:user_view_name, :start, :end, true)", nativeQuery=true)
	public List<Map<String, Object>> findIncomeAggregatedByVendorAndCategories(@Param("user_view_name") String userId,
			@Param("start") LocalDate from, @Param("end") LocalDate to);

	//#9
	//Find income tuple:
		//Vendor (source) - sum total - Cat1-Cat2...
		//Takes start and end date (typically a montly basis)
	@Query(value = "SELECT * FROM BUILD_INCOME_EXPENSE_SUMMARY(:user_view_name, :start, :end, false)", nativeQuery=true)
	public List<Map<String, Object>> findExpensesAggregatedByCategoryAndBoughtFor(@Param("user_view_name") String userId,
			@Param("start") LocalDate from, @Param("end") LocalDate to);


	//#10
	//Find all categories in transactions table
	@Query("SELECT DISTINCT t.category FROM Transaction t WHERE t.user.userId = :userId")
	List<String> findDistinctCategoryByUserUserId(String userId);

	//#11 Find and Group By Pay Method
	@Query("SELECT DISTINCT t.payMethod FROM Transaction t WHERE t.user.userId = :userId")
	List<PayMethod> findDistinctPayMethodByUserUserId(String userId);


	//#12 Find and Group By Bought For
	@Query("SELECT DISTINCT t.boughtFor FROM Transaction t WHERE t.user.userId = :userId GROUP BY t.boughtFor")
	List<String> findDistinctBoughtForByUserUserId(String userId);


	//#13 Find and Group By Pay status
	@Query("SELECT DISTINCT t.payStatus FROM Transaction t WHERE t.user.userId = :userId GROUP BY t.payStatus")
	List<String> findDistinctPayStatusByUserUserId(String userId);


	//#14 Find by userId and TrueId
	public Optional<Transaction> findByUserUserIdAndTrueId(String userId, long trueId);

	//#15 Find by userId and t_id
	public Optional<Transaction> findByUserUserIdAndTid(String userId, long tid);

	//#16 Find all transactions having given vendor
	List<Transaction> findAllByVendor(Vendor v);
}
