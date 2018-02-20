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
package net.sf.jkniv.whinstone.jdbc;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;

/**
 * @author Alisson Gomes
 */
@SuppressWarnings("rawtypes")
public class ResultSetParserTest extends BaseJdbc
{
    @Autowired
    Repository repositoryDerby;
    
    @Test
    public void whenSelectReturnMapInterface()
    {
        Queryable q = QueryFactory.newInstance("listWithMapInterface");
        List<Map<String, Object>> list = repositoryDerby.list(q);
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(HashMap.class));
    }

    @Test
    public void whenSelectReturnHashMap()
    {
        Queryable q = QueryFactory.newInstance("listWithMapInterface");
        List<HashMap> list = repositoryDerby.list(q, HashMap.class);
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(HashMap.class));
    }

    @Test
    public void whenSelectReturnProperties()
    {
        Queryable q = QueryFactory.newInstance("listWithMapInterface");
        List<Properties> list = repositoryDerby.list(q, Properties.class);
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(Properties.class));
    }

    @Test
    public void whenSelectReturnLinkedHashMap()
    {
        Queryable q = QueryFactory.newInstance("listWithMapInterface");
        List<LinkedHashMap> list = repositoryDerby.list(q, LinkedHashMap.class);
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(LinkedHashMap.class));
    }

    @Test
    public void whenSelectReturnHashtable()
    {
        Queryable q = QueryFactory.newInstance("listWithMapInterface");
        List<Hashtable> list = repositoryDerby.list(q, Hashtable.class);
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(Hashtable.class));
    }

    @Test
    public void whenSelectReturnIdentityHashMap()
    {
        Queryable q = QueryFactory.newInstance("listWithMapInterface");
        List<IdentityHashMap> list = repositoryDerby.list(q, IdentityHashMap.class);
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(IdentityHashMap.class));
    }
    
    @Test
    public void whenSelectReturnConcurrentHashMap()
    {
        Queryable q = QueryFactory.newInstance("listWithMapInterface");
        List<ConcurrentHashMap> list = repositoryDerby.list(q, ConcurrentHashMap.class);
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(ConcurrentHashMap.class));
    }
    
    @Test
    public void whenSelectWithCustomResultSetParser()
    {
        Queryable q = QueryFactory.newInstance("listWithMapInterface");
        List<HashMap<String, Object>> list = repositoryDerby.list(q, new CustomResultRow());
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(HashMap.class));
        assertThat((Boolean)list.get(0).get("JUNIT"), is(Boolean.TRUE));
    }


}
