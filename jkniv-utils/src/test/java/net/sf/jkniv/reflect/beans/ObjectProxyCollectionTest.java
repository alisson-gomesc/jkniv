/* 
 * JKNIV, utils - Helper utilities for jdk code.
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
package net.sf.jkniv.reflect.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.sf.jkniv.acme.domain.Author;
import net.sf.jkniv.acme.domain.Book;

public class ObjectProxyCollectionTest
{
    
    @Test
    public void whenCopyDataFromAnotheObject()
    {
        Author authorEmpty = new Author();
        Author author = new Author();
        Book b1 = new Book(1001L, "Book 1", "ISBN-1", "John"), b2 = new Book(1001L, "Book 1", "ISBN-1", "John");
        List<Book> books = new ArrayList<Book>();
        books.add(b1);
        books.add(b2);
        author.setName("John");
        author.setId(101L);
        author.setAge(40);
        author.setBooks(books);
        ObjectProxy<Author> proxy = new DefaultObjectProxy<Author>(authorEmpty);
        
        proxy.from(author);
        
        assertThat(proxy.getInstance(), is(notNullValue()));
        assertThat(proxy.getInstance().getId(), is(101L));
        assertThat(proxy.getInstance().getName(), is("John"));
        assertThat(proxy.getInstance().getAge(), is(40));
        assertThat(proxy.getInstance().getBooks().size(), is(2));
    }
    
    
    @Test
    public void whenMergeDataFromAnotheObject()
    {
        Author authorEmpty = new Author();
        Author author = new Author();
        Book b1 = new Book(1001L, "Book 1", "ISBN-1", "John"), 
             b2 = new Book(1002L, "Book 2", "ISBN-2", "John"),
             b3 = new Book(1003L, "Book 3", "ISBN-3", "John"), 
             b4 = new Book(1004L, "Book 4", "ISBN-4", "John");
        List<Book> books = new ArrayList<Book>(), books2 = new ArrayList<Book>();
        books.add(b3);
        books.add(b4);
        
        books2.add(b1);
        books2.add(b2);
        
        authorEmpty.setBooks(books2);
        
        author.setName("John");
        author.setId(101L);
        author.setAge(40);
        author.setBooks(books);
        
        ObjectProxy<Author> proxy = new DefaultObjectProxy<Author>(authorEmpty);
        
        proxy.merge(author);
        
        assertThat(proxy.getInstance(), is(notNullValue()));
        assertThat(proxy.getInstance().getId(), is(101L));
        assertThat(proxy.getInstance().getName(), is("John"));
        assertThat(proxy.getInstance().getAge(), is(40));
        assertThat(proxy.getInstance().getBooks().size(), is(4));
        for(int i=0; i<4; i++)
        {
            assertThat(proxy.getInstance().getBooks().get(i).getId(), is(1000L+i+1));
            assertThat(proxy.getInstance().getBooks().get(i).getName(), is("Book " +(i+1)));
            assertThat(proxy.getInstance().getBooks().get(i).getIsbn(), is("ISBN-" +(i+1)));
            assertThat(proxy.getInstance().getBooks().get(i).getAuthor().getName(), is("John"));
        }
    }
    

}
