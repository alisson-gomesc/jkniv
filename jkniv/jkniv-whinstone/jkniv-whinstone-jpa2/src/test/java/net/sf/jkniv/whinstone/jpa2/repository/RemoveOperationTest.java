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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.acme.domain.orm.Author;
import com.acme.domain.orm.Book;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class RemoveOperationTest extends BaseTest
{
    @Test
    @Transactional
    public void whenRemoveEntityUsinJpaMapping()
    {
        Repository repository = getRepository();
        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        Book newBook = new Book(), removeBook = null;
        newBook.setName(name);
        newBook.setIsbn(isbn);
        
        repository.add(newBook);
        assertThat(newBook.getId(), notNullValue());
        removeBook = repository.get(newBook);
        assertThat(removeBook, notNullValue());
        assertThat(name, is(removeBook.getName()));
        assertThat(isbn, is(removeBook.getIsbn()));
        repository.remove(removeBook);
        
        removeBook = repository.get(newBook);
        assertThat(removeBook, nullValue());
    }

    @Test
    @Transactional
    public void whenRemoveEntityUsingNativeQuery()
    {
        Repository repository = getRepository();
        Author a = new Author();
        a.setId(505L);
        Queryable queryable = QueryFactory.of("deleteAuthor2Native", a);
        
        Queryable countQuery = QueryFactory.of("Author.list", a);
        int count = repository.list(countQuery).size();

        assertThat(count, is(6));

        int rows = repository.remove(queryable);
        
        assertThat(rows, is(1));
        
        int recount = repository.list(countQuery).size();
        assertThat(recount, is(5));
    }

    
    
}
