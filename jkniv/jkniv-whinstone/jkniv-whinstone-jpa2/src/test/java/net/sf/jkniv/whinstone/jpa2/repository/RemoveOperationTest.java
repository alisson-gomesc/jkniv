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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.acme.domain.orm.Author;
import com.acme.domain.orm.Book;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class RemoveOperationTest extends BaseTest
{
    @Test
    @Transactional
    public void removeTest()
    {
        Repository repository = getRepository();
        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        Book b1 = new Book(), b2 = null;
        b1.setName(name);
        b1.setIsbn(isbn);
        
        repository.add(b1);
        Assert.assertNotNull(b1.getId());
        b2 = repository.get(b1);
        Assert.assertNotNull(b2);
        Assert.assertEquals(name, b2.getName());
        Assert.assertEquals(isbn, b2.getIsbn());
        repository.remove(b2);
        b2 = repository.get(b1);
        Assert.assertNull(b2);
    }

    @Ignore("Script db-load.sql doesn't works, fix spring-contex.xml with <jdbc:initialize-database... ")
    @Test
    @Transactional
    public void whenRemoveUsinNativeQuery()
    {
        Repository repository = getRepository();
        Author a = new Author();
        a.setId(505L);
        Queryable queryable = QueryFactory.newInstance("deleteAuthor2Native", a);
        
        Queryable countQuery = QueryFactory.newInstance("Book.list", a);
        int count = repository.list(countQuery).size();

        assertThat(count, is(6));

        int rows = repository.remove(queryable);
        
        assertThat(rows, is(1));
    }

    
    
}
