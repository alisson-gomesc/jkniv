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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.cassandra.model.Vehicle;

public class CassandraDeleteTest extends BaseJdbc
{
    @Test
    public void whenCassandraRemoveEntity()
    {
        Repository repositoryCas = getRepository();
        Vehicle v = new Vehicle();
        v.setName("Livina");
        v.setPlate("DEL7778");
        v.setColor("red");
        
        repositoryCas.add(v);
        Vehicle v2 = repositoryCas.get(v);;
        assertThat(v2.getPlate(), is(v.getPlate()));
        
        repositoryCas.remove(v);
        
        v2 = repositoryCas.get(v);;
        
        assertThat(v2, nullValue());
    }

    @Test
    public void whenCassandraRemoveWithQueryable()
    {
        Repository repositoryCas = getRepository();
        Vehicle v = new Vehicle();
        v.setName("Livina");
        v.setPlate("DEL7779");
        v.setColor("red");
        
        repositoryCas.add(v);
        Vehicle v2 = repositoryCas.get(v);
        assertThat(v2.getPlate(), is(v.getPlate()));
        
        repositoryCas.remove(QueryFactory.of("Vehicle#remove", "plate", v.getPlate()));
        
        v2 = repositoryCas.get(v);;
        
        assertThat(v2, nullValue());
    }

    
    private int select(String select)
    {
        Queryable q = QueryFactory.of(select);
        List<Map> list = getRepository().list(q);
        return list.size();
    }    
}
