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
import static org.hamcrest.Matchers.instanceOf;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.cassandra.model.Vehicle;
import net.sf.jkniv.whinstone.cassandra.result.CustomResultRow;

@SuppressWarnings("rawtypes")
public class CassandraSelectGetTest extends BaseJdbc
{
    private static final String PLATE = "OMN7176";
    private static final String COLOR = "white";
    
    @Test
    public void whenRepositoryOverloadReturnType()
    {
        Repository repositoryCas = getRepository();
        Queryable q = QueryFactory.of("selectVehicle", "plate", PLATE);
        
        Vehicle v = repositoryCas.get(q);
        assertThat(v, instanceOf(Vehicle.class));
        assertThat(v.getPlate(), is(PLATE));
        assertThat(v.getColor(), is(COLOR));

        Map<String, Object> map  = repositoryCas.get(q, Map.class);
        assertThat(map, instanceOf(Map.class));
        assertThat(map.get("plate").toString(), is(PLATE));
        assertThat(map.get("color").toString(), is(COLOR));
        assertThat(map, instanceOf(Map.class));
        assertThat(map, instanceOf(HashMap.class));
    }
    
    @Test
    public void whenGetWithParamOfMapReturnEntity()
    {
        Repository repositoryDb = getRepository();
        Vehicle vehicle = new Vehicle("OMN7176");
        Vehicle ret = repositoryDb.get(Vehicle.class, vehicle);
        assertThat(ret, notNullValue());
        assertThat(ret, instanceOf(Vehicle.class));
        assertThat(ret.getName(), is("bugatti"));
        assertThat(ret.getPlate(), is("OMN7176"));
        assertThat(ret.getColor(), is("white"));
    }

    @Test
    public void whenCassandraListSpecificResultRow()
    {
        Repository repositoryCas = getRepository();
        Queryable q = QueryFactory.of("selectVehicle", new Vehicle("OMN7176"));
        
        Map map  = repositoryCas.get(q, new CustomResultRow<Map>());
        assertThat(map, instanceOf(Map.class));
        assertThat(map, instanceOf(HashMap.class));
        assertThat((Boolean)map.get("JUNIT"), is(Boolean.TRUE));
        assertThat(map.get("0"), instanceOf(String.class));
    }

}
