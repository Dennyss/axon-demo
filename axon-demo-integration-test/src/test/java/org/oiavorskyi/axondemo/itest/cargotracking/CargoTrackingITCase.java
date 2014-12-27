package org.oiavorskyi.axondemo.itest.cargotracking;

import org.axonframework.eventsourcing.EventSourcingRepository;
import org.junit.Test;
import org.oiavorskyi.axondemo.aggregates.CargoTracking;
import org.oiavorskyi.axondemo.itest.AbstractITCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@SuppressWarnings( "SpringJavaAutowiringInspection" )
public class CargoTrackingITCase extends AbstractITCase {

    @Autowired
    CargoTrackingSUT.API api;

    @Autowired
    EventSourcingRepository<CargoTracking> cargoTrackingRepository;

    @Test
    public void shouldStartNewTrackingByRequest() throws Exception {
        String status = api.startCargoTracking("testCargoId", "testCorrelationId", "someTimestamp")
                           .get(1000, TimeUnit.MILLISECONDS);

        CargoTracking cargoTracking = cargoTrackingRepository.load("testCorrelationId");

        assertThat(cargoTracking, notNullValue());

        assertThat(status, is("OK"));
    }

}
