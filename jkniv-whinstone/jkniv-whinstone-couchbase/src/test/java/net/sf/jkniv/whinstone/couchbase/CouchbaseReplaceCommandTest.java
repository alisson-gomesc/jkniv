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
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.notNull;

import java.util.Calendar;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.couchbase.client.java.document.Document;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.document.json.JsonValue;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchbase.model.Airline;
import net.sf.jkniv.whinstone.couchbase.model.AirlineDoc;

public class CouchbaseReplaceCommandTest extends BaseCouchbase
{    
    @Test
    public void whenCouchbaseReplaceDocumentNotExists()
    {
        Repository repository = getRepository();
        Queryable q = QueryFactory.of("replace", "1575933006209");
        int rows = repository.update(q);
        assertThat(rows, is(0));
    }
    
    @Test
    public void whenCouchbaseReplaceCommandWithEntity()
    {
        Repository repository = getRepository();
        //RawJsonDocument doc = RawJsonDocument.create("airline_4547");
        //Queryable q = QueryFactory.of("get", doc);
        AirlineDoc airline = repository.get(AirlineDoc.class, "airline_4547");
        assertThat(airline, instanceOf(AirlineDoc.class));
        assertThat(airline.getCallsign(), is("SOUTHWEST"));
        assertThat(airline.getCountry(), is("United States"));
        assertThat(airline.getIata(), is("WN"));
        assertThat(airline.getIcao(), is("SWA"));
        assertThat(airline.getId(), is(4547));
        assertThat(airline.getName(), startsWith("Southwest Airlines"));
        assertThat(airline.getType(), is("airline"));
        
        String newName = "Southwest Airlines " + Calendar.getInstance().get(Calendar.MINUTE);
        airline.setName(newName);
        
        int rows = repository.update(QueryFactory.of("replace", airline));
        assertThat(rows, is(1));
        AirlineDoc airlineUpdated = repository.get(AirlineDoc.class, "airline_4547");

        assertThat(airlineUpdated.getName(), is(newName));
        //assertThat(airlineUpdated.cas(), notNullValue());
        //assertThat(airline.cas(), notNullValue());
        //assertThat(airlineUpdated.cas(), not(airline.cas()));
    }

}
