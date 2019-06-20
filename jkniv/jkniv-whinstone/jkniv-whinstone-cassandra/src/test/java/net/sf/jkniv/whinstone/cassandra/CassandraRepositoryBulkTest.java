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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.cassandra.model.Vehicle;

public class CassandraRepositoryBulkTest extends BaseJdbc
{

    // add operations
    @Test
    public void whenAddBulkCollectionOfPojo() throws SQLException
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.add(QueryFactory.of("Vehicle#add", getCollectionOfPojo()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }


    @Test
    public void whenAddBulkCollectionOfMap()
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.add(QueryFactory.of("Vehicle#add", getCollectionOfMap()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }

    @Test
    public void whenAddBulkCollectionOfArray()
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.add(QueryFactory.of("Vehicle#add", getCollectionOfArray()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }

    @Test
    public void whenAddBulkArrayOfMap()
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.add(QueryFactory.ofArray("Vehicle#add", getArrayOfMap()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }

    @Test
    public void whenAddBulkArrayOfPojo()
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.add(QueryFactory.ofArray("Vehicle#add", getArrayOfPojo()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }

    // update operations
    @Test
    public void whenUpdateBulkCollectionOfPojo() throws SQLException
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.update(QueryFactory.of("Vehicle#add", getCollectionOfPojo()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }


    @Test
    public void whenUpdateBulkCollectionOfMap()
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.update(QueryFactory.of("Vehicle#add", getCollectionOfMap()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }

    @Test
    public void whenUpdateBulkCollectionOfArray()
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.update(QueryFactory.of("Vehicle#add", getCollectionOfArray()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }

    @Test
    public void whenUpdateBulkArrayOfMap()
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.update(QueryFactory.ofArray("Vehicle#add", getArrayOfMap()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }

    @Test
    public void whenUpdateBulkArrayOfPojo()
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.update(QueryFactory.ofArray("Vehicle#add", getArrayOfPojo()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }

    // remove operations
    @Test
    public void whenRemoveBulkCollectionOfPojo() throws SQLException
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.remove(QueryFactory.of("Vehicle#add", getCollectionOfPojo()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }


    @Test
    public void whenRemoveBulkCollectionOfMap()
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.remove(QueryFactory.of("Vehicle#add", getCollectionOfMap()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }

    @Test
    public void whenRemoveBulkCollectionOfArray()
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.remove(QueryFactory.of("Vehicle#add", getCollectionOfArray()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }

    @Test
    public void whenRemoveBulkArrayOfMap()
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.remove(QueryFactory.ofArray("Vehicle#add", getArrayOfMap()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }

    @Test
    public void whenRemoveBulkArrayOfPojo()
    {
        Repository repositoryCas = getRepository();
        int rows = repositoryCas.remove(QueryFactory.ofArray("Vehicle#add", getArrayOfPojo()));
        assertThat("Eventually consistent cannot reach how many rows was affected", rows, equalTo(-10));
    }

    private List<Vehicle> getCollectionOfPojo()
    {
        List<Vehicle> vehicles= new ArrayList<Vehicle>();
        vehicles.add(new Vehicle("NEW1001","A"));
        vehicles.add(new Vehicle("NEW1002","B"));
        vehicles.add(new Vehicle("NEW1003","C"));
        vehicles.add(new Vehicle("NEW1004","D"));
        vehicles.add(new Vehicle("NEW1005","E"));
        return vehicles;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<Map> getCollectionOfMap()
    {
        List<Map> vehicles= new ArrayList<Map>();
        Map m1 = new HashMap(), m2 = new HashMap(), m3 = new HashMap(), m4 = new HashMap(), m5 = new HashMap();
        m1.put("plate","NEW2001"); m1.put("name","A");
        m2.put("plate","NEW2002"); m1.put("name","A");
        m3.put("plate","NEW2003"); m1.put("name","A");
        m4.put("plate","NEW2004"); m1.put("name","A");
        m5.put("plate","NEW2005"); m1.put("name","A");
        vehicles.add(m1);
        vehicles.add(m2);
        vehicles.add(m3);
        vehicles.add(m4);
        vehicles.add(m5);
        return vehicles;
    }
    
    private List<Object[]> getCollectionOfArray()
    {
        List<Object[]> vehicles = new ArrayList<Object[]>();
        vehicles.add(new Object[]{"NEW3001","A"});
        vehicles.add(new Object[]{"NEW3002","A"});
        vehicles.add(new Object[]{"NEW3003","A"});
        vehicles.add(new Object[]{"NEW3004","A"});
        vehicles.add(new Object[]{"NEW3005","A"});
        return vehicles;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object[] getArrayOfMap()
    {
        Object[] vehicles = new Object[5];
        Map m1 = new HashMap(), m2 = new HashMap(), m3 = new HashMap(), m4 = new HashMap(), m5 = new HashMap();
        m1.put("plate","NEW4001"); m1.put("name","A");
        m2.put("plate","NEW4002"); m1.put("name","A");
        m3.put("plate","NEW4003"); m1.put("name","A");
        m4.put("plate","NEW4004"); m1.put("name","A");
        m5.put("plate","NEW4005"); m1.put("name","A");
        vehicles[0] = m1;
        vehicles[1] = m2;
        vehicles[2] = m3;
        vehicles[3] = m4;
        vehicles[4] = m5;
        return vehicles;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object[] getArrayOfPojo()
    {
        Object[] vehicles = new Object[5];
        Map m1 = new HashMap(), m2 = new HashMap(), m3 = new HashMap(), m4 = new HashMap(), m5 = new HashMap();
        m1.put("plate","NEW2001"); m1.put("name","A");
        m2.put("plate","NEW2002"); m1.put("name","A");
        m3.put("plate","NEW2003"); m1.put("name","A");
        m4.put("plate","NEW2004"); m1.put("name","A");
        m5.put("plate","NEW2005"); m1.put("name","A");
        vehicles[0] = new Vehicle("NEW5001","A");
        vehicles[1] = new Vehicle("NEW5002","B");
        vehicles[2] = new Vehicle("NEW5003","C");
        vehicles[3] = new Vehicle("NEW5004","D");
        vehicles[4] = new Vehicle("NEW5005","E");
        return vehicles;
    }
}
