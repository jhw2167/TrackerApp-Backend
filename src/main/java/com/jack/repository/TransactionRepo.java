package com.jack.repository;


import java.sql.Date;
import java.time.LocalDate;
//JAVA Imports
import java.util.List;
import java.util.Map;

//Spring Imports
import org.springframework.data.jpa.repository.Query;
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
	//simple find all transactions
	public List<Transaction> findAll();
	
	//simple find tranasction like id
	public Transaction findBytId(Long tId);
		
	
	//FindAll sorted by date YYYY-MM-DD format
	public List<Transaction> findAllByOrderByPurchaseDateDesc();
	
	//FindAll between dates sorted by date YYYY-MM-DD format
	@Query(value="SELECT * FROM transactions AS t WHERE t.purchase_date >= :start AND t.purchase_date < :end ORDER BY t.purchase_date DESC", nativeQuery=true)
	public List<Transaction> findAllBetweenPurchaseDatesOrderByPurchaseDateDesc(
			@Param("start") LocalDate from, @Param("end") LocalDate to);
		
	
	//findAll transactions with matching purchaseDate
	public List<Transaction> findAllByPurchaseDate(LocalDate purchaseDate);
	
	//count transactions with matching purchaseDate
	public long countByPurchaseDate(LocalDate purchaseDate);
	
	//find all transactions sorted between start and end
	@Query(value="SELECT * FROM transactions AS t ORDER BY t.t_id DESC LIMIT :limit OFFSET :offset", nativeQuery=true)
	public List<Transaction> findAllByOrderByTidDescPageable( @Param("limit") Long limit, @Param("offset") Long offset);
	
	
	//find all that partially match name
	@Query(value="SELECT * FROM TRANSACTIONS t WHERE t.vendor LIKE UPPER(:name)", nativeQuery=true)
	public List<Transaction> findAllLikeVendorName(@Param("name") String name);

	
	//Find income tuple:
		//Vendor (source) - sum total - Cat1-Cat2...
		//Takes start and end date (typically a montly basis
	@Query(value="SELECT v1 as aggregateCol, SUM(sum1) / COUNT(cat) as value, STRING_AGG(cat, '/') as categories FROM\r\n"
			+ "( SELECT vendor AS v1, SUM(amount) AS sum1 \r\n"
			+ "FROM TRANSACTIONS t1\r\n"
			+ "WHERE t1.IS_INCOME=TRUE AND \r\n"
			+ "t1.purchase_date >= :start AND\r\n"
			+ "t1.purchase_date < :end \r\n"
			+ "GROUP BY t1.VENDOR ) AS a\r\n"
			+ "INNER JOIN \r\n"
			+ "(\r\n"
			+ "SELECT DISTINCT t2.VENDOR AS v2, t2.category AS cat \r\n"
			+ "FROM TRANSACTIONS t2 \r\n"
			+ "WHERE t2.IS_INCOME =TRUE AND \r\n"
			+ "t2.purchase_date >= :start AND \r\n"
			+ "t2.purchase_date < :end \r\n"
			+ ") b ON a.v1=b.v2 GROUP BY v1 ORDER BY value DESC", nativeQuery=true)
	public List<Map<String, Object>> findIncomeAggregatedByVendorAndCategories(
			@Param("start") LocalDate from, @Param("end") LocalDate to);
	
	
	//Find income tuple:
		//Vendor (source) - sum total - Cat1-Cat2...
		//Takes start and end date (typically a montly basis
	
	@Query(value="SELECT c1 as aggregateCol, SUM(sum1) / COUNT(BOUGHT_FOR) as value, STRING_AGG(BOUGHT_FOR, '/') as categories"
			+ " FROM\r\n"
			+ "( SELECT CATEGORY AS c1, SUM(amount) AS sum1 \r\n"
			+ "FROM TRANSACTIONS t1\r\n"
			+ "WHERE t1.IS_INCOME=false AND \r\n"
			+ "t1.purchase_date >= :start AND\r\n"
			+ "t1.purchase_date < :end \r\n"
			+ "GROUP BY t1.CATEGORY ) AS a\r\n"
			+ "INNER JOIN \r\n"
			+ "(\r\n"
			+ "SELECT DISTINCT t2.CATEGORY AS c2, t2.BOUGHT_FOR \r\n"
			+ "FROM TRANSACTIONS t2 \r\n"
			+ "WHERE t2.IS_INCOME =false AND \r\n"
			+ "t2.purchase_date >= :start AND \r\n"
			+ "t2.purchase_date < :end \r\n"
			+ ") b ON a.c1=b.c2 GROUP BY c1 ORDER BY value DESC", nativeQuery=true)
	public List<Map<String, Object>> findExpensesAggregatedByCategoryAndBoughtFor(
			@Param("start") LocalDate from, @Param("end") LocalDate to);
	
	
	
	//Find all categories in transactions table
	@Query(value="SELECT t.category FROM TRANSACTIONS t GROUP BY CATEGORY", nativeQuery=true)
	public List<String> findCategoryGroupByCategory();
		
	@Query(value="SELECT t.PAY_METHOD FROM TRANSACTIONS t GROUP BY PAY_METHOD", nativeQuery=true)
	public List<String> findPayMethodsGroupByPayMethod();
	
	
	@Query(value="SELECT t.bought_for FROM TRANSACTIONS t GROUP BY bought_for", nativeQuery=true)
	public List<String> findBoughtForGroupByBoughtFor();
	
	
	@Query(value="SELECT t.PAY_STATUS FROM TRANSACTIONS t GROUP BY PAY_STATUS", nativeQuery=true)
	public List<String> findPayStatusGroupByPayStatus();
	
	
}
