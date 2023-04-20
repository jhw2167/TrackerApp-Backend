package com.jack.repository.subrepo;


import com.jack.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Project Imports
import com.jack.model.submodel.TransactionKey;

/* CRUD repository of spring provides basic "find by ___ criteria to search transactions in DB"
 * - we will also make use of sorted criteria to provide outgoing info in sorted format
 * - 
 */

public interface TransactionKeyRepo extends JpaRepository<TransactionKey, Long>
{
	//simple find all transactions
	public List<TransactionKey> findAll();
	
	//simple Find TransactionKey using userId and Tid
	@Query(value = "SELECT * FROM  " +
			"( " +
			"(SELECT * FROM TRANSACTION_KEYS TK  " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN  " +
			"TRANSACTIONS T  " +
			"ON T.TRUE_ID = A.TRUE_ID  AND T.t_id= :t_id" +
			") ", nativeQuery = true)
	public Optional<TransactionKey> findByUserIdAndTid(@Param("user_id") String userId, @Param("t_id") long tid);

	@Query(value="DELETE FROM transaction_keys WHERE true_id=:true_id", nativeQuery=true)
	public void deleteByTrueId(@Param("true_id") long trueId);

	@Query(value="SELECT USER_ID FROM TRANSACTION_KEYS TK WHERE TRUE_ID=:true_id", nativeQuery=true)
	public String findUserIdByTrueId(@Param("true_id") long trueId);
}
