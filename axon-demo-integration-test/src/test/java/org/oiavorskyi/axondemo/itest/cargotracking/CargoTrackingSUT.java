package org.oiavorskyi.axondemo.itest.cargotracking;

import org.oiavorskyi.axondemo.itest.JmsRequestor;
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

        @Autowired
        private JmsRequestor requestor;

        @Autowired
        private Destination inboundCommandsDestination;

        @Override
        public Future<String> startCargoTracking( String cargoId, String correlationId,
                                                  String timestamp ) {
            return requestor.sendRequest("test", inboundCommandsDestination);
//                    new StartCargoTrackingMessage(cargoId, correlationId, timestamp));
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
