package net.sf.jkniv.whinstone.jpa2;

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
import net.sf.jkniv.whinstone.spi.RepositoryFactory;

public class RepositoryFactoryJpaTest
{

    @Test
    public void whenLookupJpa()
    {
        RepositoryFactory factory = RepositoryService.getInstance().lookup(RepositoryType.JPA);
        Repository repository = factory.newInstance("/repository-sql.xml");
        assertThat(factory, instanceOf(RepositoryFactory.class));
        assertThat(factory, instanceOf(RepositoryFactoryJpa.class));
        assertThat(factory.getType(), is(RepositoryType.JPA));
        assertThat(repository, instanceOf(Repository.class));
        assertThat(repository, instanceOf(RepositoryJpa.class));
    }

}
