/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.sqlegance.builder;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.Statistical;
import net.sf.jkniv.sqlegance.builder.xml.NoSqlStats;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.sqlegance.logger.SimpleDataMasking;
import net.sf.jkniv.sqlegance.transaction.TransactionType;

public class RepositoryConfigTest
{
    //private SqlContext sqlContext = new ClassPathSqlContext("/repository-sql.xml");
    
    @Test
    public void whenReadRepositoryConfigSuccessfully()
    {
        RepositoryConfig config = new RepositoryConfig("whinstone-jdbc");
        assertThat("My jdbc datasource config", is(config.getDescription()));
        assertThat("jdbc/dssample", is(config.getJndiDataSource()));
        assertThat(TransactionType.LOCAL, is(config.getTransactionType()));
        assertThat("admin", is( config.getProperty(RepositoryProperty.JDBC_USER)));        
        assertThat("whinstone-jdbc", is(config.getName()));
        //assertThat("net.sf.jkniv.whinstone.jdbc.DefaultPreparedStatementStrategy", is(config.getProperty(RepositoryProperty.PREPARED_STATEMENT_STRATEGY)));
        assertThat("net.sf.jkniv.sqlegance.QueryNameStrategy", is(config.getProperty(RepositoryProperty.QUERY_NAME_STRATEGY)));
    }
    
    @Test
    public void whenReadRepositoryConfigLoadFromContext()
    {
        SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
        RepositoryConfig config = sqlContext.getRepositoryConfig();
        assertThat("My jdbc datasource config", is(config.getDescription()));
        assertThat("jdbc/dssample", is(config.getJndiDataSource()));
        assertThat(TransactionType.LOCAL, is(config.getTransactionType()));
        assertThat("admin", is( config.getProperty(RepositoryProperty.JDBC_USER)));
        assertThat("whinstone-jdbc", is(config.getName()));
        assertThat(sqlContext.getName(), is(config.getName()));
        //assertThat("net.sf.jkniv.whinstone.jdbc.DefaultPreparedStatementStrategy", is(config.getProperty(RepositoryProperty.PREPARED_STATEMENT_STRATEGY)));
        assertThat("net.sf.jkniv.sqlegance.QueryNameStrategy", is(config.getProperty(RepositoryProperty.QUERY_NAME_STRATEGY)));
    }
    
    
    @Test
    public void whenRepositoryConfigHasDefaultValues()
    {
        RepositoryConfig config = new RepositoryConfig("default-values");
        assertThat(config.getName(), is("default-values"));
        assertThat(config.getDescription(), is("My Repository config with default values"));
        assertThat(config.getJndiDataSource(), is("jdbc/default_values"));
        assertDefaultValues(config);
    }

    @Test
    public void whenRepositoryConfigLoadInexistentFileKeepDefaultValues()
    {
        RepositoryConfig config = new RepositoryConfig("fileNotFound-m1-r3-k9");
        assertThat(config.getName(), is("fileNotFound-m1-r3-k9"));
        assertDefaultValues(config);
    }
    
    private void assertDefaultValues(RepositoryConfig config)
    {
        assertThat(config.getTransactionType(), is(TransactionType.LOCAL));
        assertThat(config.getProperty(RepositoryProperty.JDBC_USER), is(nullValue()));
        assertThat(config.getProperty(RepositoryProperty.JDBC_DRIVER), is(nullValue()));
        assertThat(config.getProperty(RepositoryProperty.JDBC_PASSWORD), is(nullValue()));
        assertThat(config.getProperty(RepositoryProperty.JDBC_URL), is(nullValue()));
        //assertThat(config.getProperty(RepositoryProperty.PREPARED_STATEMENT_STRATEGY), is(nullValue()));
        //assertThat(config.getDataSource(), is(nullValue()));
        
        assertThat(config.getProperty(RepositoryProperty.DATA_MASKING), is(RepositoryProperty.DATA_MASKING.defaultValue()));
        assertThat(config.getProperty(RepositoryProperty.DEBUG_SQL), is(RepositoryProperty.DEBUG_SQL.defaultValue()));
        assertThat(config.getProperty(RepositoryProperty.RELOADABLE_XML_ENABLE), is(RepositoryProperty.RELOADABLE_XML_ENABLE.defaultValue()));
        //assertThat(config.getProperty(RepositoryProperty.PROJECT_PACKAGE_ENABLE), is(RepositoryProperty.PROJECT_PACKAGE_ENABLE.defaultValue()));
        assertThat(config.getProperty(RepositoryProperty.QUERY_NAME_STRATEGY), is(RepositoryProperty.QUERY_NAME_STRATEGY.defaultValue()));
        assertThat(config.getProperty(RepositoryProperty.SHORT_NAME_ENABLE), is(RepositoryProperty.SHORT_NAME_ENABLE.defaultValue()));
        assertThat(config.getProperty(RepositoryProperty.SQL_DIALECT), is(RepositoryProperty.SQL_DIALECT.defaultValue()));
        assertThat(config.getProperty(RepositoryProperty.SQL_STATS), is(RepositoryProperty.SQL_STATS.defaultValue()));
        
        assertThat(RepositoryProperty.SQL_STATS.defaultValue(), is("false"));
        
        assertThat(config.getProperties().isEmpty(), is(true));
        assertThat(config.isReloadableXmlEnable(), is(false));
        assertThat(config.isShotKeyEnable(), is(false));
        //assertThat(config.getPreparedStatementStrategy(), is(nullValue()));
        assertThat(config.getQueryNameStrategy(), is(RepositoryProperty.QUERY_NAME_STRATEGY.defaultValue()));
        
        assertThat(config.getDataMasking(), instanceOf(SimpleDataMasking.class));
        //assertThat(config.getSqlLogger(), instanceOf(SqlLogger.class));
        assertThat(config.getSqlDialect().getClass().getName(), is(RepositoryProperty.SQL_DIALECT.defaultValue()));
        assertThat(config.getSqlDialect(), instanceOf(SqlDialect.class));
        assertThat(config.getSqlDialect(), instanceOf(AnsiDialect.class));
        assertThat(config.getStatistical(), instanceOf(Statistical.class));
        assertThat(config.getStatistical(), instanceOf(NoSqlStats.class));
        
        assertThat(config.getSqlDialect().getMaxOfParameters(), is(Integer.MAX_VALUE));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.UNKNOW), is(false));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.LIMIT), is(false));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.LIMIT_OFF_SET), is(false));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.ROWNUM), is(false));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.CONN_HOLDABILITY), is(true));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.STMT_HOLDABILITY), is(false));
        
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY), is(false));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.PAGING_ROUNDTRIP), is(true));
    }
    
    @Test
    public void whenRepositoryConfigOverrideSqlFeatureSupport()
    {
        RepositoryConfig config = new RepositoryConfig("dialect-override");
        assertThat(config.getName(), is("dialect-override"));
        assertThat(config.getDescription(), is("My jdbc datasource config overriding dialect"));
        assertThat(config.getJndiDataSource(), is("jdbc/dssample"));

        assertThat(config.getSqlDialect().getMaxOfParameters(), is(255));

        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.UNKNOW), is(true));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.LIMIT), is(true));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.LIMIT_OFF_SET), is(false));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.ROWNUM), is(true));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.CONN_HOLDABILITY), is(false));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.STMT_HOLDABILITY), is(true));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY), is(true));
        assertThat(config.getSqlDialect().supportsFeature(SqlFeatureSupport.PAGING_ROUNDTRIP), is(false));
        
    }
}
