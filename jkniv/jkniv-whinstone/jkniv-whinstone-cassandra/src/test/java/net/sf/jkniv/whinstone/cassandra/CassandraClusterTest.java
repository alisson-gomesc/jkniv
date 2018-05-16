package net.sf.jkniv.whinstone.cassandra;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;

public class CassandraClusterTest extends BaseJdbc
{
    private static Repository repository;
    
    //@BeforeClass
    public static void setUp()
    {
        Properties config = new Properties();
        config.put(RepositoryProperty.JDBC_URL.key(), "10.1.200.89,10.1.200.90");
        config.put(RepositoryProperty.JDBC_SCHEMA.key(),  "db3t");
        config.put(RepositoryProperty.JDBC_USER.key(), "cassandra");
        config.put(RepositoryProperty.JDBC_PASSWORD.key(), "cassandra");
        
        repository = RepositoryService.getInstance().lookup(RepositoryType.CASSANDRA).newInstance(config);
        
        
    }
    
    @Test @Ignore("cluste another enviroment")
    public void whenRun()
    {
        
        List<Map> list = repository.list(super.getQuery("all"));
        assertThat(list.isEmpty(), is(false));
        
        
    }
}
