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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.domain.acme.FlatAuthor;
import net.sf.jkniv.whinstone.jdbc.domain.acme.FlatBook;
import net.sf.jkniv.whinstone.jdbc.domain.bank.Account;
import net.sf.jkniv.whinstone.jdbc.domain.bank.InsufficientFundsException;

//@Ignore("UnitOfWork in TreadLocal was lock database connection fix to HandlerCommad")
public class BankTransactionTest extends BaseJdbc
{
    @Autowired
    private Repository          repositoryDerby;
    @Rule
    public ExpectedException    catcher = ExpectedException.none();
    private int                 countBeforeTransaction;
    private List<Account>       accounts;
    private int                 allMoney;
    private static final String JOHN    = "John Lennon";
    private static final String PAUL    = "Paul McCartney";
    private static final String RINGO   = "Ringo Starr";
    private static final String GEORGE  = "George Harrison";
    
    @Before
    public void setUp()
    {
        Queryable q = QueryFactory.of("accounts");
        accounts = repositoryDerby.list(q);
        for (Account a : accounts)
        {
            allMoney += a.getBalance();
        }
    }
    
    @After
    public void tearDown()
    {
        Queryable q = QueryFactory.of("accounts");
        accounts = repositoryDerby.list(q);
        int checkMoney = 0;
        for (Account a : accounts)
        {
            System.out.println(a);
            checkMoney += a.getBalance();
            assertThat("No more money", a.getBalance(), greaterThanOrEqualTo(0));
        }
        assertThat("No more money", allMoney, equalTo(checkMoney));
    }
    
    @Test
    public void whenNoMovementAccount()
    {
        assertThat("There are 4 accounts", accounts.size(), equalTo(4));
    }
    
    @Test
    public void whenTransfer200Money()
    {
        repositoryDerby.getTransaction().begin();
        Account a = repositoryDerby.get(QueryFactory.of("accountsByName", "name", JOHN));
        Account b = repositoryDerby.get(QueryFactory.of("accountsByName", "name", PAUL));
        a.transfer(200, b);
        
        repositoryDerby.update(QueryFactory.of("update-balance", a));
        repositoryDerby.update(QueryFactory.of("update-balance", b));
        repositoryDerby.getTransaction().commit();
    }
    
    @Test
    public void whenTransferHaventFunds()
    {
        catcher.expect(InsufficientFundsException.class);
        try
        {
            repositoryDerby.getTransaction().begin();
            Account a = repositoryDerby.get(QueryFactory.of("accountsByName", "name", JOHN));
            Account b = repositoryDerby.get(QueryFactory.of("accountsByName", "name", PAUL));
            a.transfer(1200, b);
        }
        catch (InsufficientFundsException e)
        {
            repositoryDerby.getTransaction().rollback();
            throw e;
        }
    }
    
    @Test
    public void whenTransferMoney()
    {
        repositoryDerby.getTransaction().begin();
        Account a = repositoryDerby.get(QueryFactory.of("accountsByName", "name", JOHN));
        Account b = repositoryDerby.get(QueryFactory.of("accountsByName", "name", PAUL));
        Account c = repositoryDerby.get(QueryFactory.of("accountsByName", "name", RINGO));
        Account d = repositoryDerby.get(QueryFactory.of("accountsByName", "name", GEORGE));
        a.transfer(200, b);
        c.transfer(600, d);
        repositoryDerby.update(QueryFactory.of("update-balance", a));
        repositoryDerby.update(QueryFactory.of("update-balance", b));
        repositoryDerby.update(QueryFactory.of("update-balance", c));
        repositoryDerby.update(QueryFactory.of("update-balance", d));
        repositoryDerby.getTransaction().commit();
    }
    
    @Test
    public void whenTransferMoneyWithRollback()
    {
        repositoryDerby.getTransaction().begin();
        Account a = repositoryDerby.get(QueryFactory.of("accountsByName", "name", JOHN));
        Account b = repositoryDerby.get(QueryFactory.of("accountsByName", "name", PAUL));
        Account c = repositoryDerby.get(QueryFactory.of("accountsByName", "name", RINGO));
        Account d = repositoryDerby.get(QueryFactory.of("accountsByName", "name", GEORGE));
        a.transfer(200, b);
        c.transfer(600, d);
        repositoryDerby.update(QueryFactory.of("update-balance", a));
        repositoryDerby.update(QueryFactory.of("update-balance", b));
        repositoryDerby.getTransaction().commit();
        repositoryDerby.getTransaction().begin();
        repositoryDerby.update(QueryFactory.of("update-balance", c));
        repositoryDerby.update(QueryFactory.of("update-balance", d));
        repositoryDerby.getTransaction().rollback();
    }
    
}
