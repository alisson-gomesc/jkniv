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
package net.sf.jkniv.whinstone.couchdb;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;

@SuppressWarnings("rawtypes")
public class CouchDbCacheTest extends BaseJdbc
{
    
    @Test
    public void whenCouchDbListUsingCache()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("authorBRInCache","natio","BR");
        
        List<Map> list1 = repositoryDb.list(q);
        assertThat(list1.size(), greaterThan(0));
        assertThat(list1.get(0), instanceOf(Map.class));
        assertThat(q.isCached(), is(false));
        
        List<Map> list2 = repositoryDb.list(q);
        assertThat(q.isCached(), is(true));
        assertThat(list1.size(), is(list2.size()));        
    }

    @Test
    public void whenCouchDbGetUsingCache()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("authorBRInCache","natio","GB");
        getRepository().list(q);
        
        Map map1 = repositoryDb.get(q);
        assertThat(map1, instanceOf(Map.class));
        assertThat(q.isCached(), is(false));
        assertThat(map1.get("name").toString(), is("Martin Fowler"));
        
        Map map2 = repositoryDb.get(q);
        assertThat(q.isCached(), is(true));
        assertThat(map1.size(), is(map2.size()));
        assertThat(map1.get("name").toString(), is("Martin Fowler"));
    }

}
