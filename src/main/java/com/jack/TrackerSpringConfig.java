package com.jack;

//Spring Imports
import com.jack.model.Transaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

//Project imports
import com.jack.aspect.*;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class TrackerSpringConfig {

    @Bean
    public TransactionControllerAspect transactionControllerAspect() {
        return new TransactionControllerAspect();
    }
    @Bean
    public LoggerAspect loggerAspect() {
        return new LoggerAspect();
    }

    @Bean
    public TransactionRepoAspect transactionRepoAspect() {
        return new TransactionRepoAspect();
    }
}
