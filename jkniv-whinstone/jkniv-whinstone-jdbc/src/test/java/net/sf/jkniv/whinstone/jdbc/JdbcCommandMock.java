package net.sf.jkniv.whinstone.jdbc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import net.sf.jkniv.cache.NoCache;
import net.sf.jkniv.sqlegance.Deletable;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.Updateable;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.xml.NoSqlStats;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.sqlegance.params.ParamParserFactory;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetHoldability;
import net.sf.jkniv.sqlegance.statement.ResultSetType;
import net.sf.jkniv.sqlegance.transaction.TransactionType;
import net.sf.jkniv.sqlegance.validation.ValidateType;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;

public class JdbcCommandMock
{
    private DataSource        dataSource;
    private Connection        connection;
    private PreparedStatement stmt;
    private ResultSetMetaData rsMetadata;
    private DatabaseMetaData  dbMetadata;
    
    private RepositoryConfig  repositoryConfig;
    private SqlContext        sqlContext;
    private Sql               sql;
    private Repository        repository;
    private Class<?>          returnType;
    private ParamParser       paramParser;
    
    public JdbcCommandMock(Class<?> returnType)
    {
        this.returnType = returnType;
        this.connection = mock(Connection.class);
        this.dataSource = mock(DataSource.class);
        this.stmt = mock(PreparedStatement.class);
        this.rsMetadata = mock(ResultSetMetaData.class);
        this.dbMetadata = mock(DatabaseMetaData.class);
        this.repositoryConfig = mock(RepositoryConfig.class);
        this.sqlContext = mock(SqlContext.class);
        this.paramParser= mock(ParamParser.class);
        try
        {
            given(this.dataSource.getConnection()).willReturn(this.connection);
            given(this.connection.getAutoCommit()).willReturn(true);
            given(this.connection.prepareStatement(anyString(), anyInt(), anyInt())).willReturn(this.stmt);
            given(this.connection.prepareStatement(anyString(), anyInt(), anyInt(), anyInt())).willReturn(this.stmt);
            
            given(this.stmt.executeUpdate()).willReturn(1);
            given(this.stmt.execute()).willReturn(true);
            given(this.dbMetadata.getJDBCMajorVersion()).willReturn(1);
            given(this.dbMetadata.getJDBCMinorVersion()).willReturn(0);
            given(this.dbMetadata.getDriverName()).willReturn("MOCKITO");
            given(this.dbMetadata.getDriverVersion()).willReturn("1");
            
            //given(this.rs.getMetaData()).willReturn(this.rsMetadata);
            Properties props = new Properties();
            props.put(RepositoryProperty.JDBC_ADAPTER_FACTORY.key(), DataSourceAdapter.class.getName());
            given(this.repositoryConfig.getName()).willReturn("Mockito");
            given(this.repositoryConfig.getProperties()).willReturn(props);
            given(this.repositoryConfig.lookup()).willReturn(this.dataSource);
            given(this.repositoryConfig.getJndiDataSource()).willReturn("jdbc/Mockito");
            given(this.repositoryConfig.getProperty(RepositoryProperty.JDBC_ADAPTER_FACTORY.key()))
                    .willReturn(DataSourceAdapter.class.getName());
            given(this.repositoryConfig.getTransactionType()).willReturn(TransactionType.LOCAL);
            given(this.repositoryConfig.getQueryNameStrategy())
                    .willReturn("net.sf.jkniv.sqlegance.HashQueryNameStrategy");
            
            given(this.sqlContext.getRepositoryConfig()).willReturn(this.repositoryConfig);
            
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Cannot setUp mockito assumptions", e);
        }
        this.repository = RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance(sqlContext);
    }
    
