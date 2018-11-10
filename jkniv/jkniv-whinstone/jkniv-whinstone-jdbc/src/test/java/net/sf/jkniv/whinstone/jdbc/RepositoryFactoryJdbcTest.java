package net.sf.jkniv.whinstone.jdbc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.Test;

import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.spi.RepositoryFactory;

public class RepositoryFactoryJdbcTest
{    
    @Test
    public void whenLookupJdbc()
    {
        RepositoryFactory factory = RepositoryService.getInstance().lookup(RepositoryType.JDBC);
        Repository repository = factory.newInstance("/repository-sql.xml");
        assertThat(factory, instanceOf(RepositoryFactory.class));
        assertThat(factory, instanceOf(RepositoryFactoryJdbc.class));
        assertThat(factory.getType(), is(RepositoryType.JDBC));
        assertThat(repository, instanceOf(Repository.class));
        assertThat(repository, instanceOf(RepositoryJdbc.class));
    }


}
