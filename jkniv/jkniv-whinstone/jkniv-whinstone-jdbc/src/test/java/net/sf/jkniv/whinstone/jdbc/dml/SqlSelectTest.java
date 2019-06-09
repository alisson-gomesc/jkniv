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
package net.sf.jkniv.whinstone.jdbc.dml;

import static net.sf.jkniv.whinstone.jdbc.RepositoryJdbcInstanceTest.TOTAL_BOOKS;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.CustomResultRow;
import net.sf.jkniv.whinstone.jdbc.domain.acme.Book;
import net.sf.jkniv.whinstone.jdbc.domain.acme.FlatAuthor;
import net.sf.jkniv.whinstone.jdbc.domain.acme.FlatBook;

public class SqlSelectTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Autowired
    Repository repositoryDerby;
    
    @Test @Ignore("mockito")
    public void whenSelectWrongClassType()
    {
        catcher.expect(RepositoryException.class);
        Queryable q = QueryFactory.of("getBookByISBN");
        List<FlatAuthor> list = repositoryDerby.get(q);
        Assert.assertTrue(list.size() > 0);
        Assert.assertTrue(list.get(0) instanceof FlatAuthor);
    }
    
    //public void whenSelectOneRecordByUniqueValue()
    @Test @Ignore("mockito")
    public void whenSelectAllRecords()
    {
        Queryable q = QueryFactory.of("getBookByISBN");
        List<FlatBook> books = repositoryDerby.list(q);
        Assert.assertTrue(books.size() == TOTAL_BOOKS);
        Assert.assertTrue(books.get(0) instanceof FlatBook);
        for (FlatBook b : books)
        {
            assertThat(b.getAuthor(), notNullValue());
            assertThat(b.getAuthorId(), notNullValue());
            assertThat(b.getId(), notNullValue());
            assertThat(b.getIsbn(), notNullValue());
            assertThat(b.getName(), notNullValue());
        }
    }
    
    @Test @Ignore("mockito")
    public void whenSelectOverloadReturnType()
    {
        Queryable q = QueryFactory.of("getBookToOverloadType");
        List<Book> list = repositoryDerby.list(q, Book.class);
        Assert.assertTrue(list.size() == TOTAL_BOOKS);
        for (Book b : list)
        {
            assertThat(b, instanceOf(Book.class));
            assertThat(b.getId(), notNullValue());
            assertThat(b.getName(), notNullValue());
            assertThat(b.getIsbn(), notNullValue());
        }
    }
    
    @Test @Ignore("mockito")
    public void whenSelectDoesntDefinedReturnType()
    {
        //catcher.expect(RepositoryException.class); //now, default is java.util.Map
        Queryable q = QueryFactory.of("listBooksNoSpecificType");
        List<Map> list = repositoryDerby.list(q);
        assertThat(list.size(), is(TOTAL_BOOKS));
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(HashMap.class));
    }
    
    @Test @Ignore("mockito")
    public void whenSelectWithoutTypeAndCustomResultSetParser()
    {
        CustomResultRow parser = null;
        Queryable q = QueryFactory.of("listBooksNoSpecificType");
        List<HashMap<String, Object>> list = repositoryDerby.list(q, parser);
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(HashMap.class));
        assertThat(String.valueOf(list.get(0).get("name")), is("Beyond Good and Evil"));
    }
    
    @Test @Ignore("mockito")
    public void whenSelectDoesntDefinedReturnTypeButForceOne()
    {
        Queryable q = QueryFactory.of("listBooksNoSpecificType");
        List<Book> list = repositoryDerby.list(q, Book.class);
        Assert.assertTrue(list.size() == TOTAL_BOOKS);
        Assert.assertTrue(list.get(0) instanceof Book);
    }
    
    @Test @Ignore("mockito")
    public void whenSelectOneRecordByUniqueValueWithMapParams()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("isbn", "978-1503250888");
        Queryable q = QueryFactory.of("getBookByISBNWithMap", map);
        List<FlatBook> list = repositoryDerby.list(q);
        Assert.assertTrue(list.size() == 1);
    }
    
    @Test
    public void whenSelectOneRecordByUniqueValueWithListParams()
    {
        List<Object> params = new ArrayList<Object>();
        params.add("978-1503250888");
        Queryable q = QueryFactory.of("getBookByISBN", params);
        List<FlatBook> list = repositoryDerby.list(q);
        Assert.assertTrue(list.size() == 1);
    }
    
    @Test
    public void whenSelectRecordsUsingDotsToMarkParams()
    {
        Map<String, Long> params = new HashMap<String, Long>();
        params.put("id", Long.valueOf(2));
        Queryable q = QueryFactory.of("getAuthorWithTwoDots", params);
        List<FlatAuthor> list = repositoryDerby.list(q);
        Assert.assertTrue(list.size() == 1);
    }
    
    @Test
    public void whenSelectRecordsUsingHashToMarkParams()
    {
        Map<String, Long> params = new HashMap<String, Long>();
        params.put("id", Long.valueOf(1));
        Queryable q = QueryFactory.of("getAuthorWithHashParam", params);
        List<FlatAuthor> list = repositoryDerby.list(q);
        Assert.assertTrue(list.size() == 1);
    }
    
    @Test @Ignore("mockito")
    public void whenGetRecordWithoutQueryable()
    {
        FlatBook b = new FlatBook();
        b.setIsbn("978-0321826626");
        
        FlatBook b1 = repositoryDerby.get(b);
        assertThat(b1, is(notNullValue()));
    }

}
