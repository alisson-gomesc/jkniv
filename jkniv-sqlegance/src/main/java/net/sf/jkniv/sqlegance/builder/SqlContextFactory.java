package net.sf.jkniv.sqlegance.builder;

import java.util.Properties;

import net.sf.jkniv.sqlegance.SqlContext;

/**
 * Load the SQL sentences from XML file build a new context to the queries
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class SqlContextFactory
{
    /**
     * Build a new context from SQL sentences
     * @param resourceName Initial XML file with SQL sentences
     * @return SQL sentences that belong to repository (like database)
     */
    public static SqlContext newInstance(String resourceName)
    {
        return new ClassPathSqlContext(resourceName);
    }

    /**
     * Build a new context from SQL sentences
     * @param resourceName Initial XML file with SQL sentences
     * @param props customized properties to SQL context, see {@code RepositoryProperty} enumeration 
     * @return SQL sentences that belong to repository (like database)
     */
    public static SqlContext newInstance(String resourceName, Properties props)
    {
        return new ClassPathSqlContext(resourceName, props);
    }

    /**
     * Build a new context from SQL sentences
     * @param resourceName Initial XML file with SQL sentences
     * @param repositoryConfigName name of repository
     * @return SQL sentences that belong to repository (like database)
     */
    public static SqlContext newInstance(String resourceName, String repositoryConfigName)
    {
        return new ClassPathSqlContext(resourceName, repositoryConfigName, null);
    }

}
