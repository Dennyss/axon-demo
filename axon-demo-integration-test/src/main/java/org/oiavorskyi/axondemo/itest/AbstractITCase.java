package org.oiavorskyi.axondemo.itest;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.runner.RunWith;
import org.oiavorskyi.axondemo.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.ConnectionFactory;


@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(
        classes = { Application.Config.class },
        initializers = { ExecutionProfileAwareApplicationContextInitializer.class }
)
public abstract class AbstractITCase {

    @Configuration
    public static class TestUtilsConfig {

        @Autowired
        @Qualifier( "rawConnectionFactory" )
        private ConnectionFactory rawConnectionFactory;

        @Bean
        public JmsTemplate testJmsTemplate() {
            return new JmsTemplate(testConnectionFactory());
        }

        @Bean
        public ConnectionFactory testConnectionFactory() {
            CachingConnectionFactory result = new CachingConnectionFactory(rawConnectionFactory);
            result.setSessionCacheSize(2); // Don't need many connections for testing
            result.setCacheConsumers(false);
            return result;
        }
    }

    @Configuration
    @Profile( "integration" )
    public static class IntegrationEnvironmentConfig {

        @Bean
        @DependsOn("integrationBroker")
        public ConnectionFactory rawConnectionFactory() {
            return new ActiveMQConnectionFactory("vm://integration?create=false");
        }

        @Bean
        public BrokerService integrationBroker() throws Exception {
            BrokerService broker = new BrokerService();

            broker.setBrokerName("integration");
            broker.setPersistent(false);
            broker.addConnector("tcp://localhost:61616");
            broker.start();

            return broker;
        }

    }

}
