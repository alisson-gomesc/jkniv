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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.Sql;

/**
 * <p>
 * The XmlBuilderSql is the central class to read XML files and load the queries
 * at memory. The default initial file with the queries is named "SqlConfig.xml"
 * this file can have included other files like:
 * </p>
 * <p>
 * <code>
 * &lt;include href="SQL1Test.xml"/&gt;
 * &lt;include href="SQL2Test.xml"/&gt;  
 * </code>
 * </p>
 * <p>
 * If you don't like the name "SqlConfig.xml" or have some constraint with this
 * name you can create other name to file and performed the method
 * <code>XmlBuilderSql.configFile("/newXmlFile.xml")</code>.
 * </p>
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 * @deprecated use SqlContextFactory
 */
public final class XmlBuilderSql
{
    private static final Logger                  LOG               = LoggerFactory.getLogger(XmlBuilderSql.class);
    private static String[]                      defaultFileConfig =
    { "/repository-sql.xml", "/SqlConfig.xml" };
    private static boolean                       initialized       = false;
    
    private static final Map<String, Properties> repoConfig        = new HashMap<String, Properties>();
    
    private static ClassPathSqlContext sqlContext;
    
    /**
     * evict instance of <code>XmlBuilderSql</code>
     */
    private XmlBuilderSql()
    {
    }
    
    /**
     * Load Re-initialize the mapping XML file, making the framework read again the
     * XML file named "fileConfig". Automatic reload XML is provider at 0.6.0 version.
     * 
     * @param fileConfig
     *            The XML file with SQL sentences.
     * @deprecated This method is deprecated and will must be removed at version 1.0
     */
    @Deprecated
    public synchronized static void configFile(String fileConfig)
    {
        LOG.warn("This method is deprecated and will must be removed at version 1.0");
        if (sqlContext == null)
        {
            sqlContext = new ClassPathSqlContext(fileConfig);
        }
        else
            sqlContext.appendResource(fileConfig);
    }
    
    private static void loadQueries()
    {
        try
        {
            if (!readXml(defaultFileConfig[0]))
            {
                LOG.warn("File [{}] is deprecated, the new file [{}] is read first. You can be interested to use SqlContextFactory.newInstance(String) instead XmlBuilderSql.", defaultFileConfig[1], defaultFileConfig[0]);
                readXml(defaultFileConfig[1]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();// TODO exception design needs
        }
    }
    
    private static boolean readXml(String xmlFile)
    {
        boolean xmlLoadedAndProcess = false;
        if (!XmlBuilderSql.initialized)
        {
            LOG.trace("reading xml [{}] initialized={}", xmlFile, XmlBuilderSql.initialized);
            
            if (sqlContext == null)
                sqlContext = new ClassPathSqlContext(xmlFile);
            else
                sqlContext.appendResource(xmlFile);
            xmlLoadedAndProcess = true;
            XmlBuilderSql.initialized = true;
        }
        return xmlLoadedAndProcess;
    }
    
    /**
     * Retrieve one query according your name.
     * 
     * @param name
     *            Name of the query.
     * @return Return the query object with SQL.
     * @exception IllegalArgumentException
     *                if the parameter name does not refer names of query
     *                configured this exception is thrower.
     */
    public static Sql getQuery(String name)
    {
        if (notLoadedQueries())
            loadQueries();
        
        Sql sql = sqlContext.getQuery(name);
        
        return sql;
    }
    
    private static boolean notLoadedQueries()
    {
        return !XmlBuilderSql.initialized;
    }
    
    public static boolean containsQuery(String name)
    {
        if (notLoadedQueries())
            loadQueries();
        
        return sqlContext.containsQuery(name);
    }
}
