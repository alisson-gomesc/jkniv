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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.cassandra.model.Vehicle;
import net.sf.jkniv.whinstone.cassandra.result.CustomResultRow;

public class CassandraRepositoryUpdateTest extends BaseJdbc
{
    @Autowired
    Repository repository;
    // (my_key,evt_date,object_id, lat, lng, warn)
    

    @Test
    public void whenCassandraUpsert()
    {
        int initialSize = select("simpleSelect");
        Object[]   params = { "CAR001", 20.000001F, -88.000001F, 2, "k002", new Date() };        
        Repository repositoryCas = getRepository();
        Queryable q = QueryFactory.ofArray("simpleUpsert",params);
        repositoryCas.update(q);
        int newSize = select("simpleSelect");

        assertThat(initialSize+1, is(newSize));
    }
    
    @Test
    public void whenCassandraUpdateEntity()
    {
        Repository repositoryCas = getRepository();
        Vehicle v = new Vehicle();
        v.setPlate("UUU9999");
        v.setName("Livina");
        v.setColor("green");
        repositoryCas.remove(v);

        repositoryCas.add(v);
        v.setColor("blue");
        repositoryCas.update(v);
        
        Vehicle v2 = repositoryCas.get(v);
        assertThat(v.getPlate(), is(v2.getPlate()));
        assertThat(v.getColor(), is(v2.getColor()));
    }
    

    private int select(String select)
    {
        Queryable q = QueryFactory.of(select);
        List<Map> list = getRepository().list(q);
        return list.size();
    }

}
