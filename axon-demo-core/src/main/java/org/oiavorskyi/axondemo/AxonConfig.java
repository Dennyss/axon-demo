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


    @Bean
    EventStore eventStore() throws IOException {
        // TODO: Change to the proper version of event store (e.g. RDBMS or Redis-based)
        Path tempDirectory = Files.createTempDirectory("axon-demo-events");
        return new FileSystemEventStore(new SimpleEventFileResolver(tempDirectory.toFile()));
    }

}
