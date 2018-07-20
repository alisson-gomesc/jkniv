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
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;
import net.sf.jkniv.whinstone.couchdb.result.CustomResultRow;

public class CouchDbRepositoryViewTest extends BaseJdbc
{
    
    @Test
    public void whenUseViewWithoutParams()
    {
        Repository repositoryDb = getRepository();
        List<Map> list = repositoryDb.list(QueryFactory.of("docs/_view/natio"), Map.class);
        assertThat(list.size(), greaterThan(0));
        assertThat(list.get(0), instanceOf(Map.class));
        System.out.println(list.get(0));
    }

    @Test
    public void whenUseViewListWithReturnType()
    {
        Repository repositoryDb = getRepository();
        List<Author> list = repositoryDb.list(QueryFactory.of("docs/_view/natio"));
        assertThat(list.size(), greaterThan(0));
        assertThat(list.get(0), instanceOf(Author.class));
    }

//    @Test
//    public void whenUseViewGetWithReturnType()
//    {
//        Repository repositoryDb = getRepository();
//        Author author = repositoryDb.get(QueryFactory.of("docs/_view/natio"));
//        assertThat(author, notNullValue());
//        assertThat(author, instanceOf(Author.class));
//    }

    @Test
    public void whenUseViewWithParams()
    {
        Repository repositoryDb = getRepository();
        Queryable q = getQuery("docs/_view/natio", asParams("startkey","DE","endkey","DE"));
        
        List<Author> list = repositoryDb.list(q);
        assertThat(list.size(), greaterThanOrEqualTo(3));
        assertThat(list.get(0), instanceOf(Author.class));
        System.out.println(list.get(0));
    }
    
}
