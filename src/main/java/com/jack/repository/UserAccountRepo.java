package com.jack.repository;

//Java Imports
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.jack.model.UserAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAccountRepo extends JpaRepository<UserAccount, String> {

    @Query(value="SELECT * FROM user_accounts WHERE user_id=:userId", nativeQuery = true)
    public Optional<UserAccount> findByUserId(@Param("userId") String userId);

    Optional<Object> findByEmail(String email);
}
