package net.sf.jkniv.whinstone.cassandra;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.Test;

import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.spi.RepositoryFactory;

public class RepositoryFactoryCassandraTest extends BaseJdbc
{
    @Test
    public void whenLookupCassandra()
    {
        RepositoryFactory factory = RepositoryService.getInstance().lookup(RepositoryType.CASSANDRA);
        Repository repository = factory.newInstance("/repository-sql.xml");
        assertThat(factory, instanceOf(RepositoryFactory.class));
        assertThat(factory, instanceOf(RepositoryFactoryCassandra.class));
        assertThat(factory.getType(), is(RepositoryType.CASSANDRA));
        assertThat(repository, instanceOf(Repository.class));
        assertThat(repository, instanceOf(RepositoryCassandra.class));
    }

}
