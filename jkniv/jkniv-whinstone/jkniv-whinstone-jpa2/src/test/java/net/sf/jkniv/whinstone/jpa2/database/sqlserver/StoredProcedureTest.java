package net.sf.jkniv.whinstone.jpa2.database.sqlserver;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class StoredProcedureTest extends BaseTest
{
    @Test @Ignore("TODO implements stored with JPA")
    public void whenCallSqlServerFunctionScalarValue()
    {
        Repository repository = getRepositorySqlServer();
        Queryable queryable = QueryFactory.newInstance("comunicatePrice");
        List<Object[]> config = repository.list(queryable);
        assertThat(config.size(), greaterThan(0));
        assertThat(config.get(0), instanceOf(Object[].class));
    }
    
}
