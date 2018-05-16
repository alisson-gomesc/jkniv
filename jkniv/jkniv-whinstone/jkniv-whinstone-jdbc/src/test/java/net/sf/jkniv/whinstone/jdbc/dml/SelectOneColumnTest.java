/* 
 * JKNIV, whinstone one contract to access your database.
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
package net.sf.jkniv.whinstone.jdbc.dml;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.acme.domain.Author;

public class SelectOneColumnTest extends BaseJdbc
{
    
    @Autowired
    Repository repositoryDerby;
    
    @Test
    public void whenUseNativeQuerySelectOneStringColumn()
    {
        Author params = new Author();
        params.setId(4L);
        Queryable queryable = QueryFactory.newInstance("authorByIdSqlOneStringColumn", params);
        List<String> authors = repositoryDerby.list(queryable);
        assertThat(authors.size(), is(2));
        assertThat(queryable.getTotal(), is(2L));
        assertThat(authors.get(0), instanceOf(String.class));
    }

    @Test
    public void whenUseNativeQuerySelectOneNumberColumn()// TODO test with date, calendar, duration, boolean... types
    {
        Author params = new Author();
        params.setId(4L);
        Queryable queryable = QueryFactory.newInstance("authorByIdSqlOneNumberColumn", params);
        List<Long> authors = repositoryDerby.list(queryable);
        assertThat(authors.size(), is(2));
        assertThat(queryable.getTotal(), is(2L));
        assertThat(authors.get(0), instanceOf(Long.class));
    }

    @Test
    public void whenUseNativeQuerySelectOneIntegerColumn()
    {
        Author params = new Author();
        params.setId(4L);
        Queryable queryable = QueryFactory.newInstance("authorByIdSqlOneNumberColumn", params);
        List<Integer> authors = repositoryDerby.list(queryable, Integer.class);
        assertThat(authors.size(), is(2));
        assertThat(queryable.getTotal(), is(2L));
        assertThat(authors.get(0), instanceOf(Integer.class));
    }

    @Test
    public void whenUseNativeQuerySelectOneFloatColumn()
    {
        Author params = new Author();
        params.setId(4L);
        Queryable queryable = QueryFactory.newInstance("authorByIdSqlOneNumberColumn", params);
        List<Float> authors = repositoryDerby.list(queryable, Float.class);
        assertThat(authors.size(), is(2));
        assertThat(queryable.getTotal(), is(2L));
        assertThat(authors.get(0), instanceOf(Float.class));
    }

    @Test
    public void whenUseNativeQueryEmptySelectResult()
    {
        Queryable queryable = QueryFactory.newInstance("authorNoExists");
        List<String> authors = repositoryDerby.list(queryable);
        assertThat(authors.size(), is(0));
    }
    
}
