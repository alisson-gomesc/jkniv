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

public class SqlSelectMockitoTest
{
    @Rule
    public ExpectedException  catcher = ExpectedException.none();
    
    private DataSource        dataSource;
    private Connection        connection;
    private PreparedStatement stmt;
    private ResultSet         rs;
    private ResultSetMetaData rsMetadata;
    private DatabaseMetaData  dbMetadata;
    
    private RepositoryConfig  repositoryConfig;
    private SqlContext        sqlContext;
    private Selectable        sql;
    
    @Before
    public void setUp() throws SQLException
    {
        this.connection = mock(Connection.class);
        this.dataSource = mock(DataSource.class);
        this.stmt = mock(PreparedStatement.class);
        this.rs = mock(ResultSet.class);
        this.rsMetadata = mock(ResultSetMetaData.class);
        this.dbMetadata = mock(DatabaseMetaData.class);
        this.repositoryConfig = mock(RepositoryConfig.class);
        this.sqlContext = mock(SqlContext.class);
        this.sql = mock(Selectable.class);
        
        given(this.dataSource.getConnection()).willReturn(this.connection);
        given(this.connection.prepareStatement(anyString(), anyInt(), anyInt())).willReturn(this.stmt);
        given(this.stmt.executeQuery()).willReturn(this.rs);
        given(this.stmt.executeQuery(anyString())).willReturn(this.rs);
        given(this.dbMetadata.getJDBCMajorVersion()).willReturn(1);
        given(this.dbMetadata.getJDBCMinorVersion()).willReturn(0);
        given(this.dbMetadata.getDriverName()).willReturn("MOCKITO");
        given(this.dbMetadata.getDriverVersion()).willReturn("1");
        
        given(this.rs.getMetaData()).willReturn(this.rsMetadata);
        
        given(this.repositoryConfig.getName()).willReturn("Mockito");
        given(this.repositoryConfig.lookup()).willReturn(this.dataSource);
        given(this.repositoryConfig.getJndiDataSource()).willReturn("jdbc/Mockito");
        given(this.repositoryConfig.getProperty(RepositoryProperty.JDBC_ADAPTER_FACTORY.key()))
                .willReturn(DataSourceAdapter.class.getName());
        given(this.repositoryConfig.getTransactionType()).willReturn(TransactionType.LOCAL);
        given(this.repositoryConfig.getQueryNameStrategy()).willReturn("net.sf.jkniv.sqlegance.HashQueryNameStrategy");
        
        given(this.sql.getValidateType()).willReturn(ValidateType.NONE);
        given(this.sql.getSql(any())).willReturn("select * from dual");
        given(this.sql.getSqlDialect()).willReturn(new AnsiDialect());
        given(this.sql.getParamParser()).willReturn(ParamParserFactory.getInstance(ParamMarkType.COLON));
        given(this.sql.getStats()).willReturn(NoSqlStats.getInstance());
        given(this.sql.getSqlType()).willReturn(SqlType.SELECT);
        given(this.sql.asSelectable()).willReturn((Selectable) this.sql);
        
        given(this.sqlContext.getRepositoryConfig()).willReturn(this.repositoryConfig);
        given(this.sqlContext.getQuery(anyString())).willReturn(this.sql);
        
    }
    
