package org.oiavorskyi.axondemo.itest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

@Configuration
public class CargoTrackingSUT {

    @Autowired
    @Qualifier( "rawConnectionFactory" )
    private ConnectionFactory rawConnectionFactory;

    @Bean
    public API api( @Qualifier("inboundCommandsDestination") Destination destination ) {
        return new JmsAPIImpl(testJmsTemplate(), destination);
    }

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


    public static interface API {

        public void startCargoTracking( String cargoId, String correlationId, String timestamp );

    }

    private static class JmsAPIImpl implements API {

        private final JmsTemplate template;
        private final Destination destination;

        public JmsAPIImpl( JmsTemplate template, Destination destination ) {
            this.template = template;
            this.destination = destination;
        }

        @Override
        public void startCargoTracking( String cargoId, String correlationId, String timestamp ) {
            template.convertAndSend(destination,
                    new StartCargoTrackingMessage(cargoId, correlationId, timestamp));
        }
    }

    private static final class StartCargoTrackingMessage {

        final String cargoId;
        final String correlationId;
        final String timestamp;

        public StartCargoTrackingMessage( String cargoId, String correlationId, String timestamp ) {
            this.cargoId = cargoId;
            this.correlationId = correlationId;
            this.timestamp = timestamp;
        }
    }
}
