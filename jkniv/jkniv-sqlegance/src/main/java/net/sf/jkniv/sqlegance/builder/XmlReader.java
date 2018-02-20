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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sf.jkniv.sqlegance.DefaultClassLoader;
import net.sf.jkniv.sqlegance.RepositoryException;

/**
 * Internal helper class to read XML nodes using XPATH expressions and building
 * SQL queries.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class XmlReader
{
    private static final Logger LOG   = LoggerFactory.getLogger(XmlReader.class);
    private static final XPath  xpath = XPathFactory.newInstance().newXPath();
    
    private String              resourceName;
    private Document            doc;
    private Date                timestamp;
    private String              version;
    private String              schemaXsd;
    
    public XmlReader(String resourceName)
    {
        this.resourceName = resourceName;
    }
    
    public String getResourceName()
    {
        return resourceName;
    }
    
    public Date getTimestamp()
    {
        return timestamp;
    }
    
    /**
     * Load {@code org.w3c.dom.Document} parsing your content of
     * <code>resourceName</code>.
     * 
     * @return Document Parsed the content from <code>resourceName</code>.
     * @throws RepositoryException
     *             if some error happen
     */
    public boolean load()
    {
        InputStream is = null;
        boolean loaded = false;
        try
        {
            //Loading XML bean definitions from class path resource [spring-context.xml]
            LOG.info("Loading XML resource [{}] from class path", resourceName);
            URL url = DefaultClassLoader.getResource(resourceName);
            if (url == null)
                return false;
            
                try
                {
                    is = url.openStream();
                }
                catch (IOException e)
                {
                    LOG.error("Cannot open xml file [{}]", resourceName, e);
                }
                if (is != null)
                {
                    doc = loadXML(is);
                    doc.getDocumentElement().normalize();
                    loaded = true;
                    setVersion();
                }
        }
        finally
        {
            closeStream(is);
        }
        XmlValidator.validate(getXsdUrl(), resourceName);
        return loaded;
    }
    
    /**
     * Load the XML file and make your parse with XML schema.
     * 
     * @param xml
     *            path from XML file in absolute format (start with '/')
     * @return Return XML file as Document object.
     * @throws RuntimeException
     *             is launched in case something wrong occurs; like
     *             ParserConfigurationException, SAXException or IOException.
     * @see Document
     * @see ParserConfigurationException
     * @see SAXException
     * @see IOException
     */
    private Document loadXML(InputStream xml)
    {
        Document doc = null;
        if (xml == null)
            throw new RepositoryException(
                    "There is XML file '<include href=...' that is not correctly named, check the name "
                            + "from your xml files at '<include href=...' tag. The file path is absolute and must agree that package is.");
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            // docBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
            // "http://www.w3.org/2001/XMLSchema");
            // docBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource",
            // "sqlegance-0.1.xsd");
            
            DocumentBuilder docBuilder;
            docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse(xml);
        }
        catch (ParserConfigurationException e)
        {
            // FIXME exception design create ParserException
            throw new RuntimeException("Error in parser the xml file [" + xml + "]. ParserConfigurationException: "
                    + e.getMessage()
                    + ". Verify if the name from file start with '/' and contains the package, because the path is absolute");
        }
        catch (SAXException e)
        {
            // FIXME exception design create ParserException
            throw new RuntimeException("Error in parser the xml file [" + xml + "]. SAXException: " + e.getMessage());
        }
        catch (IOException e)
        {
            // FIXME exception design create ParserException
            throw new RuntimeException("Error in parser the xml file [" + xml + "]. IOException: " + e.getMessage());
        }
        return doc;
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
        return evaluateXpath(expressionXpath, true);
    }
    
    /**
     * Evaluate the compiled XPath expression in the specified context and
     * return the result as the specified type.
     * 
     * @param expressionXpath
     * @param element
     * @return
     */
    public NodeList evaluateXpath(String expressionXpath, boolean logEnable)
    {
        NodeList nodeList = null;
        try
        {
            XPathExpression exp = xpath.compile(expressionXpath);
            nodeList = (NodeList) exp.evaluate(doc, XPathConstants.NODESET);
        }
        catch (XPathExpressionException e)
        {
            if (logEnable)
                LOG.info("XPath is wrong: " + expressionXpath);
        }
        return nodeList;
    }
    
    /**
     * TODO documentation
     * 
     * @param expressionXpath
     * @param element
     * @return
     */
    public NodeList evaluateXpath(String expressionXpath, Element element)
    {
        NodeList nodeList = null;
        try
        {
            nodeList = (NodeList) xpath.evaluate(expressionXpath, element, XPathConstants.NODESET);
        }
        catch (XPathExpressionException e)
        {
            LOG.info("XPath is wrong: " + expressionXpath);
        }
        return nodeList;
    }
    
    /**
     * TODO documentation
     * 
     * @param element
     * @return
     */
    public String getTextFromElement(Element element)
    {
        String text = element.getChildNodes().item(0).getNodeValue().trim();
        
        if ((text == null || "".equals(text)) && element.getChildNodes().item(0).getNextSibling() != null)
        {
            text = element.getChildNodes().item(0).getNextSibling().getNodeValue().trim();
        }
        return text;
    }
    
    public Element getFirstElement(String xpath)
    {
        Element element = null;
        NodeList list = evaluateXpath(xpath, false);
        if (list != null && list.getLength() > 0)
        {
            Node node = list.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                element = (Element) node;
            }
        }
        return element;
    }
    
    /**
     * Close InputStream quietly
     * 
     * @param is
     *            stream to be closed
     */
    private void closeStream(InputStream is)
    {
        if (is != null)
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                LOG.warn("Cannot close input stream [{}]", resourceName);
            }
        }
    }
    
    private URL getXsdUrl()
    {
        String xsd = schemaXsd;
        if (this.version != null && !"".equals(version.trim()))
            xsd = xsd.replaceAll(".xsd", "-" + version + ".xsd");// sample sqlegance-sql.xsd -> sqlegance-sql-0.6.xsd
            
        URL schemaFile = getClass().getResource(xsd);
        return schemaFile;
    }
    
    private void setVersion()
    {
        Node first = doc.getFirstChild();
        if (first instanceof Element)
        {
            Element e = (Element) first;
            this.version = e.getAttribute("version");
            if (XmlStatement.ROOT_NODE.equals(e.getTagName()))
                schemaXsd = XmlStatement.SCHEMA_XSD;
            else if (RepositoryConfig.XPATH_ROOT_NODE.equals(e.getTagName()))
                schemaXsd = RepositoryConfig.SCHEMA_XSD;
            else
                schemaXsd = XmlStatement.SCHEMA_XSD;
        }
    }
    
    @Override
    public String toString()
    {
        return "XmlReader [resourceName=" + resourceName + ", timestamp=" + timestamp + "]";
    }
}
