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
package net.sf.jkniv.whinstone.jdbc.tx;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatBook;

public class DataSourceTransactionTest extends BaseJdbc
{
    @Autowired
    Repository repositoryDerby;
    Queryable q;
    int countBeforeTransaction;
    
    @Before
    public void setUp()
    {
        q = QueryFactory.newInstance("getBookByISBN");
        System.out.println("setUp->" + Thread.currentThread().getName());
        repositoryDerby.getTransaction().begin();
        countBeforeTransaction = repositoryDerby.list(q).size();
        System.out.println("initial="+countBeforeTransaction);
    }

    @After
    public void tearDown()
    {
        System.out.println("tearDown->" + Thread.currentThread().getName());
        int result = repositoryDerby.list(q).size();
        
        assertThat(countBeforeTransaction+1, is(result));
        
        repositoryDerby.getTransaction().rollback();
        
        result = repositoryDerby.list(q).size();
        
        assertThat(countBeforeTransaction, is(result));
    }

    @Test
    public void whenRunDataSourceTransaction()
    {
        System.out.println("whenRunDataSourceTransaction->" + Thread.currentThread().getName());

        FlatBook book = new FlatBook();
        book.setId(System.currentTimeMillis());
        book.setAuthorId(1L);
        book.setIsbn("1000-005");
        book.setName("booik");
        repositoryDerby.add(book);
    }
}
