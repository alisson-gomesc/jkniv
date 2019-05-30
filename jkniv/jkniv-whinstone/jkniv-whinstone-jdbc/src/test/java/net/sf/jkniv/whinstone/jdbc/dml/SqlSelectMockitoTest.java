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

import static net.sf.jkniv.whinstone.jdbc.RepositoryJdbcInstanceTest.TOTAL_BOOKS;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.xml.NoSqlStats;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.params.ParamParserColonMark;
import net.sf.jkniv.sqlegance.params.ParamParserFactory;
import net.sf.jkniv.sqlegance.transaction.TransactionType;
import net.sf.jkniv.sqlegance.validation.ValidateType;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.BaseMockito;
import net.sf.jkniv.whinstone.jdbc.CustomResultRow;
import net.sf.jkniv.whinstone.jdbc.DataSourceAdapter;
import net.sf.jkniv.whinstone.jdbc.acme.domain.Book;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatAuthor;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatBook;

public class SqlSelectMockitoTest extends BaseMockito
{
//    @Rule
//    public ExpectedException catcher = ExpectedException.none();  
//
//    private DataSource dataSource;
//    private Connection connection;
//    private PreparedStatement stmt;
//    private ResultSet rs;
//    private ResultSetMetaData rsMetadata;
//    private DatabaseMetaData dbMetadata;
//    
//    private RepositoryConfig repositoryConfig;
//    private SqlContext sqlContext;
//    private Selectable selectable;
//    private Queryable qqueryable;
//    private Repository repository;
    private List<FlatBook> bbooks;
    
    protected void setUpResultSetMetaData(ResultSetMetaData rsMetadata) throws SQLException  {
        given(this.rsMetadata.getColumnCount()).willReturn(3);
        given(this.rsMetadata.getColumnLabel(1)).willReturn("id");
        given(this.rsMetadata.getColumnName(1)).willReturn("id");
        given(this.rsMetadata.getColumnLabel(2)).willReturn("name");
        given(this.rsMetadata.getColumnName(2)).willReturn("name");
        given(this.rsMetadata.getColumnLabel(3)).willReturn("book");
        given(this.rsMetadata.getColumnName(3)).willReturn("book");
    }
    
    protected void setUpSql(Sql sql) {
        given(this.sql.getSqlType()).willReturn(SqlType.SELECT);
        given(this.sql.asSelectable()).willReturn((Selectable)this.sql);
        given(this.sql.getReturnType()).willReturn(FlatAuthor.class.getName());
        doReturn(FlatAuthor.class).when(this.sql).getReturnTypeAsClass();

    }

    
//    @Before
//    public void setUp() throws Exception 
//    {
//        this.qqueryable = mock(Queryable.class);        
//        this.connection = mock(Connection.class);
//        this.dataSource = mock(DataSource.class);
//        this.stmt = mock(PreparedStatement.class);
//        this.rs = mock(ResultSet.class);
//        this.rsMetadata = mock(ResultSetMetaData.class);
//        this.dbMetadata = mock(DatabaseMetaData.class);
//        this.repositoryConfig = mock(RepositoryConfig.class);
//        this.sqlContext = mock(SqlContext.class);
//        this.selectable = mock(Selectable.class);
//        
//        //repositoryConfig.getJndiDataSource()
//        //this.sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
        
//        given(this.dataSource.getConnection()).willReturn(this.connection);
//        given(this.connection.prepareStatement(anyString(),  Mockito.anyInt(), Mockito.anyInt())).willReturn(this.stmt);
//        given(this.stmt.executeQuery()).willReturn(this.rs);
//        given(this.dbMetadata.getJDBCMajorVersion()).willReturn(1);
//        given(this.dbMetadata.getJDBCMinorVersion()).willReturn(0);
//        given(this.dbMetadata.getDriverName()).willReturn("MOCKITO");
//        given(this.dbMetadata.getDriverVersion()).willReturn("1");

//        //given(this.rsMetadata.getColumnType(1)).willReturn(java.sql.Types.BIGINT);
//        //given(this.rsMetadata.getColumnType(2)).willReturn(java.sql.Types.VARCHAR);
//        //given(this.rsMetadata.getColumnType(3)).willReturn(java.sql.Types.BIGINT);
//        given(this.rs.getMetaData()).willReturn(this.rsMetadata);
//        
//        given(this.repositoryConfig.getName()).willReturn("Mockito");
//        given(this.repositoryConfig.lookup()).willReturn(this.dataSource);
//        given(this.repositoryConfig.getJndiDataSource()).willReturn("jdbc/Mockito");
//        given(this.repositoryConfig.getProperty(RepositoryProperty.JDBC_ADAPTER_FACTORY.key())).willReturn(DataSourceAdapter.class.getName());
//        given(this.repositoryConfig.getTransactionType()).willReturn(TransactionType.LOCAL);
//        given(this.repositoryConfig.getQueryNameStrategy()).willReturn("net.sf.jkniv.sqlegance.HashQueryNameStrategy");
        
//        
//        given(this.selectable.getSqlType()).willReturn(SqlType.SELECT);
//        given(this.selectable.asSelectable()).willReturn(this.selectable);
//        given(this.selectable.getValidateType()).willReturn(ValidateType.NONE);
//        given(this.selectable.getSql(any())).willReturn("any sql");
//        given(this.selectable.getSqlDialect()).willReturn(new AnsiDialect());
//        given(this.selectable.getParamParser()).willReturn(ParamParserFactory.getInstance(ParamMarkType.COLON));
//        given(this.selectable.getStats()).willReturn(NoSqlStats.getInstance());
//        given(this.selectable.getReturnType()).willReturn(FlatAuthor.class.getName());
//        //given(this.selectable.getReturnTypeAsClass()).willReturn(FlatAuthor.class);
//        doReturn(FlatAuthor.class).when(this.selectable).getReturnTypeAsClass();
//        given(this.sqlContext.getRepositoryConfig()).willReturn(this.repositoryConfig);
//        given(this.sqlContext.getQuery(anyString())).willReturn(this.selectable);
//        
//        this.repository = RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance(sqlContext);

        //doReturn(bbooks).when(repository).list(qqueryable);
//    }
    
    @Test
    public void whenUsingMockitto() throws SQLException
    {
        setUpResultSetMetaData(rsMetadata);
        setUpSql(this.sql);
        given(this.rs.next()).willReturn(true, true, false);
        given(this.rs.getObject(1)).willReturn(1L, 2L);
        given(this.rs.getObject(2)).willReturn("Author 1", "Author 2");
        given(this.rs.getObject(3)).willReturn(10L, 11L);
        
        Queryable q = QueryFactory.of("getBookByISBN");
        List<FlatAuthor> books = repository.list(q);
        Assert.assertTrue(books.size() == 2);
        Assert.assertTrue(books.get(0) instanceof FlatAuthor);
        for (FlatAuthor a : books)
        {
            assertThat(a.getName(), notNullValue());
            assertThat(a.getId(), notNullValue());
            assertThat(a.getBookId(), notNullValue());
        }
    }

    
}
