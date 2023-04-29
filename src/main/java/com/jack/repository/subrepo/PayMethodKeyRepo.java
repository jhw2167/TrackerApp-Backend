/*
package com.jack.repository.subrepo;


import com.jack.model.PayMethod;
import com.jack.model.submodel.PayMethodKey;
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

*/
/* CRUD repository of spring provides basic "find by ___ criteria to search transactions in DB"
 * - we will also make use of sorted criteria to provide outgoing info in sorted format
 * - 
 *//*


public interface PayMethodKeyRepo extends JpaRepository<PayMethodKey, Long>
{
	//simple find all transactions
	public List<PayMethodKey> findAll();

	@Query(value = "SELECT * FROM pay_method_keys pmk WHERE pm_id=:id", nativeQuery=true)
	public Optional<PayMethodKey> findByPmId(@Param("id") long pmId);

	public boolean existsByPayMethod(PayMethod pm);
}
*/
