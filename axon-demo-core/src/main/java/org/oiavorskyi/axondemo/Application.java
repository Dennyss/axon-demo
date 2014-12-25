package org.oiavorskyi.axondemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Application {

    private static final String[] VALID_PROFILES = new String[] { "production" };
    private static       Logger   log            = LoggerFactory.getLogger(Application.class);

    public static void main( String[] args ) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Config.class);

        String executionProfile = identifyCurrentExecutionProfile();
        log.info("Using {} execution profile for Spring context", executionProfile);
        context.getEnvironment().setActiveProfiles(executionProfile);

        context.refresh();
        context.registerShutdownHook();

        log.info("Application has successfully started");
    }

    /**
     * Identifies execution profile to be used. Only Spring beans configured within this profile or
     * no profile at all will be loaded. This opens possibility to switch between different
     * environments without any code changes.
     *
     * This method looks for a file with name "runtime.profile" in the directory from where process
     * was started and if it exists assumes first line in this file as a name of profile.
     *
     * @return name of Spring profile to be used for execution of application
     */
    public static String identifyCurrentExecutionProfile() {
        String result = "default";

        log.debug("Identifying execution profile: working directory is {}",
                Paths.get("").toAbsolutePath().normalize().toString());

        Path pathToRuntimeProfileMarkerFile = FileSystems.getDefault().getPath("runtime.profile");
        boolean markerExists = Files.exists(pathToRuntimeProfileMarkerFile);

        if ( markerExists ) {
            try {
                List<String> values = Files.readAllLines(pathToRuntimeProfileMarkerFile,
                        Charset.defaultCharset());
                String profileName = values.get(0);
                log.debug("Identifying execution profile: found runtime.profile file with value " +
                        profileName);
                if ( Arrays.binarySearch(VALID_PROFILES, profileName) >= 0 ) {
                    result = profileName;
                }
            } catch ( IOException e ) {
                // Ignore exception and assume default profile
            }
        } else {
            log.debug("Identifying execution profile: no runtime.profile file was found");
        }

        return result;
    }

    @Configuration
    @ComponentScan( { "org.oiavorskyi.axondemo" } )
    @PropertySource( "/application.properties" )
    public static class Config {


    }
}
