package org.oiavorskyi.axondemo.itest;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oiavorskyi.axondemo.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.ConnectionFactory;

import static org.hamcrest.CoreMatchers.*;


@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = { Application.Config.class,
        SimpleITCase.StubConfiguration.class } )
@ActiveProfiles( resolver = SimpleITCase.EnvironmentBasedProfileResolver.class )
public class SimpleITCase {

    @Autowired
    private ConnectionFactory rawConnectionFactory;

    @Test
    public void simpleTest() throws Exception {
        Assert.assertThat(rawConnectionFactory, instanceOf(ActiveMQConnectionFactory.class));
    }

    public static class EnvironmentBasedProfileResolver implements ActiveProfilesResolver {

        @Override
        public String[] resolve( Class<?> testClass ) {
            String profile = "integration";
            String executionProfile = Application.identifyCurrentExecutionProfile();

            if ( !"default" .equals(executionProfile) ) {
                // Overriding default integration profile to allow running smoke tests in
                // production environment(s)
                profile = executionProfile;
            }
            return new String[] { profile };
        }
    }

    @Configuration
    @Profile( "integration" )
    public static class StubConfiguration {

        @Bean
        public ConnectionFactory rawConnectionFactory() {
            return new ActiveMQConnectionFactory();
        }

    }

}
