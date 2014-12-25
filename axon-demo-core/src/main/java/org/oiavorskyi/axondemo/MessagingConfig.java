package org.oiavorskyi.axondemo;

import com.ibm.mq.jms.MQConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.ConnectionFactory;

@Configuration
@PropertySource( "/messaging.properties" )
public class MessagingConfig {

    @Bean
    public ConnectionFactory jmsConnectionFactory(
            ConnectionFactory rawConnectionFactory,
            @Value( "#{environment.getProperty('messaging.connection.pool.size')}" ) int poolSize
    ) {
        CachingConnectionFactory connFactory = new CachingConnectionFactory(rawConnectionFactory);
        connFactory.setSessionCacheSize(poolSize);

        return connFactory;
    }

    @Profile( "default" )
    @Configuration
    public static class DevelopmentConfig {

        @Bean
        public ConnectionFactory rawConnectionFactory() {
            return new ActiveMQConnectionFactory();
        }

    }

    @Profile( "production" )
    @Configuration
    public static class ProductionConfig {

        @Bean
        public ConnectionFactory rawConnectionFactory() {
            return new MQConnectionFactory();
        }

    }

}
