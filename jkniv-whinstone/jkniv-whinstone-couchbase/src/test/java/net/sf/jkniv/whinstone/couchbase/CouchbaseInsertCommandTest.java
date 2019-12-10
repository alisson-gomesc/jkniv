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
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.notNull;

import java.util.Map;

import org.junit.Test;

import com.couchbase.client.java.document.Document;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchbase.model.Airline;
import net.sf.jkniv.whinstone.couchbase.model.AirlineDoc;

public class CouchbaseInsertCommandTest extends BaseCouchbase
{    
    @Test
    public void whenCouchbaseAddCommandWithQueryable()
    {
        Repository repositoryDb = getRepository();
        Airline airline = new Airline();
        airline.setCallsign("TO BOARD");
        airline.setCountry("BRAZIL");
        airline.setIata("BR");
        airline.setIcao("BRA");
        airline.setName("Ali airlines");
        airline.setType("airline");
        
        Queryable q = QueryFactory.of("add", airline);
        int rows =repositoryDb.add(q);
        assertThat(rows, is(1));
        assertThat(airline.getId(), notNullValue());
    }

    @Test
    public void whenCouchbaseAddCommandWithEntity()
    {
        Repository repositoryDb = getRepository();
        Airline airline = new Airline();
        airline.setCallsign("TO BOARD");
        airline.setCountry("BRAZIL");
        airline.setIata("BR");
        airline.setIcao("BRA");
        airline.setName("Ali airlines");
        airline.setType("airline");
        
        repositoryDb.add(airline);
        assertThat(airline.getId(), notNullValue());
    }
    
}
