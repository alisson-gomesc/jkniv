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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;
import net.sf.jkniv.whinstone.couchdb.result.CustomResultRow;

public class CouchDbRepositoryFindTest extends BaseJdbc
{
    
    @Test
    public void whenCouchDbListWithFixedFind()
    {
        Repository repositoryDb = getRepository();
        Queryable q = getQuery("authorsBR");
        
        List<Map> list = repositoryDb.list(q);
        assertThat(list.size(), greaterThan(0));
        assertThat(list.get(0), instanceOf(Map.class));
        System.out.println(list.get(0));
    }
    

    @Test
    public void whenCouchDbListTypedObject()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("authorsBRTyped");
        
        List<Author> list = repositoryDb.list(q);
        assertThat(list.size(), greaterThan(0));
        assertThat(list.get(0), instanceOf(Author.class));
        System.out.println(list.get(0));
    }
    
    @Test @Ignore("doesn't work")
    public void whenCouchDbListBooksFromNestedProperty()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("booksFromYear", "year", 1886L);
        
        List<Author> list = repositoryDb.list(q);
        assertThat(list.size(), is(1));
        assertThat(list.get(0), instanceOf(Author.class));
        assertThat(list.get(0).getBooks().get(0).getPublished(), is(1886L));
        System.out.println(list.get(0));
    }
    
    
    @Test
    public void whenCouchDbListWithFindParametrized()
    {
        Repository repositoryDb = getRepository();
        Map<String, String> params = new HashMap<String, String>();
        params.put("nat", "DE");
        Queryable q = getQuery("authorsNat", params);
        
        List<Map> list = repositoryDb.list(q);
        assertThat(list.size(), greaterThan(0));
        assertThat(list.get(0), instanceOf(Map.class));
        System.out.println(list.get(0));
    }

    @Test
    public void whenCouchDbListUsingLikeTwoFieldsSameParam()
    {
        Repository repositoryDb = getRepository();        
        List<Map> list = repositoryDb.list(QueryFactory.of("authorsUsingLike", "name", "(?i)ka"));
        assertThat(list.size(), is(2));
        assertThat(list.get(0), instanceOf(Map.class));
        System.out.println(list.get(0));
    }

}
