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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

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

public class CouchbaseGetCommandTest extends BaseCouchbase
{    
    @Test
    public void whenCouchbaseSelectByKey()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("get", "airline_1191");
        
        Map<String, Object> map = repositoryDb.get(q);
        assertThat(map, instanceOf(Map.class));
        assertThat(map.get("callsign"), is("REUNION"));
        assertThat(map.get("country"), is("France"));
        assertThat(map.get("iata"), is("UU"));
        assertThat(map.get("icao"), is("REU"));
        assertThat(map.get("id"), is(1191));
        assertThat(map.get("name"), is("Air Austral"));
        assertThat(map.get("type"), is("airline"));
    }
    
    @Test
    public void whenCouchbaseSelectByKeyOverloadType()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("get", "airline_1191");
        
        Airline airline = repositoryDb.get(q, Airline.class);
        assertThat(airline, instanceOf(Airline.class));
        assertThat(airline.getCallsign(), is("REUNION"));
        assertThat(airline.getCountry(), is("France"));
        assertThat(airline.getIata(), is("UU"));
        assertThat(airline.getIcao(), is("REU"));
        assertThat(airline.getId(), is("1191"));
        assertThat(airline.getName(), is("Air Austral"));
        assertThat(airline.getType(), is("airline"));
    }
    
    @Test
    public void whenCouchbaseSelectByKeyWithDefaultTypeOfCouchbase()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("get", "airline_1191");
        
        Document<JsonObject> airline = repositoryDb.get(q, JsonDocument.class);
        assertThat(airline, instanceOf(Document.class));
        assertThat(airline.content(), instanceOf(JsonObject.class));
        assertThat(airline.cas(), is(1575521952519225344L));
        assertThat(airline.expiry(), is(0));
        assertThat(airline.id(), is("airline_1191"));
        assertThat(airline.content().get("callsign"), is("REUNION"));
        assertThat(airline.content().get("country"), is("France"));
        assertThat(airline.content().get("iata"), is("UU"));
        assertThat(airline.content().get("icao"), is("REU"));
        assertThat(airline.content().get("id"), is(1191));
        assertThat(airline.content().get("name"), is("Air Austral"));
        assertThat(airline.content().get("type"), is("airline"));
    }
    
    
    @Test
    public void whenCouchbaseSelectByKeyExtendDocument()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("get", "airline_1191");
        
        AirlineDoc airline = repositoryDb.get(q, AirlineDoc.class);
        assertThat(airline, instanceOf(AirlineDoc.class));
        // FIXME AirlineDoc must have content
//        assertThat(airline.content().getCallsign(), is("REUNION"));
//        assertThat(airline.content().getCountry(), is("France"));
//        assertThat(airline.content().getIata(), is("UU"));
//        assertThat(airline.content().getIcao(), is("REU"));
//        assertThat(airline.content().getId(), is(1191));
//        assertThat(airline.content().getName(), is("Air Austral"));
//        assertThat(airline.content().getType(), is("airline"));
        assertThat(airline.getCallsign(), is("REUNION"));
        assertThat(airline.getCountry(), is("France"));
        assertThat(airline.getIata(), is("UU"));
        assertThat(airline.getIcao(), is("REU"));
        assertThat(airline.getId(), is("1191"));
        assertThat(airline.getName(), is("Air Austral"));
        assertThat(airline.getType(), is("airline"));
    }
    
}
