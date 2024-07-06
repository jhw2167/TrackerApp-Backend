package com.jack.service;

//Java Imports
import java.util.List;
import java.util.Optional;

//Spring imports
import com.jack.model.UserAccount;
import com.jack.model.dto.UserAccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

//Project Imports
import com.jack.repository.UserAccountRepo;
import org.springframework.web.client.HttpClientErrorException;


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

    public List<UserAccount> getAllUserAccounts() {
        return repo.findAll();
    }

    public UserAccount createUserAccount(UserAccountDto user) {

        System.out.println("Creating user: " + user.toString());
        UserAccount u = new UserAccount( user );

        System.out.println("Creating user: " + u.toString());

        if( u == null )
            throw new HttpClientErrorException( HttpStatus.BAD_REQUEST, "UserAccountDto is null");

        if( repo.findByEmail( u.getEmail() ).isPresent() )
            throw new HttpClientErrorException( HttpStatus.CONFLICT, String.format("User with email: %s already exists", u.getEmail()) );


        if( repo.existsById( u.getUserId() ) )
            throw new HttpClientErrorException( HttpStatus.CONFLICT, String.format("User with id: %s already exists", u.getUserId()) );

        System.out.println("Creating user: " + user.toString());
        return repo.save(u);
    }
}
