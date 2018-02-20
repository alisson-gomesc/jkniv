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
package net.sf.jkniv.whinstone.jpa2.old.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.acme.domain.flat.AuthorFlat;
import com.acme.domain.orm.Author;
import com.acme.domain.orm.Book;

import junit.framework.Assert;
import net.sf.jkniv.sqlegance.IQuery;
import net.sf.jkniv.sqlegance.Query;
import net.sf.jkniv.whinstone.jpa2.AuthorRepository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;
import net.sf.jkniv.whinstone.jpa2.BookRepository;

@Ignore("AbstractRepository protected level")
public class SelectOperationTest extends BaseTest
{
    //@Autowired
    BookRepository bookRepository;

    //@Autowired
    AuthorRepository authorRepository;   


    @Test
    public void getExecuteQueryTest()
    {
        Long ONE = new Long("1");
        Book b = new Book();
        b.setId(ONE);
        b = bookRepository.get(b);
    }
    
    @Test
    @Transactional
    public void listTest()
    {
        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        final int SIZE = 2;
        for (int i = 0; i < SIZE; i++)
        {
            Book b1 = new Book();
            b1.setName(name + " " + i);
            b1.setIsbn(isbn);
            bookRepository.add(b1);
        }
        List<Book> list = bookRepository.list();
        Assert.assertEquals(SIZE+TOTAL_BOOKS, list.size());
    }
    
    @Test
    @Transactional
    public void listPaginated()
    {
        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        final Long SIZE = 25L, MAX = 10L;
        for (int i = 0; i < SIZE; i++)
        {
            Book b1 = new Book();
            b1.setName(name + " " + i);
            b1.setIsbn(isbn);
            bookRepository.add(b1);
        }
        Book b = new Book();
        b.setName("Spoke%");
        IQuery query = new Query("listLikeName", b, 0, MAX.intValue());
        List<Book> list = bookRepository.list(query);
        
        Assert.assertEquals(MAX.intValue(), list.size());
        Assert.assertEquals(SIZE.longValue()+TOTAL_BOOKS, query.getTotal());
    }
    
    @Test
    @Transactional
    public void listBeanTest()
    {
        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        IQuery query = null;
        Author author = new Author();
        List<AuthorFlat> authorFlats = null;
        List<Book> books = new ArrayList<Book>();
        final int SIZE = 5;
        
        author.setName("Alisson");
        author.setBooks(books);
        authorRepository.add(author);
        for (int i = 0; i < SIZE; i++)
        {
            Book b1 = new Book();
            b1.setName(name + " " + i);
            b1.setIsbn(isbn);
            b1.setAuthor(author);
            bookRepository.add(b1);
            books.add(b1);
        }
        
        authorRepository.flush();
        query = new Query("listBean", author, 0, SIZE);
        authorFlats = authorRepository.list(query, AuthorFlat.class);
        
        Assert.assertNotNull(authorFlats);
        Assert.assertEquals(SIZE, authorFlats.size());
        
        Assert.assertEquals(SIZE, books.size());
        for (int i = 0; i < SIZE; i++)
        {
            Book b = books.get(i);
            Assert.assertEquals(name + " " + i, b.getName());
            bookRepository.remove(b);
        }
    }
    
    @Test
    @Transactional
    public void selectORMQuery()
    {
        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        Book b1 = new Book(), b2 = null;
        b1.setName(name);
        b1.setIsbn(isbn);
        bookRepository.add(b1);
        Assert.assertNotNull(b1.getId());
        
        IQuery q1 = new Query("jpql.getBookOrdered");
        IQuery q2 = new Query("jpql.getBookByName", params);
        
        List<Book> list = bookRepository.list(q1);
        b2 = bookRepository.get(q2);
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        Assert.assertNotNull(b2);
        Assert.assertEquals(name, b2.getName());
        Assert.assertEquals(isbn, b2.getIsbn());
    }
    
}
