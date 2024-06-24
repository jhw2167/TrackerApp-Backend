package com.jack;

//Spring Imports
import com.jack.aspect.transactionaspect.TransactionControllerAspect;
import com.jack.aspect.transactionaspect.TransactionRepoAspect;
import com.jack.aspect.useraspect.UserControllerAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

//Project imports
import com.jack.aspect.*;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class TrackerSpringConfig {

    /* General */
    @Bean
    public LoggerAspect loggerAspect() { return new LoggerAspect(); }


    /* Transaction */

    @Bean
    public TransactionControllerAspect transactionControllerAspect() {
        return new TransactionControllerAspect();
    }
    @Bean
    public TransactionRepoAspect transactionRepoAspect() {
        return new TransactionRepoAspect();
    }

    /* User */
    @Bean
    public UserControllerAspect userControllerAspect() {
        return new UserControllerAspect();
    }
}
