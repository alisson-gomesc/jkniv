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
package net.sf.jkniv.whinstone.couchbase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;

import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchbase.model.Airline;
import net.sf.jkniv.whinstone.couchbase.model.Airport;

public class CouchbaseRepositorySelectTest extends BaseCouchbase
{

    @Test
    public void whenCouchbaseSelectLiteralValue()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("select-literal");
        List<Map<String, String>> list = repositoryDb.list(q);
        assertThat(list.size(), is(1));
        assertThat(q.getTotal(), greaterThan(0L));
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0).get("greeting"), is("Hello world!"));
    }

    @Test
    public void whenCouchbaseSelectWithAterix()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("select-travel");
        
        List<Map<String, ?>> list = repositoryDb.list(q);
        assertThat(list.size(), is(4));
        assertThat(q.getTotal(), is(4L));
        assertThat(list.get(0), instanceOf(Map.class));
    }

    @Test
    public void whenCouchbaseSelectWithParamName()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("select-airport-from-city", "city", "Cedar City");
        Airport airport = repositoryDb.get(q);
        assertThat(airport, instanceOf(Airport.class));
        assertThat(airport.getAirportname(), is("Cedar City Rgnl"));
        assertThat(airport.getCity(), is("Cedar City"));
        assertThat(airport.getCountry(), is("United States"));
        assertThat(airport.getFaa(), is("CDC"));
        assertThat(airport.getGeo().getAlt(), is(5622));
        assertThat(airport.getGeo().getLat(), is(37.700967D));
        assertThat(airport.getGeo().getLon(), is(-113.098847D));
        assertThat(airport.getIcao(), is("KCDC"));
        assertThat(airport.getId(), is(3824));
        assertThat(airport.getType(), is("airport"));
        assertThat(airport.getTz(), is("America/Denver"));
    }

    @Test
    public void whenCouchbaseSelectWithTwoParamName()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("select-airport-from-city", "city", "Cedar City", "type", "airport");
        Airport airport = repositoryDb.get(q);
        assertThat(airport, instanceOf(Airport.class));
        assertThat(airport.getAirportname(), is("Cedar City Rgnl"));
        assertThat(airport.getCity(), is("Cedar City"));
        assertThat(airport.getCountry(), is("United States"));
        assertThat(airport.getFaa(), is("CDC"));
        assertThat(airport.getGeo().getAlt(), is(5622));
        assertThat(airport.getGeo().getLat(), is(37.700967D));
        assertThat(airport.getGeo().getLon(), is(-113.098847D));
        assertThat(airport.getIcao(), is("KCDC"));
        assertThat(airport.getId(), is(3824));
        assertThat(airport.getType(), is("airport"));
        assertThat(airport.getTz(), is("America/Denver"));
    }
    
    @Test
    public void whenCouchbaseSelect4Airlines()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("select-4airlines");
        
        List<Airline> list = repositoryDb.list(q);
        assertThat(list.size(), is(4));
        assertThat(q.getTotal(), is(4L));
        assertThat(list.get(0), instanceOf(Airline.class));
        assertThat(list.get(0).getCallsign(), is("MILE-AIR"));
        assertThat(list.get(0).getCountry(), is("United States"));
        assertThat(list.get(0).getIata(), is("Q5"));
        assertThat(list.get(0).getIcao(), is("MLA"));
        assertThat(list.get(0).getId(), is(10));
        assertThat(list.get(0).getName(), is("40-Mile Air"));
        assertThat(list.get(0).getType(), is("airline"));

        assertThat(list.get(3).getCallsign(), nullValue());
        assertThat(list.get(3).getCountry(), is("United Kingdom"));
        assertThat(list.get(3).getIata(), nullValue());
        assertThat(list.get(3).getIcao(), is("JRB"));
        assertThat(list.get(3).getId(), is(10642));
        assertThat(list.get(3).getName(), is("Jc royal.britannica"));
        assertThat(list.get(3).getType(), is("airline"));
    }
    
    @Test
    public void whenCouchbaseSelectWithLimitAndOffset4Airlines()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("select-limit-offset-airlines", 0, 4);
        
        List<Airline> list = repositoryDb.list(q);
        assertThat(list.size(), is(4));
        assertThat(q.getTotal(), is(Long.valueOf(Statement.SUCCESS_NO_INFO)));
        assertThat(list.get(0), instanceOf(Airline.class));
        assertThat(list.get(0).getCallsign(), is("MILE-AIR"));
        assertThat(list.get(0).getCountry(), is("United States"));
        assertThat(list.get(0).getIata(), is("Q5"));
        assertThat(list.get(0).getIcao(), is("MLA"));
        assertThat(list.get(0).getId(), is(10));
        assertThat(list.get(0).getName(), is("40-Mile Air"));
        assertThat(list.get(0).getType(), is("airline"));

        assertThat(list.get(3), instanceOf(Airline.class));
        assertThat(list.get(3).getCallsign(), nullValue());
        assertThat(list.get(3).getCountry(), is("United Kingdom"));
        assertThat(list.get(3).getIata(), nullValue());
        assertThat(list.get(3).getIcao(), is("JRB"));
        assertThat(list.get(3).getId(), is(10642));
        assertThat(list.get(3).getName(), is("Jc royal.britannica"));
        assertThat(list.get(3).getType(), is("airline"));
        
        q = QueryFactory.of("select-limit-offset-airlines", 3, 4);
        list = repositoryDb.list(q);
        
        assertThat(list.size(), is(4));
        assertThat(q.getTotal(), is(Long.valueOf(Statement.SUCCESS_NO_INFO)));
        
        assertThat(list.get(0), instanceOf(Airline.class));
        assertThat(list.get(0).getCallsign(), nullValue());
        assertThat(list.get(0).getCountry(), is("United Kingdom"));
        assertThat(list.get(0).getIata(), nullValue());
        assertThat(list.get(0).getIcao(), is("JRB"));
        assertThat(list.get(0).getId(), is(10642));
        assertThat(list.get(0).getName(), is("Jc royal.britannica"));
        assertThat(list.get(0).getType(), is("airline"));
        
        assertThat(list.get(3), instanceOf(Airline.class));
        assertThat(list.get(3).getCallsign(), is("ACE AIR"));
        assertThat(list.get(3).getCountry(), is("United States"));
        assertThat(list.get(3).getIata(), is("KO"));
        assertThat(list.get(3).getIcao(), is("AER"));
        assertThat(list.get(3).getId(), is(109));
        assertThat(list.get(3).getName(), is("Alaska Central Express"));
        assertThat(list.get(3).getType(), is("airline"));
    }
}
