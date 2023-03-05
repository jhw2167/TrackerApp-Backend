package com.jack.service;

//Java Imports
import java.util.Optional;

//Spring imports
import com.jack.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Project Imports
import com.jack.repository.UserAccountRepo;


@Service
public class UserAccountService {

    @Autowired
    UserAccountRepo repo;

    public Optional<UserAccount> getUserAccountById(String userId) {
        return repo.findByUserId(userId);
    }

}
