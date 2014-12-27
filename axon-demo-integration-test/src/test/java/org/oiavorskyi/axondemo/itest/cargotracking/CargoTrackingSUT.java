package org.oiavorskyi.axondemo.itest.cargotracking;

import org.oiavorskyi.axondemo.itest.JmsRequester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Destination;
import java.util.concurrent.Future;

@Configuration
public class CargoTrackingSUT {

    @Bean
    public API api() {
        return new JmsAPIImpl();
    }

    public static interface API {

        public Future<String> startCargoTracking( String cargoId, String correlationId, String
                timestamp );

    }

    private static class JmsAPIImpl implements API {

        @SuppressWarnings( "SpringJavaAutowiringInspection" )
        @Autowired
        private JmsRequester requester;

        @Autowired
        private Destination inboundCommandsDestination;

        @Override
        public Future<String> startCargoTracking( String cargoId, String correlationId,
                                                  String timestamp ) {
            // So far we support only String messages but JSON will be added soon
            return requester.sendRequest(
                    new StartCargoTrackingMessage(cargoId, correlationId, timestamp).toString(),
                    inboundCommandsDestination);
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

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("StartCargoTrackingMessage{");
            sb.append("cargoId='").append(cargoId).append('\'');
            sb.append(", correlationId='").append(correlationId).append('\'');
            sb.append(", timestamp='").append(timestamp).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
