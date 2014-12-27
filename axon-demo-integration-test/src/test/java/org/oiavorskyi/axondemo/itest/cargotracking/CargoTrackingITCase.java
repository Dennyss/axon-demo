package org.oiavorskyi.axondemo.itest.cargotracking;

import org.junit.Test;
import org.oiavorskyi.axondemo.itest.AbstractITCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings( "SpringJavaAutowiringInspection" )
public class CargoTrackingITCase extends AbstractITCase {

    @Autowired
    CargoTrackingSUT.API api;

    @Test
    public void shouldStartNewTrackingByRequest() throws Exception {
        String status = api.startCargoTracking("a", "b", "c").get(1000, TimeUnit.MILLISECONDS);

        assertThat(status, is("OK"));
    }

}
