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
package net.sf.jkniv.whinstone.couchdb.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.*;
//import static org.mockito.Matchers.startsWith;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.json.JsonUtil;

public class JsonResolverTest
{

    @Test
    public void whenReadJsonContentAsString()
    {
        String content = JsonUtil.fromFile("/json-data/result-all-docs.json");
        assertThat(content, startsWith("{ \"total_rows\": 7, \"offset\": 0, \"rows\": ["));
    }

    @Test
    public void whenGetAllDocsAnswer()
    {
        String content = JsonUtil.fromFile("/json-data/result-all-docs.json");
        JsonResolver resolver = JsonResolver.of(content, Map.class);
        
        //assertThat((long)list.size(), greaterThanOrEqualTo(getTotalDocs()));
        //assertThat(q.getTotal(), greaterThanOrEqualTo(getTotalDocs()));
        //assertThat(list.get(0), instanceOf(Map.class));
    }
}
