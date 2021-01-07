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

import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.omg.CORBA.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.env.EnvPropertyResolver;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.Statistical;
import net.sf.jkniv.sqlegance.builder.xml.NoSqlStats;
import net.sf.jkniv.sqlegance.builder.xml.SqlStats;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureFactory;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.sqlegance.logger.SimpleDataMasking;
import net.sf.jkniv.sqlegance.transaction.TransactionType;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class RepositoryConfig
{
    private static final Logger                 LOG                    = LoggerFactory
            .getLogger(RepositoryConfig.class);
    private static String                       defaultFileConfig      = "/repository-config.xml";
    private static final String                 ATTR_NAME              = "name";
    private static final String                 ATTR_VALUE             = "value";
    private static final String                 ATTR_TX_TYPE           = "transaction-type";
    private static final String                 XPATH_REPO_NODE        = "/repository";
    static final String                         SCHEMA_XSD             = "/net/sf/jkniv/sqlegance/builder/xml/sqlegance-config.xsd";
    static final String                         XPATH_ROOT_NODE        = "repository-config";
    private static final String                 XPATH_PROPERTY_NODE    = XPATH_ROOT_NODE + XPATH_REPO_NODE
            + "[@name='%s']/properties/property";
    private static final String                 XPATH_DESCRIPTION_NODE = XPATH_ROOT_NODE + XPATH_REPO_NODE
            + "[@name='%s']/description[1]";
    private static final String                 XPATH_JNDI_DS_NODE     = XPATH_ROOT_NODE + XPATH_REPO_NODE
            + "[@name='%s']/jndi-data-source[1]";
    
    private static final Assertable             NOT_NULL                = AssertsFactory.getNotNull();
    //private static final Map<String, SqlLogger> loggers                = new HashMap<String, SqlLogger>();
    private String                              name;
    private String                              description;
    private String                              jndiDataSource;
    private TransactionType                     transactionType;
    private Properties                          properties;
    //private SqlLogger                           sqlLogger;
    private DataMasking                         masking;
    private RepositoryType                      repositoryType;
    private SqlDialect                          sqlDialect;
    private EnvPropertyResolver                 propertyResolver;
    public RepositoryConfig()
    {
        this(null, true);
    }
    
    /**
     * Build new configuration object that access to DataSource and your properties
     * Default instance is created if parameter is a <code>null</code> name 
     * @param name from repository configuration
     * @throws IllegalArgumentException if the name is <code>null</code>
     */
    public RepositoryConfig(String name)
    {
        this(name, false);
    }
    
    private RepositoryConfig(String name, boolean defaultRepo)
    {
        if (!defaultRepo)
            NOT_NULL.verify(name);
        
        this.name = name;
        this.properties = new Properties();
        this.propertyResolver = new EnvPropertyResolver();
        this.load();
        setDataMasking();
        //setSqlLogger();
        //this.sqlDialectName = getProperty(RepositoryProperty.SQL_DIALECT);
        if (this.transactionType == null)
            this.transactionType = TransactionType.LOCAL;
        if (isEmpty(this.jndiDataSource))
            this.jndiDataSource = name;
        
    }
    
    private void load()
    {
        XmlReader xmlReader = new XmlReader(defaultFileConfig);
        if (xmlReader.load())
        {
            NodeList nodes = xmlReader.evaluateXpath(XPATH_ROOT_NODE + XPATH_REPO_NODE);
            if (nodes != null)
            {
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element element = (Element) node;
                        String name = element.getAttribute(ATTR_NAME);
                        if (isEmpty(this.name) || this.name.equals(name))// first repository is default
                        {
                            if (LOG.isDebugEnabled())
                                LOG.debug("Binding Sql Statements context [{}] with Repository config [{}]", this.name,
                                        name);
                            this.name = name;
                            parseAttributes(element);// FIXME parser attributes
                            parseDescription(xmlReader);
                            parseJndiDataSource(xmlReader);
                            parseProperties(xmlReader);
                            break;
                        }
                    }
                }
            }
            else if (!hasProperties())
            {
                LOG.info("repository-config.xml does not found. Using [{}] as jndi datasource.", this.name);
                this.jndiDataSource = this.name;
            }
        }
        defineDialect();
    }
    
    private void parseAttributes(Element element)
    {
        this.name = element.getAttribute(ATTR_NAME);
        String txType = element.getAttribute(ATTR_TX_TYPE);
        String type = element.getAttribute("type");
        this.transactionType = TransactionType.get(txType);
        this.repositoryType = RepositoryType.get(type);
    }
    
    private void parseDescription(XmlReader xmlReader)
    {
        NodeList nodes = xmlReader.evaluateXpath(String.format(XPATH_DESCRIPTION_NODE, name));
        for (int i = 0; i < nodes.getLength(); i++)
        {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                this.description = xmlReader.getTextFromElement(element);
            }
        }
    }
    
    private void parseJndiDataSource(XmlReader xmlReader)
    {
        NodeList nodes = xmlReader.evaluateXpath(String.format(XPATH_JNDI_DS_NODE, name));
        if (nodes != null)
        {
            for (int i = 0; i < nodes.getLength(); i++)
            {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element) node;
                    this.jndiDataSource = xmlReader.getTextFromElement(element);
                }
            }
        }
    }
    
    private void parseProperties(XmlReader xmlReader)
    {
        NodeList nodesProperties = xmlReader.evaluateXpath(String.format(XPATH_PROPERTY_NODE, name));
        if (nodesProperties != null)
        {
            for (int i = 0; i < nodesProperties.getLength(); i++)
            {
                Node nodes = nodesProperties.item(i);
                if (nodes.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element) nodes;
                    String properName = element.getAttribute(ATTR_NAME);
                    String properValue = element.getAttribute(ATTR_VALUE);
                    this.properties.put(properName, properValue);
                }
            }
        }
    }
    