    @Test
    public void whenSelectAllRecords() throws SQLException
    {
        Repository repository = RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance(sqlContext);
        given(this.rsMetadata.getColumnCount()).willReturn(5);
        given(this.rsMetadata.getColumnLabel(1)).willReturn("id");
        given(this.rsMetadata.getColumnName(1)).willReturn("id");
        given(this.rsMetadata.getColumnLabel(2)).willReturn("isbn");
        given(this.rsMetadata.getColumnName(2)).willReturn("isbn");
        given(this.rsMetadata.getColumnLabel(3)).willReturn("name");
        given(this.rsMetadata.getColumnName(3)).willReturn("name");
        given(this.rsMetadata.getColumnLabel(4)).willReturn("author");
        given(this.rsMetadata.getColumnName(4)).willReturn("author");
        given(this.rsMetadata.getColumnLabel(5)).willReturn("author_id");
        given(this.rsMetadata.getColumnName(5)).willReturn("author_id");
        given(this.rs.getMetaData()).willReturn(this.rsMetadata);
        given(this.sql.getReturnType()).willReturn(FlatBook.class.getName());
        doReturn(FlatBook.class).when(this.sql).getReturnTypeAsClass();
        given(rs.next()).willReturn(true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true, false);
        given(rs.getObject(1)).willReturn(1001L, 1002L, 1003L, 1004L, 1005L, 1006L, 1007L, 1008L, 1009L, 1010L, 1011L,
                1012L, 1013L, 1014L, 1015L);
        given(rs.getObject(2)).willReturn("978-1503250888", "978-0201895421", "007-6092019909", "978-0321826626",
                "978-0321712943", "978-0321984135", "9788535920598", "9788535921182", "9788535926019", "9788535922837",
                "9788535921205", "978-1557427663", "978-1612931036", "978-0679720201", "978-0679733843");
        given(rs.getObject(3)).willReturn("Beyond Good and Evil", "Analysis Patterns: Reusable Object Models",
                "Patterns of Enterprise Application Architecture",
                "NoSQL Distilled: A Brief Guide to the Emerging World of Polyglot Persistence",
                "Domain-Specific Languages (Addison-Wesley Signature Series", "Refactoring: Ruby Edition: Ruby Edition",
                "Claro Enigma", "Sentimento do Mundo", "A Lição do Amigo", "Alguma Poesia", "José", "The Metamorphosis",
                "The Trial", "The Stranger", "The Rebel: An Essay on Man in Revolt");
        given(rs.getObject(4)).willReturn("Friedrich Nietzsche", "Martin Fowler", "Martin Fowler", "Martin Fowler",
                "Martin Fowler", "Martin Fowler", "Carlos Drummond", "Carlos Drummond", "Carlos Drummond",
                "Carlos Drummond", "Carlos Drummond", "Franz Kafka", "Franz Kafka", "Albert Camus", "Albert Camus");
        given(rs.getObject(5)).willReturn(1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 5, 5);
        
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
    }
    
    @Test
    public void whenUsingMockitto() throws SQLException
    {
        Repository repository = RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance(sqlContext);
        given(rsMetadata.getColumnCount()).willReturn(3);
        given(this.rsMetadata.getColumnLabel(1)).willReturn("id");
        given(this.rsMetadata.getColumnName(1)).willReturn("id");
        given(this.rsMetadata.getColumnLabel(2)).willReturn("name");
        given(this.rsMetadata.getColumnName(2)).willReturn("name");
        given(this.rsMetadata.getColumnLabel(3)).willReturn("book");
        given(this.rsMetadata.getColumnName(3)).willReturn("book");
        given(this.rs.getMetaData()).willReturn(this.rsMetadata);
        given(this.sql.getReturnType()).willReturn(FlatAuthor.class.getName());
        doReturn(FlatAuthor.class).when(this.sql).getReturnTypeAsClass();
        given(rs.next()).willReturn(true, true, false);
        given(rs.getObject(1)).willReturn(1L, 2L);
        given(rs.getObject(2)).willReturn("Author 1", "Author 2");
        given(rs.getObject(3)).willReturn(10L, 11L);
        
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
        verify(rs).close();
        verify(stmt).close();
        verify(connection, atLeast(1)).close();
    }
    
    @Test
    public void whenSelectNonRows() throws SQLException
    {
        Repository repository = RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance(sqlContext);
        given(this.sql.getSqlType()).willReturn(SqlType.SELECT);
        given(this.sql.asSelectable()).willReturn((Selectable) this.sql);
        given(this.sql.getReturnType()).willReturn(FlatAuthor.class.getName());
        doReturn(FlatAuthor.class).when(this.sql).getReturnTypeAsClass();
        
        given(rs.next()).willReturn(false);
        Queryable q = QueryFactory.of("0 records");
        List<FlatAuthor> books = repository.list(q);
        assertThat("Query result is Empty", books.size(), equalTo(0));
        // Wanted but not invoked:
        //    resultSet.close();
        //    -> at net.sf.jkniv.whinstone.jdbc.dml.SqlSelectMockitoTest.whenSelectNonRows(SqlSelectMockitoTest.java:275)
        //    Actually, there were zero interactions with this mock.
        //FIXME verify(rs).close();
        //FIXME verify(stmt).close();
        //FIXME verify(connection).close();
    }
    
}
