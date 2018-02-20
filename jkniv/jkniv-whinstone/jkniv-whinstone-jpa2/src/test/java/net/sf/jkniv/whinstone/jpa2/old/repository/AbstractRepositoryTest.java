/*
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

import junit.framework.Assert;
import net.sf.jkniv.sqlegance.IQuery;
import net.sf.jkniv.sqlegance.Query;
import net.sf.jkniv.whinstone.jpa2.AuthorRepository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;
import net.sf.jkniv.whinstone.jpa2.BookRepository;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.acme.domain.flat.AuthorFlat;
import com.acme.domain.orm.Author;
import com.acme.domain.orm.Book;

@Ignore("AbstractRepository protected level")
public class AbstractRepositoryTest extends BaseTest
{
    //@Autowired
    BookRepository   bookRepository;
    
    //@Autowired
    AuthorRepository authorRepository;   

    @Test
    @Transactional
    public void constructorDefault()
    {
        BookRepository repo = new BookRepository();
        List<Book> list = repo.list();
        repo.flush();
    }
    
    @Test
    @Transactional
    public void constructorString()
    {
        BookRepository repo = new BookRepository("whinstone");
        List<Book> list = repo.list();
        repo.flush();
    }
    
    @Test
    @Transactional
    public void constructorEntityManager()
    {
        BookRepository repo = new BookRepository(em);
        List<Book> list = repo.list();
        repo.flush();
    }
}
