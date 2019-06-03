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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeast;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.xml.NoSqlStats;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.params.ParamParserFactory;
import net.sf.jkniv.sqlegance.transaction.TransactionType;
import net.sf.jkniv.sqlegance.validation.ValidateType;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.jdbc.DataSourceAdapter;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatAuthor;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatBook;
import net.sf.jkniv.whinstone.transaction.TransactionSessions;
import net.sf.jkniv.whinstone.transaction.TransactionStatus;

public class SqlSelectMockitoTest
{
    @Rule
    public ExpectedException  catcher = ExpectedException.none();
    
    @Test
    public void whenSelectAllRecords() throws SQLException
    {
        JdbcMock jdbcMock = new JdbcMock(FlatBook.class);
        Repository repository = jdbcMock.columns(new String[]{"id","isbn","name","author","author_id"}).buildFifteenFlatBook();
        Queryable q = QueryFactory.of("15 FlatBook");
        List<FlatBook> books = repository.list(q);
        assertThat("There are 15 rows", books.size(), equalTo(15));
        assertThat("Row is a FlatBook object", books.get(0), instanceOf(FlatBook.class));
        for (FlatBook b : books)
        {
            assertThat(b.getAuthor(), notNullValue());
            assertThat(b.getAuthorId(), notNullValue());
            assertThat(b.getId(), notNullValue());
            assertThat(b.getIsbn(), notNullValue());
            assertThat(b.getName(), notNullValue());
        }
        assertThat(repository.getTransaction().getStatus(), is(TransactionStatus.NO_TRANSACTION));
        verify(jdbcMock.getRs()).close();
        verify(jdbcMock.getStmt()).close();
        verify(jdbcMock.getConnection(), atLeast(1)).close();
    }
    
    @Test
    public void whenUsingMockitto() throws SQLException
    {
        JdbcMock jdbcMock = new JdbcMock(FlatAuthor.class);
        Repository repository = jdbcMock.columns(new String[]{"id","name","book"}).buildThreeFlatAuthor();

        Queryable q = QueryFactory.of("2 FlatAuthor");
        List<FlatAuthor> books = repository.list(q);
        assertThat("There are 2 rows", books.size(), equalTo(2));
        assertThat("Row is a FlatAuthor object", books.get(0), instanceOf(FlatAuthor.class));
        for (FlatAuthor a : books)
        {
            assertThat(a.getName(), notNullValue());
            assertThat(a.getId(), notNullValue());
            assertThat(a.getBookId(), notNullValue());
        }
        assertThat(repository.getTransaction().getStatus(), is(TransactionStatus.NO_TRANSACTION));
        verify(jdbcMock.getRs()).close();
        verify(jdbcMock.getStmt()).close();
        verify(jdbcMock.getConnection(), atLeast(1)).close();
    }
    
    @Test
    public void whenSelectNonRows() throws SQLException
    {
        JdbcMock jdbcMock = new JdbcMock(FlatAuthor.class);
        Repository repository = jdbcMock.columns(new String[]{"id","name","book"}).getRepository();

        Queryable q = QueryFactory.of("0 records");
        List<FlatAuthor> books = repository.list(q);
        assertThat("Query result is Empty", books.size(), equalTo(0));
        assertThat(repository.getTransaction().getStatus(), is(TransactionStatus.NO_TRANSACTION));
        verify(jdbcMock.getRs()).close();
        verify(jdbcMock.getStmt()).close();
        verify(jdbcMock.getConnection(), atLeast(1)).close();
    }
    
}
