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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.whinstone.cassandra.result.CustomResultRow;

//@Ignore("RepositoryJdbc doesn't supports Cassandra driver")
public class CassandraRepositoryTest extends BaseJdbc
{
    @Autowired
    Repository repository;
    Object[]   params =
    { "k001", new Date(), "CAR001", 20.000001F, -88.000001F, 2 };
    // (my_key,evt_date,object_id, lat, lng, warn)
    
    @Test
    public void whenCassandraListDefault()
    {
        Repository repositoryCas = getRepository();
        Queryable q = getQuery("simpleSelect");
        
        List<Map<String,Object>> list = repositoryCas.list(q);
        assertThat(list.size(), greaterThan(0));
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(HashMap.class));
    }
    
    @Test
    public void whenCassandraListSpecificReturnType()
    {
        Repository repositoryCas = getRepository();
        Queryable q = getQuery("simpleSelect");
        
        List<TreeMap> list = repositoryCas.list(q, TreeMap.class);
        assertThat(list.size(), greaterThan(0));
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(TreeMap.class));
    }
    
    @Test
    public void whenCassandraListSpecificResultRow()
    {
        Repository repositoryCas = getRepository();
        Queryable q = getQuery("simpleSelect");
        
        List<Map> list = repositoryCas.list(q, new CustomResultRow<Map>());
        assertThat(list.size(), greaterThan(0));
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(HashMap.class));
        assertThat((Boolean)list.get(0).get("JUNIT"), is(Boolean.TRUE));
        assertThat(list.get(0).get("0"), instanceOf(String.class));
    }
    
    
    
    @Test
    public void whenCassandraRepositoryExecuteRemoveListAddCommands()
    {
 
        //remove();
        //assertThat(add(), is(true));
        //assertThat(select(), is(true));
        //assertThat(remove(), is(true));
        
        //assertThat(list.size(), greaterThan(0));
    }

    @Test
    public void whenCassandraAdd()
    {
        int initialSize = select();
        
        params[3] = new Float( ((Float)params[3]).floatValue()+0.000001F);  
        params[4] = new Float( ((Float)params[4]).floatValue()+0.000001F); 
        Repository repositoryCas = getRepository();
        Queryable q = getQuery("simpleInsert",params);
        repositoryCas.add(q);
        int newSize = select();

        assertThat(initialSize+1, is(newSize));
    }
    
    private boolean remove()
    {
        Queryable q = getQuery("removeAll");
        repository = getRepository();
        int rows = repository.remove(q);
        return (rows > 0 ? true : false);
    }
    
    private boolean add()
    {
        Queryable q = getQuery("simpleInsert", params);
        int rows = repository.add(q);
        return (rows > 0 ? true : false);
    }
    
    private int select()
    {
        Queryable q = getQuery("simpleSelect");
        List<Map> list = getRepository().list(q);
        return list.size();
    }
    
}
