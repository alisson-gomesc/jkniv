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
package net.sf.jkniv.whinstone.jdbc.params;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;

//import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
//import static org.hamcrest.core.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.acme.domain.Book;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatAuthor;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatBook;

public class SqlUpdateCollectionTest extends BaseJdbc
{
    @Autowired
    Repository repositoryDerby;

    @Test
    public void whenUpdateUsingCollectionOfEntity()
    {
        Collection<Book> params = getValuesBook();
        Queryable qUpdate = QueryFactory.newInstance("Book#update", params);
        Queryable qIsbn = QueryFactory.newInstance("Book#get", params.iterator().next());        
        int rowsAffected = repositoryDerby.update(qUpdate);
        assertThat(rowsAffected, is(10));
        
        Book book = repositoryDerby.get(qIsbn);
        assertThat(book, notNullValue());
        assertThat(book.getVisualization(), is(10));
        assertThat(book.getIsbn(), is("978-1503250888"));
        assertThat(book.getId(), is(1001L));
        assertThat(book.getName(), is("Beyond Good and Evil"));
    }
    
    @Test
    public void whenUpdateUsingCollectionOfMapWith()
    {
        Collection<Map<String, Object>> params = getValuesBookAsMap();
        Queryable qUpdate = QueryFactory.newInstance("Book#update", params);
        Queryable qIsbn = QueryFactory.newInstance("Book#get", params.iterator().next());        
        int rowsAffected = repositoryDerby.update(qUpdate);
        assertThat(rowsAffected, is(10));
        
        Book book = repositoryDerby.get(qIsbn);
        assertThat(book, notNullValue());
        assertThat(book.getVisualization(), is(20));
        assertThat(book.getIsbn(), is("978-1503250888"));
        assertThat(book.getId(), is(1001L));
        assertThat(book.getName(), is("Beyond Good and Evil"));
    }
    
    @Test
    public void whenUpdateUsingCollectionOfArray()
    {
        Collection<Object[]> params = getValuesBookAsArray();
        Book book = new Book();
        book.setId(1001L);
        Queryable qUpdate = QueryFactory.newInstance("Book#update2", params);
        Queryable qIsbn = QueryFactory.newInstance("Book#get", book);        
        int rowsAffected = repositoryDerby.update(qUpdate);
        assertThat(rowsAffected, is(10));
        
        book = repositoryDerby.get(qIsbn);
        assertThat(book, notNullValue());
        assertThat(book.getVisualization(), is(30));
        assertThat(book.getIsbn(), is("978-1503250888"));
        assertThat(book.getId(), is(1001L));
        assertThat(book.getName(), is("Beyond Good and Evil"));
    }

    private Collection<Book> getValuesBook()
    {
        List<Book> list = new ArrayList();
        for (int i=0; i<10; i++)
        {
        	Book b = new  Book();
            b.setId(1001L);
            b.setIsbn("978-1503250888");
            b.setName("Beyond Good and Evil");
            b.setVisualization(i+1);
            list.add(b);
        }
        return list;
    }

    private Collection<Map<String, Object>> getValuesBookAsMap()
    {
        List<Map<String, Object>> list = new ArrayList();
        for (int i=0; i<10; i++)
        {
            Map<String, Object> row = new HashMap<String, Object>();
            row.put("id", 1001);
            row.put("isbn", "978-1503250888");
            row.put("name", "Beyond Good and Evil");
            row.put("visualization", i+11);
            list.add(row);
        }
        return list;
    }

    //update book set name = ?, isbn = ?, visualization = ? where id = ?
    private Collection<Object[]> getValuesBookAsArray()
    {
        List<Object[]> list = new ArrayList();
        for (int i=0; i<10; i++)
        {
            Object[] param = new Object[4];
            param[0] = "Beyond Good and Evil";
            param[1] = "978-1503250888";
            param[2] = i+21;
            param[3] = 1001L;
            list.add(param);
        }
        return list;
    }

}
