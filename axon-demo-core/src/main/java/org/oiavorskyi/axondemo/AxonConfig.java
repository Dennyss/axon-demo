package org.oiavorskyi.axondemo;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.fs.FileSystemEventStore;
import org.axonframework.eventstore.fs.SimpleEventFileResolver;
import org.oiavorskyi.axondemo.aggregates.CargoTracking;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class AxonConfig {

    @Bean
    public EventBus eventBus() {
        return new SimpleEventBus();
    }

    @Bean
    public EventSourcingRepository<CargoTracking> cargoTrackingRepository( EventStore eventStore ) {
        EventSourcingRepository<CargoTracking> repository =
                new EventSourcingRepository<>(CargoTracking.class, eventStore);
        repository.setEventBus(eventBus());

        return repository;
    }


    @Profile( "default" )
    @Configuration
    public static class DevelopmentConfig {

        @Bean
        EventStore eventStore() throws IOException {
            // TODO: Initialize proper Dev version of store
            Path tempDirectory = Files.createTempDirectory("axon-demo-events");
            return new FileSystemEventStore(new SimpleEventFileResolver(tempDirectory.toFile()));
        }

    }

    @Profile( "production" )
    @Configuration
    public static class ProductionConfig {

        @Bean
        EventStore eventStore() throws IOException {
            // TODO: Initialize proper Production version of store
            Path tempDirectory = Files.createTempDirectory("axon-demo-events");
            return new FileSystemEventStore(new SimpleEventFileResolver(tempDirectory.toFile()));
        }

    }

}
