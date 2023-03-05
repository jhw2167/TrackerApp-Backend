package com.jack.repository;

//Java Imports
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.jack.model.UserAccount;

public interface UserAccountRepo extends JpaRepository<UserAccount, String> {

    public Optional<UserAccount> findByUserId(String userId);
}
