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
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatBook;

public class DriverManagerTransactionTest extends BaseJdbc
{
    @Autowired
    Repository repositoryDriverMngr;
    Queryable q;
    int countBeforeTransaction;
    
    @Before
    public void setUp()
    {
        q = QueryFactory.of("getBookByISBN");
        System.out.println("setUp->" + Thread.currentThread().getName());
        //repositoryOra.getTransaction().begin();
        countBeforeTransaction = repositoryDriverMngr.list(q).size();
        System.out.println("initial count="+countBeforeTransaction);
    }

    @After
    public void tearDown()
    {
        System.out.println("tearDown->" + Thread.currentThread().getName());
        int result = repositoryDriverMngr.list(q).size();
        System.out.println("result count="+result);
        assertThat(result, is(countBeforeTransaction+1));
    }

    @Test
    @Transactional
    @Commit
    public void whenRunDriverManagerTransaction()
    {
        System.out.println("whenRunDriverManagerTransaction->" + Thread.currentThread().getName());

        FlatBook book = new FlatBook();
        book.setId(System.currentTimeMillis());
        book.setAuthorId(1L);
        book.setIsbn("1000-005");
        book.setName("booik");
        repositoryDriverMngr.add(book);
    }
}
