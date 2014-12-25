package org.oiavorskyi.axondemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

public class Application {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main( String[] args ) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Config.class);

        // Registering application.properties to enable resolution of production property setting
        ConfigurableEnvironment env = context.getEnvironment();
        env.getPropertySources().addFirst(new ResourcePropertySource("/application.properties"));

        // Switching to "production" profile if property is set. This would allow loading of
        // dedicated @Configuration files with production-specific beans.
        // Default profile will be used otherwise which is helpful for development and testing
        boolean isProduction = env.getProperty("application.production", Boolean.class, false);
        if ( isProduction ) {
            env.setActiveProfiles("production");
            log.info("The application.production property is set to true. Switching to " +
                    "production profile");
        } else {
            log.info("The application.production property is set to false or absent. Using " +
                    "default development profile");
        }

        context.refresh();
        context.registerShutdownHook();

        log.info("Application has successfully started");
    }

    @Configuration
    @ComponentScan( { "org.oiavorskyi.axondemo" } )
    public static class Config {


    }
}
