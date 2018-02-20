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
package net.sf.jkniv.whinstone.jpa2.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.List;

import org.junit.Test;

import com.acme.domain.flat.AuthorFlat;
import com.acme.domain.orm.Author;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class SelectOperationNativeTest extends BaseTest
{
    
    @Test
    public void whenUseNamedParamsForNativeQuery()
    {
        Repository repository = getRepository();
        Author params = new Author();
        params.setId(4L);
        Queryable queryable = QueryFactory.newInstance("authorByIdSqlNative", params);
        List<AuthorFlat> authors = repository.list(queryable);
        assertThat(authors.size(), is(2));
        assertThat(queryable.getTotal(), is(2L));
    }

    @Test
    public void whenUseNativeQuerySelectOneStringColumn()
    {
        Repository repository = getRepository();
        Author params = new Author();
        params.setId(4L);
        Queryable queryable = QueryFactory.newInstance("authorByIdSqlOneStringColumn", params);
        List<String> authors = repository.list(queryable);
        assertThat(authors.size(), is(2));
        assertThat(queryable.getTotal(), is(2L));
        assertThat(authors.get(0), instanceOf(String.class));
    }

    @Test
    public void whenUseNativeQuerySelectOneNumberColumn()// TODO test with date, calendar, duration, boolean... types
    {
        Repository repository = getRepository();
        Author params = new Author();
        params.setId(4L);
        Queryable queryable = QueryFactory.newInstance("authorByIdSqlOneNumberColumn", params);
        List<Long> authors = repository.list(queryable);
        assertThat(authors.size(), is(2));
        assertThat(queryable.getTotal(), is(2L));
        assertThat(authors.get(0), instanceOf(Long.class));
    }

    @Test
    public void whenUseNativeQueryEmptySelectResult()
    {
        Repository repository = getRepository();
        Queryable queryable = QueryFactory.newInstance("authorNoExists");
        List<String> authors = repository.list(queryable);
        assertThat(authors.size(), is(0));
    }
    
}
