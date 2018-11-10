package net.sf.jkniv.whinstone.couchdb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.Test;

import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.spi.RepositoryFactory;

public class RepositoryFactoryCouchDbTest extends BaseJdbc
{
    @Test
    public void whenLookupCouchdb()
    {
        RepositoryFactory factory = RepositoryService.getInstance().lookup(RepositoryType.COUCHDB);
        Repository repository = factory.newInstance("/repository-sql.xml");
        assertThat(factory, instanceOf(RepositoryFactory.class));
        assertThat(factory, instanceOf(RepositoryFactoryCouchDb.class));
        assertThat(factory.getType(), is(RepositoryType.COUCHDB));
        assertThat(repository, instanceOf(Repository.class));
        assertThat(repository, instanceOf(RepositoryCouchDb.class));
    }

}
