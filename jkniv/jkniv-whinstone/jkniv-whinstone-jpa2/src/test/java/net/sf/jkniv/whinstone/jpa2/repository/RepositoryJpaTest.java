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

import java.util.List;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.acme.domain.orm.Book;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

@SuppressWarnings("unused")
public class RepositoryJpaTest extends BaseTest
{
    @Test
    @Transactional
    public void springInjection()
    {
        Repository repository = getRepository();
        List<Book> list = repository.list(newQuery());
        repository.flush();
    }

    @Test
    @Transactional
    public void constructorDefault()
    {
        Repository repository = getRepository();

        List<Book> list = repository.list(newQuery());
        repository.flush();
    }
    
    @Test
    @Transactional
    public void constructorString()
    {
        Repository repository = getRepository();

        List<Book> list = repository.list(newQuery());
        repository.flush();
    }
    
    @Test
    @Transactional
    public void constructorEntityManager()
    {
        Repository repository = getRepository();
        List<Book> list = repository.list(newQuery());
        repository.flush();
    }
    
    private Queryable newQuery()
    {
        Queryable query = QueryFactory.newInstance("Book.list");
        return query;
    }
   
}
