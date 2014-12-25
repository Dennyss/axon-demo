package org.oiavorskyi.axondemo.itest;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.runner.RunWith;
import org.oiavorskyi.axondemo.Application;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.ConnectionFactory;


@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(
        classes = { Application.Config.class, AbstractITCase.StubConfiguration.class },
        initializers = { ExecutionProfileAwareApplicationContextInitializer.class}
)
public abstract class AbstractITCase {

    @Configuration
    @Profile( "integration" )
    public static class StubConfiguration {

        @Bean
        public ConnectionFactory rawConnectionFactory() {
            return new ActiveMQConnectionFactory();
        }

    }

}
