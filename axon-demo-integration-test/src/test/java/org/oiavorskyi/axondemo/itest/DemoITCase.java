package org.oiavorskyi.axondemo.itest;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.ConnectionFactory;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class DemoITCase extends AbstractITCase {

    @Autowired
    private ConnectionFactory rawConnectionFactory;

    @Test
    public void simpleTest() throws Exception {
        assertThat(rawConnectionFactory, instanceOf(ActiveMQConnectionFactory.class));
    }

}
