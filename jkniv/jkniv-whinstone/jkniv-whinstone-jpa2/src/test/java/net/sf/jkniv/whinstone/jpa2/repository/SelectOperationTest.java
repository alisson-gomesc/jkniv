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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.acme.domain.flat.AuthorFlat;
import com.acme.domain.orm.Author;
import com.acme.domain.orm.Book;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class SelectOperationTest extends BaseTest
{
    @Test
    public void getExecuteQueryTest()
    {
        Repository repository = getRepository();
        Long ONE = new Long("1");
        Book b = new Book();
        b.setId(ONE);
        b = repository.get(b);
        Assert.assertNull(b);
    }
    
    @Test
    @Transactional
    public void listTest()
    {
        Repository repository = getRepository();

        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        final int SIZE = 2;
        for (int i = 0; i < SIZE; i++)
        {
            Book b1 = new Book();
            b1.setName(name + " " + i);
            b1.setIsbn(isbn);
            repository.add(b1);
        }
        List<Book> list = repository.list(QueryFactory.newInstance("Book.list"));
        assertThat(list.size(), is(SIZE+TOTAL_BOOKS));
    }
    
    @Test
    @Transactional
    public void listPaginated()
    {
        Repository repository = getRepository();
        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        final Long SIZE = 25L, MAX = 10L;
        for (int i = 0; i < SIZE; i++)
        {
            Book b1 = new Book();
            b1.setName(name + " " + i);
            b1.setIsbn(isbn);
            repository.add(b1);
        }
        Book b = new Book();
        b.setName("Spoke%");
        Queryable query = QueryFactory.newInstance("listLikeName", b, 0, MAX.intValue());
        List<Book> list = repository.list(query);
        
        assertThat(list.size(), is(MAX.intValue()));
        assertThat(query.getTotal(), is(SIZE+TOTAL_BOOKS));
    }
    
    @Test
    @Transactional
    public void listBeanTest()
    {
        Repository repository = getRepository();

        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        Queryable query = null;
        Author author = new Author();
        List<AuthorFlat> authorFlats = null;
        List<Book> books = new ArrayList<Book>();
        final int SIZE = 5;
        
        author.setName("Alisson");
        author.setBooks(books);
        repository.add(author);
        for (int i = 0; i < SIZE; i++)
        {
            Book b1 = new Book();
            b1.setName(name + " " + i);
            b1.setIsbn(isbn);
            b1.setAuthor(author);
            repository.add(b1);
            books.add(b1);
        }
        
        repository.flush();
        query = QueryFactory.newInstance("listBean", author, 0, SIZE);
        authorFlats = repository.list(query);
        
        Assert.assertNotNull(authorFlats);
        Assert.assertEquals(SIZE, authorFlats.size());
        
        Assert.assertEquals(SIZE, books.size());
        for (int i = 0; i < SIZE; i++)
        {
            Book b = books.get(i);
            Assert.assertEquals(name + " " + i, b.getName());
            repository.remove(b);
        }
    }
    
    @Test
    @Transactional
    public void selectORMQuery()
    {
        Repository repository = getRepository();

        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        Book b1 = new Book(), b2 = null;
        b1.setName(name);
        b1.setIsbn(isbn);
        repository.add(b1);
        Assert.assertNotNull(b1.getId());
        
        Queryable q1 = QueryFactory.newInstance("jpql_getBookOrdered");
        Queryable q2 = QueryFactory.newInstance("jpql_getBookByName", params);
        
        List<Book> list = repository.list(q1);
        b2 = repository.get(q2);
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        Assert.assertNotNull(b2);
        Assert.assertEquals(name, b2.getName());
        Assert.assertEquals(isbn, b2.getIsbn());
    }
}
