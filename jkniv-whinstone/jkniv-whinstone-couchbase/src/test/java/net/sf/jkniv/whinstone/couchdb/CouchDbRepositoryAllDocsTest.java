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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;

@Ignore("delete me test from couchdb")
public class CouchDbRepositoryAllDocsTest extends BaseJdbc
{
    // TODO implements POST /{db}/_all_docs/queries
    // http://docs.couchdb.org/en/latest/api/database/bulk-api.html#post--db-_all_docs-queries
    
    private static final long SUCCESS_NO_INFO = Long.valueOf(Statement.SUCCESS_NO_INFO);
    
    @Test
    public void whenListAllDocs()
    {
        Repository repositoryDb = getRepository();
        Queryable q = getQuery("_all_docs");
        
        List<Map> list = repositoryDb.list(q);
        assertThat((long)list.size(), greaterThanOrEqualTo(getTotalDocs()));
        assertThat(q.getTotal(), greaterThanOrEqualTo(getTotalDocs()));
        assertThat(list.get(0), instanceOf(Map.class));
        //System.out.println(list.get(0));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void whenListAllDocsWithLimit()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("_all_docs", 2, 3);
        
        List<Map> list = repositoryDb.list(q);
        assertThat(list.size(), is(3));
        //assertThat(q.getTotal(), greaterThanOrEqualTo(getTotalDocs()));
        assertThat(q.getTotal(), is(SUCCESS_NO_INFO));
        assertThat(list.get(0), instanceOf(Map.class));
        //assertThat(list.get(0).get("id").toString(), is("3"));
        //System.out.println(list.get(0));
    }
    
    @Test
    public void whenListAllDocsWithLimitAndDescendingParam()
    {
        Repository repositoryDb = getRepository();
        Map<String, Object> params= new HashMap<String, Object>();
        params.put("descending", true);
        
        Queryable q = getQuery("_all_docs", params, 1, 3);
        
        List<Map> list = repositoryDb.list(q);
        assertThat(list.size(), is(3));
        //assertThat(q.getTotal(), greaterThanOrEqualTo(getTotalDocs()));
        assertThat(q.getTotal(), is(SUCCESS_NO_INFO));
        assertThat(list.get(0), instanceOf(Map.class));
        //assertThat(list.get(0).get("id").toString(), is("7"));
        //System.out.println(list.get(0));
    }
    
    
}
