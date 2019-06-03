package net.sf.jkniv.whinstone.jdbc.dml;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

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

public class MockitoSample
{
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
    
    @After
    public void tearDown() {
        this.dataSource = null;
        this.connection = null;
        this.stmt = null;
        this.rs = null;
        this.rsMetadata = null;
        this.dbMetadata = null;
        this.repositoryConfig = null;
        this.sqlContext = null;
        this.sql = null;
    }
    
    @Test
    public void whenSelectA() throws SQLException
    {
        Repository repository = RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance(sqlContext);
        given(this.rsMetadata.getColumnCount()).willReturn(2);
        given(this.rsMetadata.getColumnLabel(1)).willReturn("id");
        given(this.rsMetadata.getColumnName(1)).willReturn("id");
        given(this.rsMetadata.getColumnLabel(2)).willReturn("name");
        given(this.rsMetadata.getColumnName(2)).willReturn("name");
        given(this.rs.getMetaData()).willReturn(this.rsMetadata);
        given(this.sql.getReturnType()).willReturn(FlatBook.class.getName());
        doReturn(FlatBook.class).when(this.sql).getReturnTypeAsClass();
        given(rs.next()).willReturn(true, true, false);
        given(rs.getObject(1)).willReturn(1001L, 1002L);
        given(rs.getObject(2)).willReturn("Beyond Good and Evil", "The Rebel: An Essay on Man in Revolt");
        
        Queryable q = QueryFactory.of("2 FlatBook");
        List<FlatBook> books = repository.list(q);
        assertThat("There are 2 rows", books.size(), equalTo(2));
        assertThat("Row is a FlatBook object", books.get(0), instanceOf(FlatBook.class));
        for (FlatBook b : books)
        {
            assertThat(b.getId(), notNullValue());
            assertThat(b.getName(), notNullValue());
        }
    }
    
    @Test
    public void whenSelectB() throws SQLException
    {
        Repository repository = RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance(sqlContext);
        given(rsMetadata.getColumnCount()).willReturn(2);
        given(this.rsMetadata.getColumnLabel(1)).willReturn("id");
        given(this.rsMetadata.getColumnName(1)).willReturn("id");
        given(this.rsMetadata.getColumnLabel(2)).willReturn("name");
        given(this.rsMetadata.getColumnName(2)).willReturn("name");
        given(this.rs.getMetaData()).willReturn(this.rsMetadata);
        given(this.sql.getReturnType()).willReturn(FlatAuthor.class.getName());
        doReturn(FlatAuthor.class).when(this.sql).getReturnTypeAsClass();
        given(rs.next()).willReturn(true, true, false);
        given(rs.getObject(1)).willReturn(1L, 2L);
        given(rs.getObject(2)).willReturn("Author 1", "Author 2");
        
        Queryable q = QueryFactory.of("2 FlatAuthor");
        List<FlatAuthor> books = repository.list(q);
        assertThat("There are 2 rows", books.size(), equalTo(2));
        assertThat("Row is a FlatAuthor object", books.get(0), instanceOf(FlatAuthor.class));
        for (FlatAuthor a : books)
        {
            assertThat(a.getId(), notNullValue());
            assertThat(a.getName(), notNullValue());
        }
        verify(rs).close();
        verify(stmt).close();
        verify(connection, atLeast(1)).close();
    }   
}