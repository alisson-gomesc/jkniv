package be.jkniv.whinstone.tck.datasource;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.Date;
import java.util.Map;

import org.junit.Test;

import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.cassandra.RepositoryFactoryCassandra;
import net.sf.jkniv.whinstone.couchdb.RepositoryFactoryCouchDb;
import net.sf.jkniv.whinstone.jdbc.RepositoryFactoryJdbc;
import net.sf.jkniv.whinstone.jpa2.RepositoryFactoryJpa;
import net.sf.jkniv.whinstone.spi.RepositoryFactory;

public class RepositoryFactoryTest
{
    private static final String PACKAGE_JPA       = "net.sf.jkniv.whinstone.jpa2";
    private static final String PACKAGE_JDBC      = "net.sf.jkniv.whinstone.jdbc";
    private static final String PACKAGE_CASSANDRA = "net.sf.jkniv.whinstone.cassandra";
    private static final String PACKAGE_COUCHDB   = "net.sf.jkniv.whinstone.couchdb";
    
    @Test
    public void whenLookupJdbc()
    {
        RepositoryFactory factory = RepositoryService.getInstance().lookup(RepositoryType.JDBC);
        Repository repository = factory.newInstance("/repository-sql-jdbc.xml");
        assertThat(factory, instanceOf(RepositoryFactory.class));
        assertThat(factory, instanceOf(RepositoryFactoryJdbc.class));
        assertThat(factory.getType(), is(RepositoryType.JDBC));
        assertThat(repository, instanceOf(Repository.class));
        assertThat(repository.getClass().getName(), is(PACKAGE_JDBC + ".RepositoryJdbc"));
    }

    @Test
    public void whenLookupJpa()
    {
        RepositoryFactory factory = RepositoryService.getInstance().lookup(RepositoryType.JPA);
        Repository repository = factory.newInstance("/repository-sql-jpa.xml");
        assertThat(factory, instanceOf(RepositoryFactory.class));
        assertThat(factory, instanceOf(RepositoryFactoryJpa.class));
        assertThat(factory.getType(), is(RepositoryType.JPA));
        assertThat(repository, instanceOf(Repository.class));
        assertThat(repository.getClass().getName(), is(PACKAGE_JPA + ".RepositoryJpa"));
    }

    @Test
    public void whenLookupCassandra()
    {
        RepositoryFactory factory = RepositoryService.getInstance().lookup(RepositoryType.CASSANDRA);
        Repository repository = factory.newInstance("/repository-sql-cassandra.xml");
        assertThat(factory, instanceOf(RepositoryFactory.class));
        assertThat(factory, instanceOf(RepositoryFactoryCassandra.class));
        assertThat(factory.getType(), is(RepositoryType.CASSANDRA));
        assertThat(repository, instanceOf(Repository.class));
        assertThat(repository.getClass().getName(), is(PACKAGE_CASSANDRA + ".RepositoryCassandra"));
    }

    @Test
    public void whenLookupCouchdb()
    {
        RepositoryFactory factory = RepositoryService.getInstance().lookup(RepositoryType.COUCHDB);
        Repository repository = factory.newInstance("/repository-sql-couchdb.xml");
        assertThat(factory, instanceOf(RepositoryFactory.class));
        assertThat(factory, instanceOf(RepositoryFactoryCouchDb.class));
        assertThat(factory.getType(), is(RepositoryType.COUCHDB));
        assertThat(repository, instanceOf(Repository.class));
        assertThat(repository.getClass().getName(), is(PACKAGE_COUCHDB + ".RepositoryCouchDb"));
    }

}
