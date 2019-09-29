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
package net.sf.jkniv.whinstone.jpa2.statement;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.acme.domain.orm.Book;

import net.sf.jkniv.cache.NoCache;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.xml.NoSqlStats;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.params.ParamParserFactory;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetHoldability;
import net.sf.jkniv.sqlegance.statement.ResultSetType;
import net.sf.jkniv.sqlegance.validation.ValidateType;
import net.sf.jkniv.whinstone.Queryable;

@SuppressWarnings("unchecked")
public class JpaStatementAdapterTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();

    private Query query;
    private Queryable queryable;
    private Selectable sql;
    
    @Before
    public void setUp()
    {
        Class<Book> returnType = Book.class;
        this.query = mock(Query.class);
        this.queryable = mock(Queryable.class);

        this.sql = mock(Selectable.class);
        given(this.sql.getValidateType()).willReturn(ValidateType.NONE);
        given(this.sql.getSql(any())).willReturn("select * from dual");
        given(this.sql.getSqlDialect()).willReturn(new AnsiDialect());
        given(this.sql.getParamParser()).willReturn(ParamParserFactory.getInstance(ParamMarkType.COLON));
        given(this.sql.getStats()).willReturn(NoSqlStats.getInstance());
        given(this.sql.getSqlType()).willReturn(SqlType.SELECT);
        given(this.sql.asSelectable()).willReturn((Selectable) this.sql);
        given(this.sql.getCache()).willReturn(NoCache.getInstance());
        given(this.sql.getResultSetType()).willReturn(ResultSetType.DEFAULT);
        given(this.sql.getResultSetConcurrency()).willReturn(ResultSetConcurrency.DEFAULT);
        given(this.sql.getResultSetHoldability()).willReturn(ResultSetHoldability.DEFAULT);

        
        given(this.queryable.getDynamicSql()).willReturn(this.sql);
        
        given(sql.getReturnType()).willReturn(returnType.getName());
        doReturn(returnType).when(sql).getReturnTypeAsClass();
    }
    

    @Test
    public void whenQueryReturnEmptyList()
    {
        List<Book> books = new ArrayList();
        given(query.getResultList()).willReturn(books);
        JpaStatementAdapter<Book, ResultSet>  stmt = new JpaStatementAdapter<Book, ResultSet>(query, queryable, getHandlerException());
        List<Book> answer = stmt.rows();
        assertThat(answer, notNullValue());
        assertThat(answer.size(), is(0));
    }
    
    @Test
    public void whenQueryReturnOneElement()
    {
        List<Book> books = Arrays.asList(new Book(1L, "Book 1", "ISBN1"));
        given(query.getResultList()).willReturn(books);
        JpaStatementAdapter<Book, ResultSet>  stmt = new JpaStatementAdapter<Book, ResultSet>(query, queryable, getHandlerException());
        List<Book> answer = stmt.rows();
        assertThat(answer, notNullValue());
        assertThat(answer.size(), is(1));
    }

    @Test
    public void whenQueryReturnTwoElements()
    {
        List<Book> books = Arrays.asList(new Book(1L, "Book 1", "ISBN1"), new Book(2L, "Book 2", "ISBN2"));
        given(query.getResultList()).willReturn(books);
        JpaStatementAdapter<Book, ResultSet>  stmt = new JpaStatementAdapter<Book, ResultSet>(query, queryable, getHandlerException());
        List<Book> answer = stmt.rows();
        assertThat(answer, notNullValue());
        assertThat(answer.size(), is(2));
    }
    
    @Test
    public void whenQueryReturnResultList()
    {
        List<Book> books = Arrays.asList(new Book(1L, "Book 1", "ISBN1"), new Book(2L, "Book 2", "ISBN2"), new Book(3L, "Book 3", "ISBN3"));
        given(query.getResultList()).willReturn(books);
        JpaStatementAdapter<Book, ResultSet>  stmt = new JpaStatementAdapter<Book, ResultSet>(query, queryable, getHandlerException());
        List<Book> answer = stmt.rows();
        assertThat(answer, notNullValue());
        assertThat(answer.size(), is(3));
    }

    @Test
    public void whenQueryReturnObjectWithNativeQuery()
    {
        List<Object[]> books = Arrays.asList(new Object[]{1L, "Book 1", "ISBN1"}, new Object[]{2L, "Book 2", "ISBN2"});
        given(query.getResultList()).willReturn(books);
        given(this.sql.getLanguageType()).willReturn(LanguageType.NATIVE);
        given(this.sql.getReturnType()).willReturn(Book.class.getName());
        given(this.sql.hasReturnType()).willReturn(true);
        
        JpaStatementAdapter<Book, ResultSet>  stmt = new JpaStatementAdapter<Book, ResultSet>(query, queryable, getHandlerException());
        List<Book> answer = stmt.rows();
        
        assertThat(answer, notNullValue());
        assertThat(answer.size(), is(2));
        
        assertThat(answer.get(0), instanceOf(Book.class));
        assertThat(answer.get(0).getId(), is(1L));
        assertThat(answer.get(0).getName(), is("Book 1"));
        assertThat(answer.get(0).getIsbn(), is("ISBN1"));
        
        assertThat(answer.get(1).getId(), is(2L));
        assertThat(answer.get(1).getName(), is("Book 2"));
        assertThat(answer.get(1).getIsbn(), is("ISBN2"));
    }

    @Test
    public void whenThrowIllegalStateException()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("Cannot call SELECT statement with UPDATE or DELETE: Hit IllegalStateException");
        given(query.getResultList()).willThrow(new IllegalStateException("Hit IllegalStateException"));
        JpaStatementAdapter<Number, ResultSet>  stmt = new JpaStatementAdapter<Number, ResultSet>(query, queryable, getHandlerException());
        stmt.rows();
    }

    @Test
    public void whenThrowQueryTimeoutException()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("A query times out and only the statement is rolled back, if a current transaction is active, will be not be marked for rollback: Hit QueryTimeoutException");
        given(query.getResultList()).willThrow(new QueryTimeoutException("Hit QueryTimeoutException"));
        JpaStatementAdapter<Number, ResultSet>  stmt = new JpaStatementAdapter<Number, ResultSet>(query, queryable, getHandlerException());
        stmt.rows();
    }

    @Test
    public void whenThrowTransactionRequiredException()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("A transaction is required but is not active: Hit TransactionRequiredException");
        given(query.getResultList()).willThrow(new TransactionRequiredException("Hit TransactionRequiredException"));
        JpaStatementAdapter<Number, ResultSet>  stmt = new JpaStatementAdapter<Number, ResultSet>(query, queryable, getHandlerException());
        stmt.rows();
    }

    @Test
    public void whenThrowPessimisticLockException()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("A pessimistic locking conflict occurs if a current transaction is active, will be not be marked for rollback: Hit PessimisticLockException");
        given(query.getResultList()).willThrow(new PessimisticLockException("Hit PessimisticLockException"));
        JpaStatementAdapter<Number, ResultSet>  stmt = new JpaStatementAdapter<Number, ResultSet>(query, queryable, getHandlerException());
        stmt.rows();
    }

    @Test
    public void whenThrowLockTimeoutException()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("A pessimistic locking conflict occurs if a current transaction is active, will be not be marked for rollback: Hit LockTimeoutException");
        given(query.getResultList()).willThrow(new LockTimeoutException("Hit LockTimeoutException"));
        JpaStatementAdapter<Number, ResultSet>  stmt = new JpaStatementAdapter<Number, ResultSet>(query, queryable, getHandlerException());
        stmt.rows();
    }

    @Test
    public void whenThrowPersistenceException()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("The query exceeds the timeout value: Hit PersistenceException");
        given(query.getResultList()).willThrow(new PersistenceException("Hit PersistenceException"));
        JpaStatementAdapter<Number, ResultSet>  stmt = new JpaStatementAdapter<Number, ResultSet>(query, queryable, getHandlerException());
        stmt.rows();
    }

    
    private HandleableException getHandlerException()
    {
        HandleableException handlerException = new HandlerException(RepositoryException.class, "JPA Error cannot execute SQL [%s]");
       // JPA 2 throws Exceptions
       /* @throws IllegalStateException if called for a Java
        * Persistence query language UPDATE or DELETE statement */
        handlerException.config(IllegalStateException.class, "Cannot call SELECT statement with UPDATE or DELETE: %s");
        
       /* @throws QueryTimeoutException if the query execution exceeds
        * the query timeout value set and only the statement is rolled back */
        handlerException.config(QueryTimeoutException.class, "A query times out and only the statement is rolled back, if a current transaction is active, will be not be marked for rollback: %s");
       
       /* @throws TransactionRequiredException if a lock mode other than NONE has been been set
        *  and there is no transaction or the persistence context has not been joined to the transaction */
        handlerException.config(TransactionRequiredException.class, "A transaction is required but is not active: %s");
        
       /* @throws PessimisticLockException if pessimistic locking fails and the transaction is rolled back */
        handlerException.config(PessimisticLockException.class, "A pessimistic locking conflict occurs if a current transaction is active, will be not be marked for rollback: %s");
        
       /* @throws LockTimeoutException if pessimistic locking fails and only the statement is rolled back */
        handlerException.config(LockTimeoutException.class, "A pessimistic locking conflict occurs if a current transaction is active, will be not be marked for rollback: %s");
        //pessimistic locking
       /* @throws PersistenceException if the query execution exceeds the query timeout */
        handlerException.config(PersistenceException.class, "The query exceeds the timeout value: %s");
        
        return handlerException;
    }    

}

