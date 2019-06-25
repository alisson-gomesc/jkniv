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
package net.sf.jkniv.whinstone.cassandra;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.cassandra.model.ColorFlat;
import net.sf.jkniv.whinstone.cassandra.model.Vehicle;

public class CassandraPagingTest extends BaseJdbc
{
    private static final int SIZE_PAGE = 10, LOAD = 100;
    private static final long SUCCESS_NO_INFO = Long.valueOf(Statement.SUCCESS_NO_INFO);

    @BeforeClass
    public static void setUp()
    {
        Repository repositoryCas = getRepository();
        List<ColorFlat> colors = new ArrayList<ColorFlat>();
        for(int i=0; i<LOAD; i++)
            colors.add(new ColorFlat("magenta", "Color"+i));
        
        repositoryCas.add(QueryFactory.of("ColorFlat#add", colors));
    }

    @AfterClass
    public static void tearDown()
    {
        Repository repositoryCas = getRepository();
        List<ColorFlat> colors = new ArrayList<ColorFlat>();
        for(int i=0; i<LOAD; i++)
            colors.add(new ColorFlat("magenta", "Color"+i));
        
        repositoryCas.remove(QueryFactory.of("ColorFlat#remove", colors));
    }
    
    @Test
    public void whenSimplePagingQueryWorks()
    {
        Repository repositoryCas = getRepository();
        Queryable q = QueryFactory.of("all-vehicles", 0, SIZE_PAGE);
        List<Vehicle> list = repositoryCas.list(q);
        assertThat(list.size(), is(SIZE_PAGE));
        assertThat(q.getTotal(), is(SUCCESS_NO_INFO));
        // FIXME paging cassandra PageState assertThat(q.getBookmark(), notNullValue());
    }
    
    @Test
    public void whenPagingWithParamsQueryWorks()
    {
        Repository repositoryCas = getRepository();
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "magenta");
        
        Queryable q = QueryFactory.of("colors-by-name", params,0, SIZE_PAGE);
        List<Vehicle> list = repositoryCas.list(q);
        assertThat(list.size(), is(SIZE_PAGE));
        assertThat(q.getTotal(), is(SUCCESS_NO_INFO));
        // FIXME paging cassandra PageState assertThat(q.getBookmark(), notNullValue());
    }

    @Test
    public void whenPagingResultWithNoRecords()
    {
        Repository repositoryCas = getRepository();
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "no one");
        Queryable q = QueryFactory.of("colors-by-name", params, 0, SIZE_PAGE);
        List<Vehicle> list = repositoryCas.list(q);
        assertThat(list.size(), is(0));
        assertThat(q.getTotal(), is(SUCCESS_NO_INFO));
        // FIXME paging cassandra PageState assertThat(q.getBookmark(), notNullValue());
    }

}
