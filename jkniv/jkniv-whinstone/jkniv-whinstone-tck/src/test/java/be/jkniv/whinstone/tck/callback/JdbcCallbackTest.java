package be.jkniv.whinstone.tck.callback;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import be.jkniv.whinstone.tck.BaseJdbc;
import be.jkniv.whinstone.tck.model.StatsCallback;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;

public class JdbcCallbackTest extends BaseJdbc
{
    @Test
    public void whenCallbackForAdd()
    {
        Repository repositoryDb = getRepository();
        StatsCallback model = new StatsCallback();
        model.setName("Callback stuff");
        Queryable q = QueryFactory.of("add", model);
        int rows = repositoryDb.add(q);
        
        assertThat(rows, is(1));
        assertThat(model.getTotalAdd(), is(1));
        assertThat(model.getTotalSelect(), is(0));
        assertThat(model.getTotalUpdate(), is(0));
        assertThat(model.getTotalRemove(), is(0));
    }
    
    @Test
    public void whenCallbackForList()
    {
        Repository repositoryDb = getRepository();
        StatsCallback model = new StatsCallback();
        model.setName("Friedrich Nietzsche");
        Queryable q = QueryFactory.of("authors-by-name", model);
        List<Map<String, Object>> rows = repositoryDb.list(q);
        
        assertThat(rows.size(), greaterThan(0));
        assertThat(model.getTotalAdd(), is(0));
        assertThat(model.getTotalSelect(), is(1));
        assertThat(model.getTotalUpdate(), is(0));
        assertThat(model.getTotalRemove(), is(0));
    }

    @Test
    public void whenCallbackForUpdate()
    {
        Repository repositoryDb = getRepository();
        StatsCallback model = new StatsCallback();
        model.setName("Callback stuff");
        Queryable q = QueryFactory.of("add", model);
        int rows = repositoryDb.add(q);
        repositoryDb.update(model);

        assertThat(rows, is(1));
        assertThat(model.getTotalAdd(), is(1));
        assertThat(model.getTotalSelect(), is(0));
        assertThat(model.getTotalUpdate(), is(1));
        assertThat(model.getTotalRemove(), is(0));
    }
    
    @Test
    public void whenCallbackForRemove()
    {
        Repository repositoryDb = getRepository();
        StatsCallback model = new StatsCallback();
        model.setName("Callback stuff");
        Queryable q = QueryFactory.of("add", model);
        int rows = repositoryDb.add(q);
        
        assertThat(rows, is(1));
        assertThat(model.getTotalAdd(), is(1));
        assertThat(model.getTotalSelect(), is(0));
        assertThat(model.getTotalUpdate(), is(0));
        assertThat(model.getTotalRemove(), is(0));
        
        rows = repositoryDb.remove(model);
        assertThat(rows, is(1));
        assertThat(model.getTotalAdd(), is(1));
        assertThat(model.getTotalSelect(), is(0));
        assertThat(model.getTotalUpdate(), is(0));
        assertThat(model.getTotalRemove(), is(1));        
    }
}
