package com.jack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/*
 * Gives us some objects to control spring Security
 * 
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
		//Authentication for security
	@Override
    protected void configure(HttpSecurity http) throws Exception {
			//System.out.println("Attemptempting to validate creds");
			
			//Permit requests to certain URLS
			http.authorizeRequests()  
	        .antMatchers("/transactions").permitAll()       
	        .antMatchers("/transactions/*").permitAll();
			
			//Allow post methods
			http.cors().and().csrf().disable();
			
            //http.authorizeRequests().antMatchers("/**").hasRole("USER").and().formLogin();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    		//add simple in memory user access
    		//System.out.println("Memory creds loaded");
            auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
    }
	
	
    //Need a stanard password encoder to return
	 @Bean public PasswordEncoder getPasswordEncoder() {
			return NoOpPasswordEncoder.getInstance();
	 }
    //END GET PASSWORD ENCODER
    
}
//END CLASS SECURITY CONFIG
