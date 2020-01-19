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
import static org.hamcrest.Matchers.instanceOf;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.cassandra.model.Vehicle;

public class CassandraCacheTest extends BaseJdbc
{
    
    @Test
    public void whenCouchDbListUsingCache()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("selectVehicleInCache", "plates", Arrays.asList("OMN7176","OMN7000","OMN7001"));
        
        List<Vehicle> vehicles = repositoryDb.list(q);

        assertThat(vehicles.size(), is(3));
        assertThat(vehicles.get(0), instanceOf(Vehicle.class));
        assertThat(q.isCached(), is(false));
        
        List<Vehicle> vehicles2 = repositoryDb.list(q);
        assertThat(q.isCached(), is(true));
        assertThat(vehicles2.size(), is(vehicles2.size()));        
    }

    @Test
    public void whenCouchDbGetUsingCache()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("selectVehicleInCache", "plates", Arrays.asList("OMN7176"));
        
        Vehicle vehicle = repositoryDb.get(q);
        assertThat(vehicle, instanceOf(Vehicle.class));
        assertThat(q.isCached(), is(false));
        assertThat(vehicle.getName(), is("bugatti"));
        assertThat(vehicle.getPlate(), is("OMN7176"));
        assertThat(vehicle.getColor(), is("white"));
        
        Vehicle vehicle2 = repositoryDb.get(q);
        assertThat(q.isCached(), is(true));
        assertThat(vehicle2.getName(), is("bugatti"));
        assertThat(vehicle2.getPlate(), is("OMN7176"));
        assertThat(vehicle2.getColor(), is("white"));
    }

    
}
