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
package net.sf.jkniv.whinstone.jpa2.repository;

//import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.acme.domain.orm.Book;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class SqlUpdateCollectionTest extends BaseTest
{
    @Test @Transactional 
    public void whenUpdateUsingQueryable()
    {
        Repository repository = getRepository();
        Book params = getValuesBook().iterator().next();
        Queryable queryUpdate = QueryFactory.of("Book#update", params);
        Queryable queryIsbn = QueryFactory.of("Book#get", params);        
        int rowsAffected = repository.update(queryUpdate);
        assertThat(rowsAffected, is(1));
        
        Book book = repository.get(queryIsbn);
        assertThat(book, notNullValue());
        assertThat(book.getVisualization(), is(1));
        assertThat(book.getIsbn(), is("978-1503250888"));
        assertThat(book.getId(), is(1001L));
        assertThat(book.getName(), is("Beyond Good and Evil"));
    }

    @Test @Transactional
    public void whenUpdateUsingCollectionOfEntity()
    {
        Repository repository = getRepository();
        Collection<Book> params = getValuesBook();
        Queryable queryUpdate = QueryFactory.of("Book#update", params);
        Queryable queryIsbn = QueryFactory.of("Book#get", params.iterator().next());        
        int rowsAffected = repository.update(queryUpdate);
        assertThat(rowsAffected, is(10));
        
        Book book = repository.get(queryIsbn);
        assertThat(book, notNullValue());
        assertThat(book.getVisualization(), is(10));
        assertThat(book.getIsbn(), is("978-1503250888"));
        assertThat(book.getId(), is(1001L));
        assertThat(book.getName(), is("Beyond Good and Evil"));
    }
    
    @Test @Transactional
    public void whenUpdateUsingCollectionOfMapWith()
    {
        Repository repository = getRepository();
        
        Collection<Map<String, Object>> params = getValuesBookAsMap();
        Queryable qUpdate = QueryFactory.of("Book#update", params);
        Queryable qIsbn = QueryFactory.of("Book#get", params.iterator().next());        
        int rowsAffected = repository.update(qUpdate);
        assertThat(rowsAffected, is(10));
        
        Book book = repository.get(qIsbn);
        assertThat(book, notNullValue());
        assertThat(book.getVisualization(), is(20));
        assertThat(book.getIsbn(), is("978-1503250888"));
        assertThat(book.getId(), is(1001L));
        assertThat(book.getName(), is("Beyond Good and Evil"));
    }
    
    @Test @Transactional
    public void whenUpdateUsingCollectionOfArray()
    {
        Repository repository = getRepository();
        
        Collection<Object[]> params = getValuesBookAsArray();
        Book book = new Book();
        book.setId(1001L);
        Queryable qUpdate = QueryFactory.of("Book#update2", params);
        Queryable qIsbn = QueryFactory.of("Book#get", book);        
        int rowsAffected = repository.update(qUpdate);
        assertThat(rowsAffected, is(10));
        
        book = repository.get(qIsbn);
        assertThat(book, notNullValue());
        assertThat(book.getVisualization(), is(30));
        assertThat(book.getIsbn(), is("978-1503250888"));
        assertThat(book.getId(), is(1001L));
        assertThat(book.getName(), is("Beyond Good and Evil"));
    }

    private Collection<Book> getValuesBook()
    {
        List<Book> list = new ArrayList<Book>();
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
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i=0; i<10; i++)
        {
            Map<String, Object> row = new HashMap<String, Object>();
            row.put("id", 1001L);
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
        List<Object[]> list = new ArrayList<Object[]>();
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
