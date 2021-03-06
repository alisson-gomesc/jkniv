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
package net.sf.jkniv.whinstone.couchdb.commands.view;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sf.jkniv.whinstone.couchdb.model.orm.Author;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.BaseJdbc;
import net.sf.jkniv.whinstone.couchdb.CouchDbResult;
import net.sf.jkniv.whinstone.couchdb.model.orm.AuthorView;
import net.sf.jkniv.whinstone.couchdb.model.orm.Combo;

public class CouchDbViewTest extends BaseJdbc
{
    @Test
    public void whenUseViewWithoutParams()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("docs/_view/natio");
        List<Map> list = repositoryDb.list(q, Map.class);
        assertThat(q.getTotal(), is((long)list.size()));
        assertThat(list.size(), greaterThan(0));
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0).get("id"), notNullValue());
        assertThat(list.get(0).get("key"),notNullValue());
        assertThat(list.get(0).get("key").toString(), is(list.get(0).get("nationality")));
        assertThat(list.get(0).get("name"), notNullValue());
        assertThat(list.get(1).get("name"), notNullValue());
        assertThat(list.get(2).get("name"), notNullValue());
    }

    @Test
    public void whenUseViewListWithReturnType()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("docs/_view/natio");
        List<AuthorView> list = repositoryDb.list(q);
        assertThat(q.getTotal(), is((long)list.size()));
        assertThat(list.size(), greaterThan(0));
        assertThat(list.get(0), instanceOf(AuthorView.class));
        for(AuthorView view : list)
        {
            assertThat(view.getId(), notNullValue());
            assertThat(view.getKey(), is(view.getNationality()));
        }
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
        Queryable q = QueryFactory.of("docs/_view/natio", "startkey","DE","endkey","DE");
        List<AuthorView> list = repositoryDb.list(q);
        assertThat(q.getTotal(), is((long)list.size()));
        assertThat(list.size(), greaterThanOrEqualTo(3));
        assertThat(list.get(0), instanceOf(AuthorView.class));
        assertThat(list.get(0).getKey(), is("DE"));
    }

    @Test
    public void whenUseViewIncludeDoc()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("docs/_view/author", "key","Franz Kafka","include_docs","true");
        List<Author> list = repositoryDb.list(q);
        assertThat(q.getTotal(), is((long)list.size()));
        assertThat(list.size(), is(1));
        assertThat(list.get(0).getBooks().size(), is(2));
        assertThat(list.get(0), instanceOf(Author.class));
    }

    @Test
    public void whenViewUseValueAsMapAndGroupTrue()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("docs/_view/natio-combo", "group", "true");
        List<Map> list = repositoryDb.list(q);
        assertThat(q.getTotal(), is((long)list.size()));
        assertThat(list.size(), greaterThanOrEqualTo(5));
        assertThat(list.get(0), instanceOf(Map.class));
    }

    @Test
    public void whenViewAsComboWithGroupTrue()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("docs/_view/natio-combo", Combo.class, "group", "true");
        List<Combo> list = repositoryDb.list(q);
        assertThat(q.getTotal(), is((long)list.size()));
        assertThat(list.size(), greaterThanOrEqualTo(5));
        assertThat(list.get(0), instanceOf(Combo.class));
    }
    
    @Test
    public void whenViewReturnDefaultCouchDbReturn()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("docs/_view/natio-combo", CouchDbResult.class, "group", "true");
        CouchDbResult result = repositoryDb.get(q);
        assertThat(result, instanceOf(CouchDbResult.class));
        assertThat(result.getRows().size(), greaterThanOrEqualTo(5));
    }

    @Test
    public void whenViewUseValueAsIntegerAndGroupTrue()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("docs/_view/natio-combo", Integer.class, "group", "true");
        List<Integer> list = repositoryDb.list(q);
        assertThat(q.getTotal(), is((long)list.size()));
        assertThat(list.size(), greaterThanOrEqualTo(5));
        assertThat(list.get(0), instanceOf(Integer.class));        
    }

    @Test
    public void whenViewUseValueAsInteger()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("docs/_view/natio-combo", Integer.class);
        List<Integer> list = repositoryDb.list(q);
        assertThat(q.getTotal(), is((long)list.size()));
        assertThat(list.size(), is(1));
        assertThat(list.get(0), instanceOf(Integer.class));
    }

}
