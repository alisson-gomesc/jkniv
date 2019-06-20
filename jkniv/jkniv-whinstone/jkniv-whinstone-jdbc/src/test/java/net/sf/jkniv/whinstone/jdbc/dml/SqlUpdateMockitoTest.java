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
package net.sf.jkniv.whinstone.jdbc.dml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.JdbcCommandMock;
import net.sf.jkniv.whinstone.jdbc.domain.acme.FlatBook;
import net.sf.jkniv.whinstone.transaction.TransactionStatus;

public class SqlUpdateMockitoTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();
    
    @Test
    public void whenUpdatingQueryable()
    {
        JdbcCommandMock jdbcMock = new JdbcCommandMock(FlatBook.class);
        Repository repository = jdbcMock.withUpdateable().getRepository();
        Queryable q = QueryFactory.of("update");
        int rows = repository.update(q);
        assertThat("1 row was affected", rows, equalTo(1));
        verifyMethods(jdbcMock, repository.getTransaction().getStatus(), 1, 1, 2);
    }
    
    @Test
    public void whenUpdatingEntity()
    {
        JdbcCommandMock jdbcMock = new JdbcCommandMock(FlatBook.class);
        Repository repository = jdbcMock.withUpdateable().getRepository();
        FlatBook book = new FlatBook();
        FlatBook result = repository.update(book);
        assertThat(result == book, is(true));
        verifyMethods(jdbcMock, repository.getTransaction().getStatus(), 1, 1, 2);
    }
    
    @Test
    public void whenBulkPojoCollection() throws SQLException
    {
        JdbcCommandMock jdbcMock = new JdbcCommandMock(FlatBook.class);
        Repository repository = jdbcMock.withUpdateable().getRepository();
        List<FlatBook> books = new ArrayList<FlatBook>();
        books.add(new FlatBook());
        books.add(new FlatBook());
        books.add(new FlatBook());
        books.add(new FlatBook());
        books.add(new FlatBook());
        
        int rows = repository.update(QueryFactory.of("update", books));
        assertThat("5 rows was affected", rows, equalTo(5));
        verify(jdbcMock.getStmt(), times(5)).executeUpdate();
        verify(jdbcMock.getStmt(), times(1)).close();
        verify(jdbcMock.getConnection(), times(2)).close();
    }
    
    @Test
    public void whenBulkMapCollection() throws SQLException
    {
        JdbcCommandMock jdbcMock = new JdbcCommandMock(FlatBook.class);
        Repository repository = jdbcMock.withUpdateable().getRepository();
        List<Map<String, String>> books = new ArrayList<Map<String, String>>();
        books.add(new HashMap<String, String>());
        books.add(new HashMap<String, String>());
        books.add(new HashMap<String, String>());
        books.add(new HashMap<String, String>());
        
        int rows = repository.update(QueryFactory.of("update", books));
        assertThat("4 rows was affected", rows, equalTo(4));
        verify(jdbcMock.getStmt(), times(4)).executeUpdate();
        verify(jdbcMock.getStmt(), times(1)).close();
        verify(jdbcMock.getConnection(), times(2)).close();
    }
    
    @Test
    public void whenBulkArrayCollection() throws SQLException
    {
        JdbcCommandMock jdbcMock = new JdbcCommandMock(FlatBook.class);
        Repository repository = jdbcMock.withUpdateable().getRepository();
        List<Object[]> books = new ArrayList<Object[]>();
        books.add(new String[]{"a","b"});
        books.add(new String[]{"a","b"});
        books.add(new String[]{"a","b"});
        books.add(new String[]{"a","b"});
        books.add(new String[]{"a","b"});
        books.add(new String[]{"a","b"});
        
        int rows = repository.update(QueryFactory.of("update", books));
        assertThat("6 rows was affected", rows, equalTo(6));
        verify(jdbcMock.getStmt(), times(6)).executeUpdate();
        verify(jdbcMock.getStmt(), times(1)).close();
        verify(jdbcMock.getConnection(), times(2)).close();
    }
    
    @Test
    public void whenBulkArrayMap() throws SQLException
    {
        JdbcCommandMock jdbcMock = new JdbcCommandMock(FlatBook.class);
        Repository repository = jdbcMock.withUpdateable().getRepository();
        Object[] books = new Object[3];
        books[0] = new HashMap<String, String>();
        books[1] = new HashMap<String, String>();
        books[2] = new HashMap<String, String>();
        
        int rows = repository.update(QueryFactory.ofArray("update", books));
        assertThat("3 rows was affected", rows, equalTo(3));
        verify(jdbcMock.getStmt(), times(3)).executeUpdate();
        verify(jdbcMock.getStmt(), times(1)).close();
        verify(jdbcMock.getConnection(), times(2)).close();
    }
    
    @Test
    public void whenBulkArrayPojo() throws SQLException
    {
        JdbcCommandMock jdbcMock = new JdbcCommandMock(FlatBook.class);
        Repository repository = jdbcMock.withUpdateable().getRepository();
        Object[] books = new Object[7];
        books[0] = new FlatBook();
        books[1] = new FlatBook();
        books[2] = new FlatBook();
        books[3] = new FlatBook();
        books[4] = new FlatBook();
        books[5] = new FlatBook();
        books[6] = new FlatBook();
        
        int rows = repository.update(QueryFactory.ofArray("update", books));
        assertThat("7 rows was affected", rows, equalTo(7));
        verify(jdbcMock.getStmt(), times(7)).executeUpdate();
        verify(jdbcMock.getStmt(), times(1)).close();
        verify(jdbcMock.getConnection(), times(2)).close();
    }
    
    private void verifyMethods(JdbcCommandMock jdbcMock, TransactionStatus status, int timesExecuteUpdate,
            int timesStmtClose, int timesConnClose)
    {
        try
        {
            assertThat(status, is(TransactionStatus.NO_TRANSACTION));
            verify(jdbcMock.getStmt(), times(timesExecuteUpdate)).close();
            verify(jdbcMock.getStmt(), times(timesStmtClose)).close();
            verify(jdbcMock.getConnection(), times(timesConnClose)).close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Cannot verify close methods for connection, statement and resultset", e);
        }
    }
}
