package net.sf.jkniv.whinstone.couchbase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.Test;

import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.couchbase.RepositoryFactoryCouchbase;
import net.sf.jkniv.whinstone.spi.RepositoryFactory;

public class RepositoryFactoryCouchDbTest extends BaseCouchbase
{
    @Test
    public void whenLookupCouchdb()
    {
        RepositoryFactory factory = RepositoryService.getInstance().lookup(RepositoryType.COUCHBASE);
        Repository repository = factory.newInstance("/repository-sql.xml");
        assertThat(factory, instanceOf(RepositoryFactory.class));
        assertThat(factory, instanceOf(RepositoryFactoryCouchbase.class));
        assertThat(factory.getType(), is(RepositoryType.COUCHBASE));
        assertThat(repository, instanceOf(Repository.class));
        assertThat(repository, instanceOf(RepositoryCouchbase.class));
        repository.close();
    }

}