    /**
     * Build the JDBC ResultSetMetaData 
     * @param columns array of columns like {id, name, postal_code, street_name}
     * @return this builder instance
     * @throws SQLException 
     */
    public JdbcCommandMock columns(String[] columns)
    {
        try
        {
            given(rsMetadata.getColumnCount()).willReturn(columns.length);
            for (int i = 1; i <= columns.length; i++)
            {
                given(rsMetadata.getColumnLabel(i)).willReturn(columns[i - 1]);
                given(rsMetadata.getColumnName(i)).willReturn(columns[i - 1]);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Cannot build the colummns for data records", e);
        }
        return this;
    }
    
    public Repository getRepository()
    {
        return repository;
    }

    public JdbcCommandMock withSelectable()
    {
        this.sql = mock(Selectable.class);
        given(this.sql.getSqlType()).willReturn(SqlType.SELECT);
        given(this.sql.asSelectable()).willReturn((Selectable) this.sql);
        given(this.sql.getSql(any())).willReturn("select id, name from t ");
        configInternalSql();
        return this;
    }

    public JdbcCommandMock withInsertable()
    {
        this.sql = mock(Insertable.class);
        given(this.sql.getSqlType()).willReturn(SqlType.INSERT);
        given(this.sql.asInsertable()).willReturn((Insertable) this.sql);
        given(this.sql.getSql(any())).willReturn("insert into t (id,name) values(:id,:name)");
        configInternalSql();
        return this;
    }
    
    public JdbcCommandMock withUpdateable()
    {
        this.sql = mock(Updateable.class);
        given(this.sql.getSqlType()).willReturn(SqlType.UPDATE);
        given(this.sql.asUpdateable()).willReturn((Updateable) this.sql);
        given(this.sql.getSql(any())).willReturn("update t set name=:name where name=:name");
        configInternalSql();
        return this;
    }
    
    public JdbcCommandMock withDeletable()
    {
        this.sql = mock(Deletable.class);
        given(this.sql.getSqlType()).willReturn(SqlType.DELETE);
        given(this.sql.asDeletable()).willReturn((Deletable) this.sql);
        given(this.sql.getSql(any())).willReturn("delete from t where id = :id");
        //given(this.sql.getParamParser()).willReturn(this.paramParser);
        configInternalSql();
        return this;
    }

    private void configInternalSql() {
        given(this.sql.getValidateType()).willReturn(ValidateType.NONE);
        given(this.sql.getSqlDialect()).willReturn(new AnsiDialect());
        given(this.sql.getParamParser()).willReturn(ParamParserFactory.getInstance(ParamMarkType.COLON));
        given(this.sql.getStats()).willReturn(NoSqlStats.getInstance());
        given(this.sql.getResultSetType()).willReturn(ResultSetType.DEFAULT);
        given(this.sql.getResultSetConcurrency()).willReturn(ResultSetConcurrency.DEFAULT);
        given(this.sql.getResultSetHoldability()).willReturn(ResultSetHoldability.DEFAULT);
        //given(this.sql.getCache()).willReturn(NoCache.getInstance());
        
        //given(this.paramParser.find(anyString())).willReturn(new String[]{"id","name"});
        
        given(sql.getReturnType()).willReturn(returnType.getName());
        doReturn(returnType).when(sql).getReturnTypeAsClass();
        given(this.sqlContext.getQuery(anyString())).willReturn(this.sql);

    }

    
    public JdbcCommandMock withAutoCommit(boolean autoCommit)
    {
        try
        {
            given(this.connection.getAutoCommit()).willReturn(autoCommit);
        }
        catch (SQLException e)
        {
            throw new RepositoryException(e);
        }
        return this;
    }

    /*
    public Repository buildFifteenFlatBook()
    {
        try
        {
            given(rs.next()).willReturn(true, true, true, true, true, true, true, true, true, true, true, true, true,
                    true, true, false);
            given(rs.getObject(1)).willReturn(1001L, 1002L, 1003L, 1004L, 1005L, 1006L, 1007L, 1008L, 1009L, 1010L,
                    1011L, 1012L, 1013L, 1014L, 1015L);
            given(rs.getObject(2)).willReturn("978-1503250888", "978-0201895421", "007-6092019909", "978-0321826626",
                    "978-0321712943", "978-0321984135", "9788535920598", "9788535921182", "9788535926019",
                    "9788535922837", "9788535921205", "978-1557427663", "978-1612931036", "978-0679720201",
                    "978-0679733843");
            given(rs.getObject(3)).willReturn("Beyond Good and Evil", "Analysis Patterns: Reusable Object Models",
                    "Patterns of Enterprise Application Architecture",
                    "NoSQL Distilled: A Brief Guide to the Emerging World of Polyglot Persistence",
                    "Domain-Specific Languages (Addison-Wesley Signature Series",
                    "Refactoring: Ruby Edition: Ruby Edition", "Claro Enigma", "Sentimento do Mundo",
                    "A Lição do Amigo", "Alguma Poesia", "José", "The Metamorphosis", "The Trial", "The Stranger",
                    "The Rebel: An Essay on Man in Revolt");
            given(rs.getObject(4)).willReturn("Friedrich Nietzsche", "Martin Fowler", "Martin Fowler", "Martin Fowler",
                    "Martin Fowler", "Martin Fowler", "Carlos Drummond", "Carlos Drummond", "Carlos Drummond",
                    "Carlos Drummond", "Carlos Drummond", "Franz Kafka", "Franz Kafka", "Albert Camus", "Albert Camus");
            given(rs.getObject(5)).willReturn(1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 5, 5);
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Cannot build data for tests", e);
        }
        //"ID", "ISBN", "NAME", "AUTHOR", "AUTHOR_ID"
        return this.repository;
    }

    
    public Repository buildOneFlatBook()
    {
        try
        {
            given(rs.next()).willReturn(true,false);
            given(rs.getObject(1)).willReturn(1001L);
            given(rs.getObject(2)).willReturn("978-1503250888");
            given(rs.getObject(3)).willReturn("Beyond Good and Evil");
            given(rs.getObject(4)).willReturn("Friedrich Nietzsche");
            given(rs.getObject(5)).willReturn(1);
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Cannot build data for tests", e);
        }
        //"ID", "ISBN", "NAME", "AUTHOR", "AUTHOR_ID"
        return this.repository;
    }

    public Repository buildThreeFlatAuthor()
    {
        try
        {
            given(rs.next()).willReturn(true, true, false);
            given(rs.getObject(1)).willReturn(1L, 2L);
            given(rs.getObject(2)).willReturn("Author 1", "Author 2");
            given(rs.getObject(3)).willReturn(10L, 11L);
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Cannot build data for tests", e);
        }
        
        return this.repository;
    }
     */
    
    public PreparedStatement getStmt()
    {
        return stmt;
    }
        
    public Connection getConnection()
    {
        return connection;
    }
    
    public Sql withSql()
    {
        return this.sql;
    }
    
    public ParamParser withParamParser()
    {
        return this.paramParser;
    }

}