//    private void setSqlLogger()
//    {
//        SqlLogger sqlLogger = loggers.get(name);
//        if (sqlLogger == null || sqlLogger.getLogLevel() == LogLevel.NONE)
//        {
//            LogLevel logLevel = LogLevel.get(getProperty(RepositoryProperty.DEBUG_SQL));
//            sqlLogger = new SqlLogger(logLevel, this.masking);
//            loggers.put(this.name, sqlLogger);
//        }
//    }
    
    private void setDataMasking()
    {
        if (masking == null)
        {
            masking = new SimpleDataMasking();
            String className = getProperty(RepositoryProperty.DATA_MASKING);
            if (className != null)
            {
                ObjectProxy<DataMasking> proxy = ObjectProxyFactory.of(className);
                masking = proxy.newInstance();
            }
        }
    }
    
    public String getQueryNameStrategy()
    {
        return getProperty(RepositoryProperty.QUERY_NAME_STRATEGY);
    }

    public String getJdbcAdapterFactory()
    {
        String adpterClassName = getProperty(RepositoryProperty.JDBC_ADAPTER_FACTORY);
        return adpterClassName;
    }

    public boolean isShotKeyEnable()
    {
        return Boolean.valueOf(getProperty(RepositoryProperty.SHORT_NAME_ENABLE));
    }
    
    public boolean isReloadableXmlEnable()
    {
        return Boolean.valueOf(getProperty(RepositoryProperty.RELOADABLE_XML_ENABLE));
    }
    
    public void add(String key,  String value)
    {
        Properties props = new Properties();
        props.put(key, value);
        add(props);
    }
    
    public void add(Properties props)
    {
        if (props != null)
        {
            for (Entry<Object, Object> entry : props.entrySet())
            {
                Object old = this.properties.put(entry.getKey().toString(), entry.getValue());
                if (old != null)
                    LOG.info("The value of key [{}] with original value [{}] was replacement to [{}]", entry.getKey(),
                            old, entry.getValue());
                if(RepositoryProperty.SQL_DIALECT.key().equals(entry.getKey().toString()))
                    defineDialect();
            }
        }
    }

    public boolean hasProperties()
    {
        return (!this.properties.isEmpty());
    }

    public DataMasking getDataMasking()
    {
        return this.masking;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public String getName()
    {
        return name;
    }
    
    public TransactionType getTransactionType()
    {
        return transactionType;
    }
    
    public SqlDialect getSqlDialect()
    {
        return this.sqlDialect;
    }

    public void setSqlDialect(SqlDialect sqlDialect)
    {
        this.sqlDialect = sqlDialect;
    }
    
    public Statistical getStatistical()
    {
        String enable = getProperty(RepositoryProperty.SQL_STATS);
        if ("true".equalsIgnoreCase(enable))
            return new SqlStats();
        
        return NoSqlStats.getInstance();
    }

    public String getJndiDataSource()
    {
        return jndiDataSource;
    }
    
    public Properties getProperties()
    {
        return properties;
    }
    
    public String getProperty(RepositoryProperty proper)
    {
        String value = properties.getProperty(proper.key(), proper.defaultValue());
        if (value != null)
            value = propertyResolver.getValue(value);
        return value;
    }
    
    public String getProperty(String proper, String defaultValue)
    {
        String value = properties.getProperty(proper, defaultValue);
        if (value != null)
            value = propertyResolver.getValue(value);
        
        return value;
        
    }
    
    public String getProperty(String proper)
    {
        return getProperty(proper, null);
    }
    
    public DataSource lookup()
    {
        String jndi = getJndiDataSource();
        DataSource ds = null;
        Context ctx = null;
        try
        {
            //ctx = (Context) new InitialContext().lookup("java:comp/env");
            ctx = new InitialContext();
            if (LOG.isDebugEnabled())
                LOG.debug("Lookuping JNDI resource with: new InitialContext().lookup(\"{}\") ...", jndi);
            ds = (DataSource) ctx.lookup(jndi);
        }
        catch (NamingException ne)
        {
            LOG.error("NamingException, cannot lookup jndi datasource [" + jndi + "]: " + ne.getMessage());
        }
        return ds;
    }
    
    private boolean isEmpty(String value)
    {
        return (value == null || "".equals(value));
    }
    
    public RepositoryType getRepositoryType()
    {
        return repositoryType;
    }

    private void defineDialect()
    {
        String sqlDialectName = getProperty(RepositoryProperty.SQL_DIALECT);
        ObjectProxy<SqlDialect> proxy = ObjectProxyFactory.of(sqlDialectName);
        sqlDialect = proxy.newInstance();
        for (SqlFeatureSupport feature : SqlFeatureSupport.values())
        {
            String value = getProperty(RepositoryProperty.PREFIX+".feature."+feature.name().toLowerCase());
            if (value != null)
            {
                boolean supported = Boolean.valueOf(value);
                sqlDialect.addFeature(SqlFeatureFactory.newInstance(feature, supported));
            }
        }
        String maxParameters = getProperty(RepositoryProperty.PREFIX+".jdbc.max_parameters");
        if (maxParameters != null)
            sqlDialect.setMaxOfParameters(Integer.valueOf(maxParameters));
    }

    @Override
    public String toString()
    {
        return "RepositoryConfig [name=" + name + ", sqlDialectName=" + getSqlDialect() + ", transactionType="
                + transactionType + "]";
    }
    
}
