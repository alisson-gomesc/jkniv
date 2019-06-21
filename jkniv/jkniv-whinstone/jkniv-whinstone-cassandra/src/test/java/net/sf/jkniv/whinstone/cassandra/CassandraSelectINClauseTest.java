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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.cassandra.result.CustomResultRow;

@SuppressWarnings("rawtypes")
public class CassandraSelectINClauseTest extends BaseJdbc
{
    //@Autowired
    //Repository repository;
    Object[]   params =
    { "k001", new Date(), "CAR001", 20.000001F, -88.000001F, 2 };


    @Test
    public void whenSelectRecordsUsingInClauseWithArray()
    {
        Repository repository = getRepository();
        String[] params = { "OMN7000", "OMN7001" };
        Queryable q = QueryFactory.ofArray("getVehiclesUsingIN", params);
        List<Map> list = repository.list(q);
        assertThat(list.size(), is(2));
    }

    @Test
    public void whenSelectVehiclesUsingINAnotherParam()
    {
        Repository repository = getRepository();
        String[] params = { "OMN7000", "OMN7001" };
        Queryable q = QueryFactory.of("getVehiclesUsingINAnotherParam", "plates", params, "name", "fusca");
        List<Map> list = repository.list(q);
        assertThat(list.size(), is(1));
    }

    @Test
    public void whenSelectVehiclesUsingINAnotherParamAfterIN()
    {
        Repository repository = getRepository();
        String[] params = { "OMN7000", "OMN7001" };
        Queryable q = QueryFactory.of("getVehiclesUsingINAnotherParamAfterIN", "plates", params, "name", "fusca");
        List<Map> list = repository.list(q);
        assertThat(list.size(), is(1));
    }

    @Test
    public void whenCassandraListUsingClauseIN()
    {
        Repository repositoryCas = getRepository();
        Queryable q = QueryFactory.of("selectClauseIN", "names", Arrays.asList("blue","white"));
        
        List<Map> list = repositoryCas.list(q);
        assertThat(list.size(), is(6));
        assertThat(list.get(0), instanceOf(Map.class));
    }
    
    @Test
    public void whenCassandraListUsingClauseINOneValue()
    {
        Repository repositoryCas = getRepository();
        Queryable q = QueryFactory.of("selectClauseIN", "names", Arrays.asList("blue"));
        
        List<Map> list = repositoryCas.list(q);
        assertThat(list.size(), is(3));
        assertThat(list.get(0), instanceOf(Map.class));
    }

    @Test
    public void whenCassandraListUsingClauseINZeroValue()
    {
        Repository repositoryCas = getRepository();
        Queryable q = QueryFactory.of("selectClauseIN", "names", Arrays.asList("yellow"));
        
        List<Map> list = repositoryCas.list(q);
        assertThat(list.size(), is(0));
    }
}
