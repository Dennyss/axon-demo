package org.oiavorskyi.axondemo.itest;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oiavorskyi.axondemo.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.ConnectionFactory;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;


@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(
        classes = { Application.Config.class, SimpleITCase.StubConfiguration.class },
        initializers = { ExecutionProfileAwareApplicationContextInitializer.class}
)
public class SimpleITCase {

    @Autowired
    private ConnectionFactory rawConnectionFactory;

    @Test
    public void simpleTest() throws Exception {
        assertThat(rawConnectionFactory, instanceOf(ActiveMQConnectionFactory.class));
    }

    @Configuration
    @Profile( "integration" )
    public static class StubConfiguration {

        @Bean
        public ConnectionFactory rawConnectionFactory() {
            return new ActiveMQConnectionFactory();
        }

    }

}
