package net.sf.jkniv.whinstone.jdbc.identity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.acme.domain.Foo;


public class OracleAutoKeyTest extends BaseJdbc
{
    @Autowired
    Repository repositoryOra;
    
    @Test
    public void whenGenerateKeyWithOracleSequence()
    {
        Foo foo1 = new Foo(),foo2 = new Foo();
        foo1.setName("Foo fights");
        foo2.setName("New Foo fights");
        Queryable queryable = QueryFactory.newInstance("test-autokey-oracle-case-sequence", foo1);
        int affected = repositoryOra.add(queryable);
        assertThat(affected, is(1));
        assertThat(foo1.getId(), notNullValue());
        assertThat(foo1.getId().longValue(), greaterThan(0L));
        Queryable queryable2 = QueryFactory.newInstance("test-autokey-oracle-case-sequence", foo2);
        affected = repositoryOra.add(queryable2);
        assertThat(affected, is(1));
        assertThat(foo2.getId(), notNullValue());
        assertThat(foo2.getId().longValue(), greaterThan(0L));
        assertThat(foo2.getId().longValue(), is(foo1.getId()+1));
    }
}
