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
package net.sf.jkniv.whinstone;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.junit.Test;

import net.sf.jkniv.whinstone.QueryName;
import net.sf.jkniv.whinstone.Queryable;

@SuppressWarnings("rawtypes")
public class QueryableTest
{
    
    @Test
    public void whenParamsIsArray()
    {
        Queryable array = new QueryName("x", new String[] { "A", "B" });
        assertThat(array.isTypeOfArray(), is(true));
        assertThat(array.isTypeOfCollection(), is(false));
        assertThat(array.isTypeOfMap(), is(false));
        
        Queryable string = new QueryName("x", "A");
        assertThat(string.isTypeOfArray(), is(false));
        assertThat(string.isTypeOfCollection(), is(false));
        assertThat(string.isTypeOfMap(), is(false));
        
    }
    
    @Test
    public void whenParamsIsCollection()
    {
        Queryable arrayList = new QueryName("x", new ArrayList());
        assertThat(arrayList.isTypeOfArray(), is(false));
        assertThat(arrayList.isTypeOfCollection(), is(true));
        assertThat(arrayList.isTypeOfMap(), is(false));

        Queryable vector = new QueryName("x", new Vector());
        assertThat(vector.isTypeOfArray(), is(false));
        assertThat(vector.isTypeOfCollection(), is(true));
        assertThat(vector.isTypeOfMap(), is(false));

        Queryable hashset = new QueryName("x", new HashSet());
        assertThat(hashset.isTypeOfArray(), is(false));
        assertThat(hashset.isTypeOfCollection(), is(true));
        assertThat(hashset.isTypeOfMap(), is(false));

        Queryable treeSet = new QueryName("x", new TreeSet());
        assertThat(treeSet.isTypeOfArray(), is(false));
        assertThat(treeSet.isTypeOfCollection(), is(true));
        assertThat(treeSet.isTypeOfMap(), is(false));
    }
    
    @Test
    public void whenParamsIsMap()
    {
        Queryable hashMap = new QueryName("x", new HashMap());
        assertThat(hashMap.isTypeOfArray(), is(false));
        assertThat(hashMap.isTypeOfCollection(), is(false));
        assertThat(hashMap.isTypeOfMap(), is(true));

        Queryable treeMap = new QueryName("x", new TreeMap());
        assertThat(treeMap.isTypeOfArray(), is(false));
        assertThat(treeMap.isTypeOfCollection(), is(false));
        assertThat(treeMap.isTypeOfMap(), is(true));

    }
    
    
    @Test
    public void whenBulkArrayCollection()
    {
        List<Object[]> vehicles = new ArrayList<Object[]>();
        vehicles.add(new Object[]{"NEW2001","A"});
        vehicles.add(new Object[]{"NEW2002","A"});
        vehicles.add(new Object[]{"NEW2003","A"});
        vehicles.add(new Object[]{"NEW2004","A"});
        vehicles.add(new Object[]{"NEW2005","A"});
        Queryable treeMap = QueryFactory.of("Vehicle#add", vehicles);
        assertThat(treeMap.isTypeOfArray(), is(false));
        assertThat(treeMap.isTypeOfMap(), is(false));
        assertThat(treeMap.isTypeOfCollection(), is(true));
        assertThat(treeMap.isTypeOfCollectionArray(), is(true));

    }

}
