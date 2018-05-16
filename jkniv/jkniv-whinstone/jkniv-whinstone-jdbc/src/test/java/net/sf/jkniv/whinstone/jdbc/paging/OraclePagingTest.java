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
package net.sf.jkniv.whinstone.jdbc.paging;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatAuthor;

import static net.sf.jkniv.whinstone.jdbc.RepositoryJdbcInstanceTest.TOTAL_BOOKS;

public class OraclePagingTest extends BaseJdbc
{
    private static final int SIZE_PAGE = 5;

    @Autowired
    Repository repositoryOra;
    
    @Test
    public void whenSimplePagingQueryWorks()
    {
        Queryable q = getQuery("listBooks", null, 0, SIZE_PAGE);
        List<FlatAuthor> list = repositoryOra.list(q);
        assertThat(list.size(), is(SIZE_PAGE));
        assertThat(q.getTotal(), is((long)TOTAL_BOOKS));
    }
    @Test
    public void whenPagingWithParamsQueryWorks()
    {
        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("author1", 3);
        params.put("author2", 4);
        
        Queryable q = getQuery("listBooksAuthor", params, 3, SIZE_PAGE);
        List<FlatAuthor> list = repositoryOra.list(q);
        assertThat(list.size(), is(SIZE_PAGE-1));
        assertThat(q.getTotal(), is(7L));
    }
    
}
