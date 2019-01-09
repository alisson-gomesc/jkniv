/* 
 * JKNIV, SQLegance keeping queries maintainable.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jkniv.whinstone;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.domain.flat.AuthorFlat;

public class QueryFactoryTest
{
    
    @Test
    public void whenAsQueryableNamed()
    {
        Queryable q = QueryFactory.of("query-name");
        assertThat(q, notNullValue());
        assertThat(q.getParams(), nullValue());
        assertThat(q.getMax(), is(Integer.MAX_VALUE));
        assertThat(q.getTotal(), is(-1L));
        assertThat(q.getOffset(), is(0));
    }
    
    @Test
    public void whenAsQueryableNamedWithOneParam()
    {
        Queryable q = QueryFactory.of("query-name", "id", 10L);
        assertThat(q, notNullValue());
        assertThat(q.getParams(), instanceOf(Map.class));
        assertThat(q.getMax(), is(Integer.MAX_VALUE));
        assertThat(q.getTotal(), is(-1L));
        assertThat(q.getOffset(), is(0));
    }
    
    @Test
    public void whenAsQueryableNamedWithEntity()
    {
        AuthorFlat a = new AuthorFlat("Jose", "Rio");
        Queryable q = QueryFactory.of("query-name", a);
        assertThat(q, notNullValue());
        assertThat(q.getParams(), instanceOf(AuthorFlat.class));
        assertThat(q.getMax(), is(Integer.MAX_VALUE));
        assertThat(q.getTotal(), is(-1L));
        assertThat(q.getOffset(), is(0));
    }
    
    @Test
    public void whenAsQueryableNamedWithString()
    {
        Queryable q = QueryFactory.of("query-name", "Jose");
        assertThat(q, notNullValue());
        assertThat(q.getParams(), instanceOf(String.class));
        assertThat(q.getParams().toString(), is("Jose"));
        assertThat(q.getMax(), is(Integer.MAX_VALUE));
        assertThat(q.getTotal(), is(-1L));
        assertThat(q.getOffset(), is(0));
    }
    
    @Test
    public void whenAsQueryableNamedWithArray()
    {
        String[] params =
        { "Albert Camus", "Franz Kafka", "Martin Fowler" };
        
        Queryable q = QueryFactory.ofArray("query-name", params);
        assertThat(q, notNullValue());
        assertThat(q.getParams(), instanceOf(String[].class));
        assertThat(q.getMax(), is(Integer.MAX_VALUE));
        assertThat(q.getTotal(), is(-1L));
        assertThat(q.getOffset(), is(0));
    }
    
    @Test
    public void whenAsQueryableNamedWithCollection()
    {
        List<String> params = Arrays.asList("Albert Camus", "Franz Kafka", "Martin Fowler");
        
        Queryable q = QueryFactory.of("query-name", params);
        assertThat(q, notNullValue());
        assertThat(q.getParams(), instanceOf(List.class));
        assertThat(q.getMax(), is(Integer.MAX_VALUE));
        assertThat(q.getTotal(), is(-1L));
        assertThat(q.getOffset(), is(0));
    }
    
    @Test
    public void whenAsQueryableNamedWithEntityMaxAndOffset()
    {
        AuthorFlat a = new AuthorFlat("Jose", "Rio");
        Queryable q = QueryFactory.of("query-name", a, 5, 25);
        assertThat(q, notNullValue());
        assertThat(q.getParams(), instanceOf(AuthorFlat.class));
        assertThat(q.getMax(), is(25));
        assertThat(q.getTotal(), is(-1L));
        assertThat(q.getOffset(), is(5));
    }

    @Test
    public void whenAsQueryableWithReturnType()
    {
        AuthorFlat a = new AuthorFlat("Jose", "Rio");
        Queryable q = QueryFactory.of("query-name", AuthorFlat.class, a);
        assertThat(q, notNullValue());
        assertThat(q.getName(), is("query-name"));
        assertThat(q.getParams(), instanceOf(AuthorFlat.class));
        assertThat(q.getMax(), is(Integer.MAX_VALUE));
        assertThat(q.getTotal(), is(-1L));
        assertThat(q.getOffset(), is(0));
    }
    
    // FIXME The method of(String, Object[]) is ambiguous for the type QueryFactory
    @Test @Ignore("The method of(String, Object[]) is ambiguous for the type QueryFactory")
    public void whenAsQueryableWithPaginate()
    {
        QueryFactory.of("authors-page-override", 0, 3);
        //QueryFactory.of("authors-page-override", 0, 3, "id", 1);
        //QueryFactory.of("authors-page-override", 0, 3, "id", 1);
    }
}
