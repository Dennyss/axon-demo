package org.oiavorskyi.axondemo.itest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CargoTrackingITCase extends AbstractITCase {

    @Autowired
    CargoTrackingSUT.API api;

    @Test
    public void simpleTest() throws Exception {
        api.startCargoTracking("a", "b", "c");
    }

}
