package net.sf.jkniv.whinstone.cassandra;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;

/**
 * EXERCISE 2 : Connect to Astra using zip bundle and credentials.
 * 
 * @author Developer Advocate Team
 */
public class Ex02_Connect_to_Cassandra {

    /** Logger for the class. */
    private static Logger LOGGER = LoggerFactory.getLogger("Exercise2");
    
    @Test
    public void should_connect_to_Astra() throws MalformedURLException {
        LOGGER.info("========================================");
        LOGGER.info("Start exercise");
        // When
        Cluster cluster = Cluster.builder()
                .withCloudSecureConnectBundle(new URL("file:///C:/dev/wks/wks-jkniv-git/jkniv-whinstone/jkniv-whinstone-cassandra/target/test-classes/database/astra-secure-connect-jkniv.zip"))
                .withCredentials("sysadmin", "Whin123456")
                .build();
            
        // Then
        cluster.connect("whinstone");
        LOGGER.info("SUCCESS");
        LOGGER.info("========================================");
    }
}
