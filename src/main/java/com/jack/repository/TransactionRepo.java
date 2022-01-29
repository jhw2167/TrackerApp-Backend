package com.jack.repository;


//JAVA Imports
import java.util.List;

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

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long>
{
	//Find all categories in transactions table
	@Query(value="SELECT t.category FROM TRANSACTIONS t GROUP BY CATEGORY", nativeQuery=true)
	public List<String> findCategoryGroupByCategory();
	
	//simple find all transactions
	public List<Transaction> findAll();
	
	//FindAll sorted by date YYYY-MM-DD format
	public List<Transaction> findAllByOrderByPurchaseDateDesc();
	
	//findAll transactions with matching purchaseDate
	public List<Transaction> findAllByPurchaseDate(String purchaseDate);
	
	//count transactions with matching purchaseDate
	public long countByPurchaseDate(String purchaseDate);
	
	//find all transactions sorted between start and end
	@Query(value="SELECT * FROM transactions AS t ORDER BY t.purchaseDate DESC LIMIT :total OFFSET :offset", nativeQuery=true)
	public List<Transaction> findAllByOrderByPurchaseDateDescPageable( @Param("total") Integer total, @Param("offset") Integer offset);
	
	
}
