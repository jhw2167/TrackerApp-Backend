package com.jack.repository;


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
	@Query(value="SELECT * FROM TRANSACTION_KEYS t " +
			"WHERE t.user_id=:userId and t.t_id=:tId ", nativeQuery=true)
	public Optional<TransactionKey> findByUserIdAndTid(String userId, Long tId);

}
