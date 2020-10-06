package net.sf.jkniv.whinstone.cassandra;

import java.util.Properties;

import org.junit.Test;

import net.sf.jkniv.reflect.beans.Capitalize.PropertyType;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;

public class AstraCassandraTest
{

    private static final String KEY_FILE = "/database/astra-secure-connect-jkniv.zip";
    private static final String USER = "sysadmin";
    private static final String PASS = "Whin123456";
    
    
    @Test
    public void whenConnectToAstraCassandra()
    {
        Properties props = new Properties();
        props.put(RepositoryProperty.JDBC_USER.key(), USER);
        props.put(RepositoryProperty.JDBC_PASSWORD.key(), PASS);
        props.put(RepositoryProperty.JDBC_SCHEMA.key(), "whinstone");
        props.put(RepositoryProperty.KEY_FILE.key(), KEY_FILE);
        
        Repository repository = RepositoryService.getInstance().lookup(RepositoryType.CASSANDRA).newInstance(props);
        
        repository.list(QueryFactory.of("colors-by-name", "name", "blue"));
        
    }
}
