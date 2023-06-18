package com.jack;

//Spring Imports
import com.jack.aspect.TransactionAspect.TransactionControllerAspect;
import com.jack.aspect.TransactionAspect.TransactionRepoAspect;
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
}
