package net.sf.jkniv.whinstone.jdbc;

//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.xml.NoSqlStats;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.params.ParamParserFactory;
import net.sf.jkniv.sqlegance.transaction.TransactionType;
import net.sf.jkniv.sqlegance.validation.ValidateType;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatBook;

public abstract class BaseMockito
{
    @Rule
    public ExpectedException    catcher = ExpectedException.none();
    
    protected DataSource        dataSource;
    protected Connection        connection;
    protected PreparedStatement stmt;
    protected ResultSet         rs;
    protected ResultSetMetaData rsMetadata;
    protected DatabaseMetaData  dbMetadata;
    
    protected RepositoryConfig  repositoryConfig;
    protected SqlContext        sqlContext;
    protected Sql               sql;
    protected Queryable         qqueryable;
    protected Repository        repository;
    
    //abstract protected void setUpResultSetMetaData(ResultSetMetaData rsMetadata) throws Exception;
    
    //abstract protected void setUpSql(Sql sql);
    
    @Before
    public void setUp() throws Exception
    {
        System.out.println("setting up mockito");
        this.qqueryable = mock(Queryable.class);
        this.connection = mock(Connection.class);
        this.dataSource = mock(DataSource.class);
        this.stmt = mock(PreparedStatement.class);
        this.rs = mock(ResultSet.class);
        this.rsMetadata = mock(ResultSetMetaData.class);
        this.dbMetadata = mock(DatabaseMetaData.class);
        this.repositoryConfig = mock(RepositoryConfig.class);
        this.sqlContext = mock(SqlContext.class);
        this.sql = mock(Selectable.class);
        
        //repositoryConfig.getJndiDataSource()
        //this.sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
        
        given(this.dataSource.getConnection()).willReturn(this.connection);
        given(this.connection.prepareStatement(anyString(), Mockito.anyInt(), Mockito.anyInt())).willReturn(this.stmt);
        given(this.stmt.executeQuery()).willReturn(this.rs);
        given(this.dbMetadata.getJDBCMajorVersion()).willReturn(1);
        given(this.dbMetadata.getJDBCMinorVersion()).willReturn(0);
        given(this.dbMetadata.getDriverName()).willReturn("MOCKITO");
        given(this.dbMetadata.getDriverVersion()).willReturn("1");
        
        //setUpResultSetMetaData(rsMetadata);
        
        given(this.rs.getMetaData()).willReturn(this.rsMetadata);
        
        given(this.repositoryConfig.getName()).willReturn("Mockito");
        given(this.repositoryConfig.lookup()).willReturn(this.dataSource);
        given(this.repositoryConfig.getJndiDataSource()).willReturn("jdbc/Mockito");
        given(this.repositoryConfig.getProperty(RepositoryProperty.JDBC_ADAPTER_FACTORY.key()))
                .willReturn(DataSourceAdapter.class.getName());
        given(this.repositoryConfig.getTransactionType()).willReturn(TransactionType.LOCAL);
        given(this.repositoryConfig.getQueryNameStrategy()).willReturn("net.sf.jkniv.sqlegance.HashQueryNameStrategy");
        
        given(this.sql.getValidateType()).willReturn(ValidateType.NONE);
        given(this.sql.getSql(any())).willReturn("any sql");
        given(this.sql.getSqlDialect()).willReturn(new AnsiDialect());
        given(this.sql.getParamParser()).willReturn(ParamParserFactory.getInstance(ParamMarkType.COLON));
        given(this.sql.getStats()).willReturn(NoSqlStats.getInstance());
        
        //setUpSql(sql);
        
        given(this.sqlContext.getRepositoryConfig()).willReturn(this.repositoryConfig);
        given(this.sqlContext.getQuery(anyString())).willReturn(this.sql);
        this.repository = RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance(sqlContext);
        
    }
}
