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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.jkniv.sqlegance.Deletable;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.Statistical;
import net.sf.jkniv.sqlegance.Updateable;
import net.sf.jkniv.sqlegance.builder.xml.AbstractSqlTag;
import net.sf.jkniv.sqlegance.builder.xml.AutoGeneratedKey;
import net.sf.jkniv.sqlegance.builder.xml.ProcedureParameterTag;
import net.sf.jkniv.sqlegance.builder.xml.ProcedureTag;
import net.sf.jkniv.sqlegance.builder.xml.SqlTag;
import net.sf.jkniv.sqlegance.builder.xml.TagFactory;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.ChooseTag;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.ITextTag;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.IfTag;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.OtherwiseTag;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.SetTag;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.StaticText;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.WhenTag;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.WhereTag;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetHoldability;
import net.sf.jkniv.sqlegance.statement.ResultSetType;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.sqlegance.validation.ValidateType;

/**
 * Internal helper class to read XML nodes using XPATH expressions and building
 * SQL queries.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class XmlStatement
{
    private static final Logger LOG                 = LoggerFactory.getLogger(XmlStatement.class);
    private static final XPath  xpath               = XPathFactory.newInstance().newXPath();
    /** statements                      */
    static final String         ROOT_NODE           = "statements";
    /** statements/package               */
    private static final String ROOT_NODE_PACKAGE   = "statements/package";
    /** statements/package[@name='%s']/  */
    private static final String NODE_PACKAGE_BYNAME = ROOT_NODE_PACKAGE + "[@name='%s']/";
    /** context */
    static final String         ATTR_CONTEXT_NAME = "context";

    static final String SCHEMA_XSD = "/net/sf/jkniv/sqlegance/builder/xml/sqlegance-stmt.xsd";
    
    private String              resourceName;
    private Date                timestamp;
    private String              contextName;
    private XmlReader           xmlReader;
    
    public XmlStatement(String resourceName)
    {
        this.resourceName = resourceName;
    }
    
    public void load()
    {
        this.xmlReader = new XmlReader(resourceName);
        this.xmlReader.load();
    }
    
    public Map<String, Sql> build(RepositoryConfig repositoryConfig)
    {
        this.timestamp = new Date();
        final Map<String, Sql> statements = new HashMap<String, Sql>();
        
        processTagsElements(ROOT_NODE + "/" + Selectable.TAG_NAME, "", statements, repositoryConfig.getSqlDialect(), repositoryConfig.getStatistical());
        processTagsElements(ROOT_NODE + "/" + Insertable.TAG_NAME, "", statements, repositoryConfig.getSqlDialect(), repositoryConfig.getStatistical());
        processTagsElements(ROOT_NODE + "/" + Updateable.TAG_NAME, "", statements, repositoryConfig.getSqlDialect(), repositoryConfig.getStatistical());
        processTagsElements(ROOT_NODE + "/" + Deletable.TAG_NAME, "", statements, repositoryConfig.getSqlDialect(), repositoryConfig.getStatistical());
        processProcedureElements(ROOT_NODE + "/" + ProcedureTag.TAG_NAME, "", statements, repositoryConfig.getSqlDialect(), repositoryConfig.getStatistical());
        NodeList nodes = xmlReader.evaluateXpath(ROOT_NODE_PACKAGE);
        if (nodes != null)
        {
            for (int i = 0; i < nodes.getLength(); i++)
            {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element) node;
                    String name = element.getAttribute("name");
                    processTagsElements(NODE_PACKAGE_BYNAME + Selectable.TAG_NAME, name, statements, repositoryConfig.getSqlDialect(), repositoryConfig.getStatistical());
                    processTagsElements(NODE_PACKAGE_BYNAME + Insertable.TAG_NAME, name, statements, repositoryConfig.getSqlDialect(), repositoryConfig.getStatistical());
                    processTagsElements(NODE_PACKAGE_BYNAME + Updateable.TAG_NAME, name, statements, repositoryConfig.getSqlDialect(), repositoryConfig.getStatistical());
                    processTagsElements(NODE_PACKAGE_BYNAME + Deletable.TAG_NAME, name, statements, repositoryConfig.getSqlDialect(), repositoryConfig.getStatistical());
                    processProcedureElements(NODE_PACKAGE_BYNAME + ProcedureTag.TAG_NAME, name, statements, repositoryConfig.getSqlDialect(), repositoryConfig.getStatistical());
                }
            }
        }
        return statements;
    }
      
    /**
     * Evaluate the compiled XPath expression in the specified context and
     * return the result as the specified type.
     * 
     * @param expressionXpath
     * @param element
     * @return
     */
    public NodeList evaluateXpath(String expressionXpath)
    {
        return xmlReader.evaluateXpath(expressionXpath, true);
    }

    public Element getFirstElement(String expressionXpath)
    {
        return xmlReader.getFirstElement(expressionXpath);
    }

        
    private void processTagsElements(String expressionXpath, String packet, 
                                     Map<String, Sql> statements,
                                     SqlDialect sqlDialect, Statistical stats)
    {
        String xpathSql = String.format(expressionXpath, packet);
        NodeList nodes = xmlReader.evaluateXpath(xpathSql);
        if (nodes != null)
        {
            for (int i = 0; i < nodes.getLength(); i++)
            {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element) node;
                    SqlTag tag = buildSql(element, node.getNodeName(), xpathSql, stats);
                    tag.bind(sqlDialect);
                    Sql oldSql = put(packet, statements, tag);
                    if (oldSql != null)
                        LOG.info("SQL {} was updated", oldSql.getName());

                }
            }
        }
    }
    
    private SqlTag buildSql(Element element, String nodeName, String xpathSql, Statistical stats)
    {
        SqlTag tag = null;
        if (ProcedureTag.TAG_NAME.equals(nodeName))
        {
            tag = buildProcedure(element, xpathSql, stats);
        }
        else
        {
            String id = element.getAttribute(AbstractSqlTag.ATTRIBUTE_NAME);
            LanguageType type = LanguageType.get(element.getAttribute(AbstractSqlTag.ATTRIBUTE_TYPE));
            Isolation isolation = Isolation.get(element.getAttribute(AbstractSqlTag.ATTRIBUTE_ISOLATION));
            int timeout = parserTimeout(element.getAttribute(AbstractSqlTag.ATTRIBUTE_TIMEOUT));
            boolean batch = Boolean.valueOf(element.getAttribute(AbstractSqlTag.ATTRIBUTE_BATCH));
            String cache = element.getAttribute(AbstractSqlTag.ATTRIBUTE_CACHE);
            String hint = element.getAttribute(AbstractSqlTag.ATTRIBUTE_HINT);
            String returnType = element.getAttribute(AbstractSqlTag.ATTRIBUTE_RETURN_TYPE);
            String groupBy = element.getAttribute(AbstractSqlTag.ATTRIBUTE_GROUP_BY);
            
            ResultSetType resultSetType = ResultSetType
                    .get(element.getAttribute(AbstractSqlTag.ATTRIBUTE_RESULTSET_TYPE));
            ResultSetConcurrency resultSetConcurrency = ResultSetConcurrency
                    .get(element.getAttribute(AbstractSqlTag.ATTRIBUTE_RESULTSET_CONCURRENCY));
            ResultSetHoldability resultSetHoldability = ResultSetHoldability
                    .get(element.getAttribute(AbstractSqlTag.ATTRIBUTE_RESULTSET_HOLDABILITY));
            
            ValidateType validateType = ValidateType.get(element.getAttribute(AbstractSqlTag.ATTRIBUTE_VALIDATION));
            
            tag = newTag(nodeName, id, type, isolation, timeout, batch, cache, hint, resultSetType,
                    resultSetConcurrency, resultSetHoldability, returnType, groupBy, validateType, element, stats);
            tag.setXpath(xpathSql + "[@id='" + id + "']");
            tag.setResourceName(this.resourceName);
            for (int j = 0; j < element.getChildNodes().getLength(); j++)
            {
                ITextTag textTag = getDynamicNode(element.getChildNodes().item(j));
                if (tag.isInsertable() && textTag instanceof AutoGeneratedKey)
                {
                    AutoGeneratedKey autoGeneratedKeyTag = (AutoGeneratedKey)textTag;
                    Insertable insertTag = (Insertable) tag;
                    insertTag.setAutoGeneratedKey(autoGeneratedKeyTag);
                }
                else if (textTag instanceof ITextTag)
                {
                    tag.addTag(textTag);
                }
            }
        }
        return tag;
    }
    
    private ProcedureTag buildProcedure(Element element, String xpathSql, Statistical stats)
    {
        String id = element.getAttribute(ProcedureTag.ATTRIBUTE_NAME);
        String spName = element.getAttribute(ProcedureTag.ATTRIBUTE_SPNAME);
        ProcedureTag tag = new ProcedureTag(id, LanguageType.STORED, stats);
        tag.setSpName(spName);
        tag.setResourceName(resourceName);
        tag.setXpath(xpathSql);
        ProcedureParameterTag[] paramsTag = processParameterTag(element);
        tag.setParams(paramsTag);
        return tag;
    }

    private AutoGeneratedKey processAutoGenerateKeyTag(Element element)
    {
        String type = element.getAttribute(AutoGeneratedKey.ATTRIBUTE_STRATEGY);
        String columns = element.getAttribute(AutoGeneratedKey.ATTRIBUTE_COLUMNS);
        String props = element.getAttribute(AutoGeneratedKey.ATTRIBUTE_PROPS);
        String text = element.getTextContent();
        AutoGeneratedKey tag = TagFactory.newAutoGeneratedKey(type, columns, props, text);
        return tag;
    }

    private void processProcedureElements(String expressionXpath, String packet, 
                                          Map<String, Sql> statements, 
                                          SqlDialect sqlDialect, Statistical stats)
    {
        String xpathSql = String.format(expressionXpath, packet);
        NodeList procedures = xmlReader.evaluateXpath(expressionXpath);
        if (procedures != null)
        {
            for (int s = 0; s < procedures.getLength(); s++)
            {
                Node firstNode = procedures.item(s);
                if (firstNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element) firstNode;
                    ProcedureTag tag = buildProcedure(element, xpathSql, stats);
                    tag.bind(sqlDialect);
                    Sql oldSql = put(packet, statements, tag);
                    if (oldSql != null)
                        LOG.info("SQL {} was updated", oldSql.getName());
                }
            }
        }
    }
    
    private Sql put(String packet, Map<String, Sql> statements, SqlTag tag)
    {
        Sql oldSql = null;
        if (packet == null || "".equals(packet))
            oldSql = statements.put(tag.getName(), tag);
        else
        {
            tag.setPackage(packet);
            oldSql =  statements.put(packet + "." + tag.getName(), tag);
        }
        return oldSql;
    }
    
    /**
     * Read and load the values of <parameter> tag from <procedure> tag.
     * 
     * @param element
     *            procedure node
     * @return
     */
    private ProcedureParameterTag[] processParameterTag(Element element)
    {
        NodeList nodeList = xmlReader.evaluateXpath(ProcedureParameterTag.TAG_NAME, element);
        ProcedureParameterTag[] params = new ProcedureParameterTag[nodeList.getLength()];
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            ProcedureParameterTag tag = null;
            Element ele = (Element) nodeList.item(i);
            String property = ele.getAttribute(ProcedureParameterTag.ATTRIBUTE_PROPERTY);
            String mode = ele.getAttribute(ProcedureParameterTag.ATTRIBUTE_MODE);
            String sqlType = ele.getAttribute(ProcedureParameterTag.ATTRIBUTE_SQLTYPE);
            String typeName = ele.getAttribute(ProcedureParameterTag.ATTRIBUTE_TYPENAME);
            
            if (!"".equals(typeName) && "IN".equals(mode))
                throw new RepositoryException("The parameter [" + property + "] is wrong. There is a typeName ["
                        + typeName + "] but is like IN mode. TypeName must be used with OUT or INOUT");
            
            if (!"".equals(typeName) && "".equals(sqlType))
                throw new RepositoryException("The parameter [" + property + "] is wrong. There is a typeName ["
                        + typeName + "] but dont have a sqlType defined");
            
            if ("".equals(sqlType) && "".equals(typeName))
                tag = new ProcedureParameterTag(property, mode);
            else if (!"".equals(typeName) && "".equals(typeName))
                tag = new ProcedureParameterTag(property, mode, sqlType);
            else
                tag = new ProcedureParameterTag(property, mode, sqlType, typeName);
            
            params[i] = tag;
        }
        return params;
    }
    
    private SqlTag newTag(String nodeName, String id, LanguageType type, Isolation isolation, int timeout,
            boolean batch, String cache, String hint, ResultSetType resultSetType,
            ResultSetConcurrency resultSetConcurrency, ResultSetHoldability resultSetHoldability, String returnType,
            String groupBy, ValidateType validateType, Element element, Statistical stats)
    {
        SqlTag tag = null;
        if (Selectable.TAG_NAME.equals(nodeName))
        {
            tag = (SqlTag)TagFactory.newSelect(id, type, isolation, timeout, 
                                               cache, resultSetType, 
                                               resultSetConcurrency, resultSetHoldability, 
                                               returnType, groupBy, validateType, stats);
            
            NodeList oneToManyList = xmlReader.evaluateXpath(OneToMany.TAG_NAME, element);

            for (int i = 0; i < oneToManyList.getLength(); i++)
            {
                OneToMany tagOtM = null;
                Element ele = (Element) oneToManyList.item(i);
                String property = ele.getAttribute(OneToMany.ATTRIBUTE_PROPERTY);
                String typeOf = ele.getAttribute(OneToMany.ATTRIBUTE_TYPEOF);
                String impl = ele.getAttribute(OneToMany.ATTRIBUTE_IMPL);
                tagOtM = TagFactory.newOneToMany(property, typeOf, impl);
                ((Selectable)tag).addOneToMany(tagOtM);
            }
        }
        else if (Insertable.TAG_NAME.equals(nodeName))
        {
            tag = (SqlTag) TagFactory.newInsert(id, type, isolation, timeout, validateType, stats);
        }
        else if (Updateable.TAG_NAME.equals(nodeName))
        {
            tag = (SqlTag) TagFactory.newUpdate(id, type, isolation, timeout, validateType, stats);
        }
        else if (Deletable.TAG_NAME.equals(nodeName))
        {
            tag = (SqlTag) TagFactory.newDelete(id, type, isolation, timeout, validateType, stats);
        }
        else if (ProcedureTag.TAG_NAME.equals(nodeName))
        {
            tag = new ProcedureTag(id, type, isolation, timeout, ValidateType.NONE, stats);
        }
        return tag;
    }
    
    private ITextTag getDynamicNode(Node node)
    {
        String nodeName = node.getNodeName();
        String text = node.getNodeValue();
        ITextTag tag = null;
        // log.info("tag child nodes <"+ nodeName + "> " + text);
        if (IfTag.TAG_NAME.equals(nodeName))
        {
            Element element = (Element) node;
            tag = new IfTag(element.getAttribute(IfTag.ATTRIBUTE_TEST), xmlReader.getTextFromElement(element));
        }
        else if (WhereTag.TAG_NAME.equals(nodeName))
        {
            Element element = (Element) node;
            List<ITextTag> listIfTag = processTagDecision(element);
            tag = new WhereTag(listIfTag);
        }
        else if (SetTag.TAG_NAME.equals(nodeName))
        {
            Element element = (Element) node;
            List<ITextTag> listIfTag = processTagDecision(element);
            tag = new SetTag(listIfTag);
        }
        else if (ChooseTag.TAG_NAME.equals(nodeName))
        {
            Element element = (Element) node;
            List<WhenTag> listWhenTag = processWhenTag(element);
            tag = new ChooseTag(listWhenTag);
        }
        else if (AutoGeneratedKey.TAG_NAME.equals(nodeName))
        {
            Element element = (Element) node;
            tag = processAutoGenerateKeyTag(element);
        }
        else
        // #text or #cdata-section
        {
            if (text != null)
            {
                text = text.trim();
                if (!"".equals(text))
                {
                    tag = new StaticText(text);
                }
            }
        }
        return tag;
    }
    
    /**
     * Evaluate all decision tags (if/choose) to guarantee the same execution
     * order that are finds in xml file
     * 
     * @param element
     * @return
     */
    private List<ITextTag> processTagDecision(Element element)
    {
        List<ITextTag> list = new ArrayList<ITextTag>();
        NodeList nodeList = xmlReader.evaluateXpath(IfTag.TAG_NAME + " | " + ChooseTag.TAG_NAME, element);
        
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Element elementNode = (Element) nodeList.item(i);
            // log.info("tag child nodes <"+ elementNode.getTagName() + "> ");
            if (IfTag.TAG_NAME.equalsIgnoreCase(elementNode.getTagName()))
            {
                IfTag tag = new IfTag(elementNode.getAttribute(IfTag.ATTRIBUTE_TEST), xmlReader.getTextFromElement(elementNode));
                list.add(i, tag);
            }
            else if (ChooseTag.TAG_NAME.equalsIgnoreCase(elementNode.getTagName()))
            {
                List<WhenTag> listWhen = processWhenTag(elementNode);
                list.add(new ChooseTag(listWhen));
            }
        }
        return list;
    }
        
    /**
     * TODO docme
     * 
     * @param element
     * @return
     */
    private List<WhenTag> processWhenTag(Element element)
    {
        List<WhenTag> list = new ArrayList<WhenTag>();
        NodeList nodeList = xmlReader.evaluateXpath(WhenTag.TAG_NAME, element);
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Element whnEle = (Element) nodeList.item(i);
            WhenTag tag = new WhenTag(whnEle.getAttribute(WhenTag.ATTRIBUTE_TEST), xmlReader.getTextFromElement(whnEle));
            list.add(i, tag);
        }
        nodeList = xmlReader.evaluateXpath(OtherwiseTag.TAG_NAME, element);
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Element otherEle = (Element) nodeList.item(i);
            OtherwiseTag tag = new OtherwiseTag(xmlReader.getTextFromElement(otherEle));
            list.add(tag);
            if (i > 0)
                throw new RepositoryException("There are more one <otherwise> tag inner <choose> tag");
        }
        return list;
    }
        
    /**
     * Parser time to define timeout to connection execute query
     * 
     * @param time
     *            time in milliseconds
     * @return return the timeout value, -1 is returned if NumberFormatException
     *         it's throw
     */
    private static int parserTimeout(String time)
    {
        int timeout = -1;
        if (time != null)
        {
            try
            {
                timeout = Integer.parseInt(time);
            }
            catch (NumberFormatException nfe)
            {
                // cannot parser number using -1 as timeout
            }
        }
        return timeout;
    }
    
    public String getResourceName()
    {
        return resourceName;
    }
    
    public Date getTimestamp()
    {
        return timestamp;
    }
    
    @Override
    public String toString()
    {
        return "XmlStatement [resourceName=" + resourceName + ", timestamp=" + timestamp + "]";
    }
/*
    private void setTimeToLive()
    {
        Node first = doc.getFirstChild();
        if (first instanceof Element)
        {
            Element e = (Element) first;
            String repositoryConfigName = e.getAttribute("context");
            if (repositoryConfigName != null)
            {
                repositoryConfigName = repositoryConfigName.trim();
                if (!"".equals(repositoryConfigName))
                {
                    RepositoryConfig config = new RepositoryConfig(repositoryConfigName);
                    config.load();
                    //                    String ttl = config.getProperty(RepositoryProperty.TTL_SQL);
                    //                    try
                    //                    {
                    //                        if (ttl != null)
                    //                            this.timeToLive = Integer.parseInt(ttl);
                    //                    }
                    //                    catch (NumberFormatException nfe)
                    //                    {
                    //                    }
                }
            }
        }
    }
*/    
}
