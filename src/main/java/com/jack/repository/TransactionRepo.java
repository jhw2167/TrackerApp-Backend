package com.jack.repository;


import java.sql.Date;
import java.time.LocalDate;
//JAVA Imports
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Spring Imports
import com.jack.model.submodel.TransactionKey;
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
	//#0
	//simple find tranasction like id
	public Transaction findByTrueId(long trueId);

	@Query(value = "SELECT * FROM  " +
			"( " +
			"(SELECT * FROM TRANSACTION_KEYS TK  " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN  " +
			"TRANSACTIONS T  " +
			"ON T.TRUE_ID = A.TRUE_ID  AND T.t_id= :t_id" +
			") ", nativeQuery = true)
	public Optional<Transaction> findByUserIdAndTid(@Param("user_id") String userId, @Param("t_id") long tid);

	//#0a Delete by trueId
	@Query(value="DELETE FROM transactions WHERE true_id=:true_id", nativeQuery=true)
	public void deleteByTrueId(@Param("true_id") long trueId);

	//#1
	//simple find all transactions by userId
	@Query(value = "SELECT * FROM  " +
			"( " +
			"(SELECT * FROM TRANSACTION_KEYS TK  " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN  " +
			"TRANSACTIONS T  " +
			"ON T.TRUE_ID = A.TRUE_ID  " +
			")", nativeQuery = true)
	public List<Transaction> findAllByUserId(@Param("user_id") String userId);

	//#2
	//FindAll sorted by date YYYY-MM-DD format
	@Query(value = "SELECT * FROM  " +
			"( " +
			"(SELECT * FROM TRANSACTION_KEYS TK  " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN  " +
			"TRANSACTIONS T  " +
			"ON T.TRUE_ID = A.TRUE_ID  " +
			") ORDER BY T.PURCHASE_DATE DESC;", nativeQuery = true)
	public List<Transaction> findAllByOrderByPurchaseDateDesc(@Param("user_id") String userId);

	//#3
	//FindAll between dates sorted by date YYYY-MM-DD format NOT INCLUSIVE TO END DATE
	@Query(value = "SELECT * FROM  " +
			"( " +
			"(SELECT * FROM TRANSACTION_KEYS TK  " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN  " +
			"TRANSACTIONS B  " +
			"ON B.TRUE_ID = A.TRUE_ID  ) AS t " +
			"WHERE t.purchase_date >= :start AND t.purchase_date < :end " +
			"ORDER BY t.purchase_date DESC", nativeQuery=true)
	public List<Transaction> findAllBetweenPurchaseDatesOrderByPurchaseDateDesc(
			@Param("user_id") String userId, @Param("start") LocalDate from,
			@Param("end") LocalDate to);

	//#4
	//findAll transactions with matching purchaseDate
	@Query(value = "SELECT * FROM  " +
			"( " +
			"(SELECT * FROM TRANSACTION_KEYS TK  " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN  " +
			"TRANSACTIONS B  " +
			"ON B.TRUE_ID = A.TRUE_ID  ) AS t " +
			"WHERE t.purchase_date = :purchaseDate " +
			"ORDER BY t.purchase_date DESC", nativeQuery=true)
	public List<Transaction> findAllByPurchaseDate(@Param("user_id") String userId,
			@Param("purchaseDate") LocalDate purchaseDate);

	//#5
	//count transactions with matching purchaseDate
	@Query(value = "SELECT COUNT(*) FROM " +
			"( " +
			"(SELECT * FROM TRANSACTION_KEYS TK  " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN  " +
			"TRANSACTIONS B  " +
			"ON B.TRUE_ID = A.TRUE_ID  ) AS t " +
			"WHERE t.purchase_date = :purchaseDate", nativeQuery=true)
	public long countByPurchaseDate(@Param("user_id") String userId,
			@Param("purchaseDate") LocalDate purchaseDate);

	//#6a
	//find all transactions sorted between user_id and t_id OFFESET AND LIMIT
	@Query(value = "SELECT * FROM " +
			"( " +
			"(SELECT * FROM TRANSACTION_KEYS TK  " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN  " +
			"TRANSACTIONS B  " +
			"ON B.TRUE_ID = A.TRUE_ID  ) AS t " +
			"ORDER BY t.t_id DESC LIMIT :limit OFFSET :offset", nativeQuery=true)
	public List<Transaction> findAllByOrderByTidDescPageable( @Param("user_id") String userId,
			@Param("limit") Long limit, @Param("offset") Long offset);


	//#7
	//find all that partially match name
	@Query(value = "SELECT * FROM " +
			"( " +
			"(SELECT * FROM TRANSACTION_KEYS TK  " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN  " +
			"TRANSACTIONS B  " +
			"ON B.TRUE_ID = A.TRUE_ID  ) AS t " +
			"WHERE t.vendor LIKE UPPER(:name)", nativeQuery=true)
	public List<Transaction> findAllLikeVendorName( @Param("user_id") String userId,
			@Param("name") String name);

	//#8
	//Find income tuple:
		//Vendor (source) - sum total - Cat1-Cat2...
		//Takes start and end date (typically a montly basis
	@Query(value = "SELECT * FROM BUILD_INCOME_EXPENSE_SUMMARY(:user_id, :start, :end, true)", nativeQuery=true)
	public List<Map<String, Object>> findIncomeAggregatedByVendorAndCategories( @Param("user_id") String userId,
			@Param("start") LocalDate from, @Param("end") LocalDate to);

	//#9
	//Find income tuple:
		//Vendor (source) - sum total - Cat1-Cat2...
		//Takes start and end date (typically a montly basis

	@Query(value = "SELECT * FROM BUILD_INCOME_EXPENSE_SUMMARY(:user_id, :start, :end, false)", nativeQuery=true)
	public List<Map<String, Object>> findExpensesAggregatedByCategoryAndBoughtFor( @Param("user_id") String userId,
			@Param("start") LocalDate from, @Param("end") LocalDate to);


	//#10
	//Find all categories in transactions table
	@Query(value="SELECT T.category FROM " +
			"(" +
			"(SELECT * FROM TRANSACTION_KEYS TK " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN " +
			"TRANSACTIONS B " +
			"ON B.TRUE_ID = A.TRUE_ID " +
			") AS T " +
			"GROUP BY category", nativeQuery=true)
	public List<String> findCategoryGroupByCategory(@Param("user_id") String userId);

	//#11
	@Query(value="SELECT T.pay_method FROM " +
			"(" +
			"(SELECT * FROM TRANSACTION_KEYS TK " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN " +
			"TRANSACTIONS B " +
			"ON B.TRUE_ID = A.TRUE_ID " +
			") AS T " +
			"GROUP BY pay_method", nativeQuery=true)
	public List<String> findPayMethodsGroupByPayMethod(@Param("user_id") String userId);

	//#12
	@Query(value="SELECT T.bought_for FROM " +
			"(" +
			"(SELECT * FROM TRANSACTION_KEYS TK " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN " +
			"TRANSACTIONS B " +
			"ON B.TRUE_ID = A.TRUE_ID " +
			") AS T " +
			"GROUP BY bought_for", nativeQuery=true)
	public List<String> findBoughtForGroupByBoughtFor(@Param("user_id") String userId);

	//#13
	@Query(value="SELECT T.pay_status FROM " +
			"(" +
			"(SELECT * FROM TRANSACTION_KEYS TK " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN " +
			"TRANSACTIONS B " +
			"ON B.TRUE_ID = A.TRUE_ID " +
			") AS T " +
			"GROUP BY pay_status", nativeQuery=true)
	public List<String> findPayStatusGroupByPayStatus(@Param("user_id") String userId);

	//#14
	@Query(value="SELECT * FROM " +
			"(" +
			"(SELECT TRUE_ID AS TK_TRUE_ID, USER_ID FROM TRANSACTION_KEYS TK " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN " +
			"TRANSACTIONS B " +
			"ON B.TRUE_ID = A.TK_TRUE_ID " +
			") AS T " +
			"WHERE T.true_Id=:true_id", nativeQuery=true)
	public Optional<Transaction> findByUserIdAndTrueId(@Param("user_id") String userId, @Param("true_id") long trueId);
	
	
}
