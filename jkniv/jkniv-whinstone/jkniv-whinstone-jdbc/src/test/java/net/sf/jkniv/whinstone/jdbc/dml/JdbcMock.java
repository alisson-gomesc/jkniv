package net.sf.jkniv.whinstone.jdbc.dml;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

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
import net.sf.jkniv.sqlegance.params.ParamParserFactory;
import net.sf.jkniv.sqlegance.transaction.TransactionType;
import net.sf.jkniv.sqlegance.validation.ValidateType;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.jdbc.DataSourceAdapter;

public class JdbcMock
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
    private Repository        repository;
    private Class<?> returnType;
    
    public JdbcMock(Class<?> returnType) throws SQLException
    {
        this.returnType = returnType;
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
        this.repository = RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance(sqlContext);
    }
    
    public Repository getRepository()
    {
        return repository;
    }
    
    /**
     * Build the return type
     * @param sql instance of Sql
     * @return this builder instance
     */
    public JdbcMock sql(Sql sql) 
    {
        given(sql.getReturnType()).willReturn(returnType.getName());
        doReturn(returnType).when(sql).getReturnTypeAsClass();
        return this;
    }
    
    /**
     * Build the JDBC ResultSetMetaData 
     * @param columns array of columns like {id, name, postal_code, street_name}
     * @return this builder instance
     * @throws SQLException 
     */
    public JdbcMock columns(String[] columns) throws SQLException 
    {
        given(rsMetadata.getColumnCount()).willReturn(columns.length);
        for(int i=1; i<=columns.length; i++) 
        {
            given(rsMetadata.getColumnLabel(i)).willReturn(columns[i-1]);
            given(rsMetadata.getColumnName(i)).willReturn(columns[i-1]);
        }
        return this;
    }
    
    public void buildFifteenFlatBook() throws SQLException
    {
        given(rs.next()).willReturn(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false);
        given(rs.getObject(1)).willReturn(1001L,1002L,1003L,1004L,1005L,1006L,1007L,1008L,1009L,1010L,1011L,1012L,1013L,1014L,1015L);
        given(rs.getObject(2)).willReturn("978-1503250888","978-0201895421","007-6092019909","978-0321826626","978-0321712943","978-0321984135","9788535920598","9788535921182","9788535926019","9788535922837","9788535921205","978-1557427663","978-1612931036","978-0679720201","978-0679733843");
        given(rs.getObject(3)).willReturn("Beyond Good and Evil","Analysis Patterns: Reusable Object Models","Patterns of Enterprise Application Architecture","NoSQL Distilled: A Brief Guide to the Emerging World of Polyglot Persistence","Domain-Specific Languages (Addison-Wesley Signature Series","Refactoring: Ruby Edition: Ruby Edition","Claro Enigma","Sentimento do Mundo","A Lição do Amigo","Alguma Poesia","José","The Metamorphosis","The Trial","The Stranger","The Rebel: An Essay on Man in Revolt");
        given(rs.getObject(4)).willReturn("Friedrich Nietzsche","Martin Fowler","Martin Fowler","Martin Fowler","Martin Fowler","Martin Fowler","Carlos Drummond","Carlos Drummond","Carlos Drummond","Carlos Drummond","Carlos Drummond","Franz Kafka","Franz Kafka","Albert Camus","Albert Camus");
        given(rs.getObject(5)).willReturn(1,2,2,2,2,2,3,3,3,3,3,4,4,5,5);
        //"ID", "ISBN", "NAME", "AUTHOR", "AUTHOR_ID"
    }
    
    public void buildThreeFlatAuthor() throws SQLException {
        //given(this.rsMetadata.getColumnCount()).willReturn(3);
        //given(this.rsMetadata.getColumnLabel(1)).willReturn("id");
        //given(this.rsMetadata.getColumnName(1)).willReturn("id");
        //given(this.rsMetadata.getColumnLabel(2)).willReturn("name");
        //given(this.rsMetadata.getColumnName(2)).willReturn("name");
        //given(this.rsMetadata.getColumnLabel(3)).willReturn("book");
        //given(this.rsMetadata.getColumnName(3)).willReturn("book");
        
        given(rs.next()).willReturn(true, true, false);
        given(rs.getObject(1)).willReturn(1L, 2L);
        given(rs.getObject(2)).willReturn("Author 1", "Author 2");
        given(rs.getObject(3)).willReturn(10L, 11L);

    }
}
