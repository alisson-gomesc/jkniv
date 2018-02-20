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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.QueryNotFoundException;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.xml.AbstractSqlTag;
import net.sf.jkniv.sqlegance.builder.xml.IncludeTag;
import net.sf.jkniv.sqlegance.builder.xml.SelectColumnsTag;
import net.sf.jkniv.sqlegance.builder.xml.SqlCommandType;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;

/**
 * Load the SQL context from XML files (e.g. "classpath:/repository-sql.xml" or "/mypackage/mysqls.xml").
 * The XMLs resources MUST BE embedded with JARs accessible from class path.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class ClassPathSqlContext implements SqlContext
{
    private static final Logger                   LOG = LoggerFactory.getLogger(ClassPathSqlContext.class);
    private final Map<String, Sql>               statements;
    private final Map<String, SelectColumnsTag>   selectColumns;// TODO implements select-columns    
    private final String                          mainResourceName;
    private final RepositoryConfig                repositoryConfig;
    private final boolean                         shortnameEnable;
    private final String                          contextName;
    private SqlDialect sqlDialect;
    /** XMLs and yours includes */
    private final Map<String, List<XmlStatement>> resources;
    
    public ClassPathSqlContext(String resourceName)
    {
        this(resourceName, null, new Properties());
    }
    
    public ClassPathSqlContext(String resourceName, Properties props)
    {
        this(resourceName, null, props);
    }
    
    public ClassPathSqlContext(String resourceName, String repoConfigName, Properties props)
    {
        long initial = System.currentTimeMillis();
        this.mainResourceName = resourceName;
        this.statements = new HashMap<String, Sql>();
        this.selectColumns = new HashMap<String, SelectColumnsTag>();
        this.resources = new HashMap<String, List<XmlStatement>>();
        
        XmlStatement xmlStatementMain = preLoad(this.mainResourceName);
        
        String defaultContextName = getDefaultContextName(xmlStatementMain);// TODO test me defaultContextName and repoConfigName
        this.repositoryConfig = new RepositoryConfig(repoConfigName == null ? defaultContextName : repoConfigName);
        this.repositoryConfig.add(props);
        this.defineDialect();
        this.shortnameEnable = repositoryConfig.isShotKeyEnable();// FIXME implements DotQueryNameStrategy cannot use shortkeyenable
                                                                  // TODO test me ShortKeyEnable with DotQueryNameStrategy
        
        build(xmlStatementMain, resourceName);
        
        if (isEmpty(defaultContextName))
            this.contextName = repositoryConfig.getName();
        else
            this.contextName = defaultContextName;
        
        if (this.repositoryConfig.isReloadableXmlEnable())
        {
            ReloadableXmlResource reloadable = new ReloadableXmlResource();
            reloadable.pooling(this);
        }
        this.selectColumns.clear();
        LOG.info("{} SQL was loaded at {} ms", statements.size(), (System.currentTimeMillis() - initial));
    }
    
    private void defineDialect()
    {
        String sqlDialectName = repositoryConfig.getSqlDialect();
        ObjectProxy<SqlDialect> proxy = ObjectProxyFactory.newProxy(sqlDialectName);
        sqlDialect = proxy.newInstance();
    }
    
    @Override
    public Sql getQuery(String name)
    {
        Sql sql = statements.get(name);
        if (sql == null)
            throw new QueryNotFoundException("Query not found [" + name + "]");//
        if (sql instanceof Duplicate)
            throw new QueryNotFoundException(
                    "There are duplicate short name [" + name + "] for this statement, use fully name to recover it");
        
        
        
        return sql;
    }
    
    public String getContextName()
    {
        return this.contextName;
    }
    
    private String getDefaultContextName(XmlStatement xmlStatementMain)
    {
        String name = null;
        Element element = xmlStatementMain.getFirstElement(XmlStatement.ROOT_NODE);
        if (element != null)
        {
            name = element.getAttribute(XmlStatement.ATTR_CONTEXT_NAME);
        }
        return name;
    }
    
    @Override
    public RepositoryConfig getRepositoryConfig()
    {
        return this.repositoryConfig;
    }
    
    Map<String, List<XmlStatement>> getResources()
    {
        return resources;
    }
    
    void appendResource(String resourceName)
    {
        XmlStatement xmlStatement = preLoad(resourceName);
        build(xmlStatement, resourceName);
    }
    
    private XmlStatement preLoad(String resourceName)
    {
        XmlStatement xmlStatementMain = new XmlStatement(resourceName);
        xmlStatementMain.load();
        return xmlStatementMain;
    }
    
    private void build(XmlStatement xmlStatementMain, String resourceName)
    {
        addResource(resourceName, xmlStatementMain);
        NodeList filesInc = xmlStatementMain.evaluateXpath("//" + IncludeTag.TAG_NAME);
        if (filesInc != null)
        {
            for (int i = 0; i < filesInc.getLength(); i++)
            {
                Element e = (Element) filesInc.item(i);
                String filename = e.getAttribute(IncludeTag.ATTRIBUTE_HREF);
                XmlStatement xmlStatementInclude = new XmlStatement(filename);
                xmlStatementInclude.load();
                addResource(resourceName, xmlStatementInclude);
                processXML(xmlStatementInclude);
            }
        }
        // for last, process the tags from main file
        Map<String, Sql> readStatements = xmlStatementMain.build(sqlDialect);
        for (Entry<String, Sql> entry : readStatements.entrySet())
            add(entry.getKey(), entry.getValue());
    }
    
    /**
     * Process a set of XML files that were not read. If the file have someone
     * xi:include to include some XML file, these files should be read
     * recursively your sentences.
     * 
     * @param xmlStatement
     *            Document loaded ready to be processed
     */
    private void processXML(XmlStatement xmlStatement)
    {
        // process the include files recursively
        //NodeList filesInc = xmlDoc.getElementsByTagName(IncludeTag.TAG_NAME);
        NodeList filesInc = xmlStatement.evaluateXpath("//" + IncludeTag.TAG_NAME);
        addResource(xmlStatement.getResourceName(), xmlStatement);
        if (filesInc != null)
        {
            for (int i = 0; i < filesInc.getLength(); i++)
            {
                Element e = (Element) filesInc.item(i);
                String filename = e.getAttribute(IncludeTag.ATTRIBUTE_HREF);
                XmlStatement xmlStatementInclude = new XmlStatement(filename);
                xmlStatementInclude.load();
                processXML(xmlStatementInclude);
                addResource(xmlStatement.getResourceName(), xmlStatementInclude);
            }
        }
        Map<String, Sql> readStatements = xmlStatement.build(sqlDialect);
        for (Entry<String, Sql> entry : readStatements.entrySet())
            add(entry.getKey(), entry.getValue());
    }
    
    /**
     * keep the traceability from files and yours include trace
     * @param xmlParent
     * @param include
     */
    private void addResource(String xmlParent, XmlStatement include)
    {
        List<XmlStatement> includes = resources.get(xmlParent);
        if (includes == null)
        {
            includes = new ArrayList<XmlStatement>();
            resources.put(xmlParent, includes);
        }
        includes.add(include);
    }
    
    private void add(String key, Sql isql)
    {
        if (key.contains(".") && shortnameEnable)
        {
            String shortName = shortName(key);
            if (statements.get(shortName) == null)
                statements.put(shortName, isql);
            else
            {
                LOG.warn("There is duplicate short name [{}] for statement [{}], use fully name to recover it",
                        shortName, key);
                statements.put(shortName, new Duplicate(isql.getName(), LanguageType.NATIVE));
            }
        }
        Sql old = statements.put(key, isql);
        if (old != null)
            LOG.warn("The statement [{}] from [{}] was replaced from resource [{}]", key, old.getResourceName(),
                    isql.getResourceName());
    }
    
    private String shortName(String key)
    {
        final String[] keyparts = key.split("\\.");
        final String shortKey = keyparts[keyparts.length - 1];
        return shortKey;
    }
    
    /**
     * Returns an unmodifiable view of the specified map.
     * @return unmodifiable Map.
     */
    public Map<String, Sql> getStatements()
    {
        return Collections.unmodifiableMap(statements);
    }
    
    @Override
    public boolean containsQuery(String name)
    {
        return statements.containsKey(name);
    }
    
    @Override
    public String getName()
    {
        return this.contextName;
    }
    
    @Override
    public SqlDialect getSqlDialect()
    {
        return this.sqlDialect;
    }
    
    private boolean isEmpty(String value)
    {
        return (value == null || "".equals(value));
    }
    
    /**
     * Represents duplicate queries with same short name
     */
    static class Duplicate extends AbstractSqlTag
    {
        public Duplicate(String name, LanguageType languageType)
        {
            super(name, languageType);
        }
        
        @Override
        public String getTagName() { return null; }
        
        @Override
        public SqlType getSqlType() { return SqlType.UNKNOWN; }
        
        @Override
        public boolean isSelectable() { return false; }
        
        @Override
        public boolean isInsertable() { return false; }
        
        @Override
        public boolean isUpdateable() { return false; }
        
        @Override
        public boolean isDeletable() { return false; }
    }
    
}
