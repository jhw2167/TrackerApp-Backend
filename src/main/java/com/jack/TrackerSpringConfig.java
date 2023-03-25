package com.jack;

//Spring Imports
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

//Project imports
import com.jack.aspect.TransactionControllerAspect;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class TrackerSpringConfig {

    @Bean
    public TransactionControllerAspect transactionControllerAspect() {
        return new TransactionControllerAspect();
    }
}
