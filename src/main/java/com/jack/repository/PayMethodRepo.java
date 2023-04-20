package com.jack.repository;


//JAVA Imports

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

//Project Imports
import com.jack.model.PayMethod;


/* CRUD repository of spring provides basic "find by ___ criteria to search transactions in DB"
 * - we will also make use of sorted criteria to provide outgoing info in sorted format
 * - 
 */

public interface PayMethodRepo extends JpaRepository<PayMethod, Long>
{
	
	//simple find all pay methods
	public List<PayMethod> findAll();
	
	//finds pay method by given id
	public PayMethod findByPmId(long pmId);
	
	//find all that partially match name
	@Query(value="SELECT * FROM " +
			"(" +
			"(SELECT PMK.PM_ID AS key_pm_id, USER_ID FROM PAY_METHOD_KEYS PMK " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN " +
			"PAY_METHODS PM " +
			"ON A.key_pm_id = PM.PM_ID " +
			") AS M " +
			"WHERE M.PAY_METHOD=:method", nativeQuery=true)
	public Optional<PayMethod> findByMethodName(@Param("user_id") String userId, @Param("method") String payMethod);

	@Query(value="SELECT * FROM " +
			"(" +
			"(SELECT PMK.PM_ID AS key_pm_id, USER_ID FROM PAY_METHOD_KEYS PMK " +
			"WHERE USER_ID =:user_id) AS A " +
			"JOIN " +
			"PAY_METHODS PM " +
			"ON A.key_pm_id = PM.PM_ID " +
			") AS M " +
			"WHERE M.PM_ID=:pm_id", nativeQuery=true)
	public Optional<PayMethod> findByPmId(@Param("user_id") String userId, @Param("pm_id") String pmId);
}
