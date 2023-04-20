package com.jack.service;

//Java Imports
import java.util.Optional;

//Spring imports
import com.jack.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

//Project Imports
import com.jack.repository.UserAccountRepo;


@Service
public class UserAccountService {

    @Autowired
    UserAccountRepo repo;

    public UserAccount getUserAccountById(String userId) throws ResourceNotFoundException {
        if(repo.existsById(userId))
            return repo.findById(userId).get();
        else
            throw new ResourceNotFoundException(String.format("No user account found with id: %s", userId));
    }

}
