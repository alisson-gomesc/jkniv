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
package net.sf.jkniv.whinstone.couchdb;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;
import net.sf.jkniv.whinstone.couchdb.model.orm.AuthorView;
import net.sf.jkniv.whinstone.couchdb.result.CustomResultRow;
import net.sf.jkniv.whinstone.params.ParameterException;

public class CouchDbRepositoryPaginationTest extends BaseJdbc
{
    @Rule
    public ExpectedException  catcher = ExpectedException.none();
    
    @Test
    public void whenCouchDbTryPaginateQueryWithLimit()
    {
        catcher.expect(ParameterException.class);
        Repository repositoryDb = getRepository();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nations", Arrays.asList("DE","GB","BR","CZ"));
        Queryable q = QueryFactory.of("authors-page-override", params, 0, 3);
        List<Map<String, ?>> list = repositoryDb.list(q);
        assertThat(q.getTotal(), is(-1L));
    }

    
    @Test
    public void whenCouchDbListWithFindPagination()
    {
        Repository repositoryDb = getRepository();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nations", Arrays.asList("DE"));
        Queryable q = QueryFactory.of("authors-page", params, 0, 2);
        
        List<Map<String, ?>> list = repositoryDb.list(q);
        assertThat(list.size(), is(2));
        assertThat(q.getTotal(), is((long)Statement.SUCCESS_NO_INFO));
        assertThat(list.get(0), instanceOf(Map.class));
    }


    @Test
    public void whenUseViewWithParams()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("docs/_view/natio", asParams("startkey","DE","endkey","DE"), 0, 2);
        List<AuthorView> list = repositoryDb.list(q);
        //TODO fix views load all records from database assertThat(q.getTotal(), is((long)TOTAL_AUTHORS));
        assertThat(list.size(), is(2));
        assertThat(list.get(0), instanceOf(AuthorView.class));
        assertThat(list.get(0).getKey(), is("DE"));
    }

}
