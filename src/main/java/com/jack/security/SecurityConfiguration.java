package com.jack.security;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


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
			http.csrf().disable()
            .authorizeRequests()
            .anyRequest().permitAll()
            .and().httpBasic();
			
            //http.authorizeRequests().antMatchers("/**").hasRole("USER").and().formLogin();
					
    }
	
	//All a jumble to allow React to access spring through CORS
	//https://stackoverflow.com/questions/59775325/origin-has-been-blocked-by-cors-policy-spring-boot-and-react
	//You can see we are just allowing all Origins (domains) headers, and methods to pass
	 @Bean
	 public FilterRegistrationBean corsFilter() {
	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        CorsConfiguration config = new CorsConfiguration();
	        config.addAllowedOrigin("*");
	        config.addAllowedHeader("*");
	        config.addAllowedMethod("*");
	        source.registerCorsConfiguration("/**", config);
	        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
	        bean.setOrder(0);
	        return bean;
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
