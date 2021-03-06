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
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.acme.domain.orm.Author;
import com.acme.domain.orm.Book;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class AddOperationTest extends BaseTest
{
    
    @Test
    @Transactional
    public void addTest()
    {
        Repository repository = getRepository();

        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        Book b1 = new Book(), b2 = null;
        b1.setName(name);
        b1.setIsbn(isbn);
        
        repository.add(b1);
        
        assertThat(b1.getId(), notNullValue());
        b2 = repository.get(b1);
        assertThat(b2, notNullValue());
        assertThat(name, is(b2.getName()));
        assertThat(isbn, is(b2.getIsbn()));
    }

    @Test
    @Transactional
    public void whenAddUsinNativeQuery()
    {
        Repository repository = getRepository();
        Author a = new Author();
        a.setId(System.currentTimeMillis());
        a.setName("Bob Dylan");
        Queryable queryable = QueryFactory.of("saveAuthor2Native", a);
        
        int rows = repository.add(queryable);
        assertThat(rows, is(1));
    }
    
    
}
