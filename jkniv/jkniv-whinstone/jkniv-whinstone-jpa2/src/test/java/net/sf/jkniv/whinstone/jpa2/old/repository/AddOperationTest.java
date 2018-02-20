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

import junit.framework.Assert;
import net.sf.jkniv.whinstone.jpa2.BaseTest;
import net.sf.jkniv.whinstone.jpa2.BookRepository;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.acme.domain.orm.Book;

@Ignore("AbstractRepository protected level")
public class AddOperationTest extends BaseTest
{
    //@Autowired
    BookRepository   bookRepository;

    @Test
    @Transactional
    public void addTest()
    {
        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        Book b1 = new Book(), b2 = null;
        b1.setName(name);
        b1.setIsbn(isbn);
        
        bookRepository.add(b1);
        
        Assert.assertNotNull(b1.getId());
        b2 = bookRepository.get(b1);
        Assert.assertNotNull(b2);
        Assert.assertEquals(name, b2.getName());
        Assert.assertEquals(isbn, b2.getIsbn());
    }

}
