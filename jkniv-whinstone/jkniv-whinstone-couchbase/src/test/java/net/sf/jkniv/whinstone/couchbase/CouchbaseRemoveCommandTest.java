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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchbase.model.Airline;

public class CouchbaseRemoveCommandTest extends BaseCouchbase
{    
    @Test
    public void whenCouchbaseRemoveDocumentNotExists()
    {
        Repository repository = getRepository();
        Queryable q = QueryFactory.of("delete-airline-by-id", "WHIN-1575933006209");
        int rows = repository.remove(q);
        assertThat(rows, is(0));
    }
    
    @Test
    public void whenCouchbaseRemoveCommandWithQueryable()
    {
        Repository repository = getRepository();
        Airline airline = new Airline();
        airline.setCallsign("callsing");
        airline.setCountry("country");
        airline.setIata("iata");
        airline.setIcao("icao");
        airline.setName("Airline name");
        int rows = repository.add(airline);
        assertThat(rows, is(1));
        assertThat(airline.getId(), notNullValue());
        
        Airline airlineDb = repository.get(Airline.class, airline);
        assertThat(airlineDb.getId(), is(airline.getId()));
        
        Queryable q = QueryFactory.of("delete-airline-by-id", airline.getId());
        
        rows = repository.remove(q);
        assertThat(rows, is(1));
        
    }


}
