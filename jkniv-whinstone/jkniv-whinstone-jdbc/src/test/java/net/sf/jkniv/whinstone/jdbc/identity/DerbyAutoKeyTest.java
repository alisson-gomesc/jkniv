package net.sf.jkniv.whinstone.jdbc.identity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.domain.acme.Foo;


public class DerbyAutoKeyTest extends BaseJdbc
{
    @Autowired
    Repository repositoryDerby;
    
    @Test
    public void whenGenerateKeyWithDerbyIdentity()
    {
        Foo foo1 = new Foo(),foo2 = new Foo();
        foo1.setName("Foo fights");
        foo2.setName("New Foo fights");
        Queryable queryable = QueryFactory.of("test-autokey-derby-case-identity", foo1);
        int affected = repositoryDerby.add(queryable);
        assertThat(affected, is(1));
        assertThat(foo1.getId(), notNullValue());
        assertThat(foo1.getId().longValue(), greaterThan(0L));
        assertThat(foo1.getId().longValue(), greaterThan(0L));
        
        Queryable queryable2 = QueryFactory.of("test-autokey-derby-case-identity", foo2);
        affected = repositoryDerby.add(queryable2);
        assertThat(affected, is(1));
        assertThat(foo2.getId(), notNullValue());
        assertThat(foo2.getId().longValue(), greaterThan(0L));
        assertThat(foo2.getId().longValue(), is(foo1.getId()+1));
    }
}
