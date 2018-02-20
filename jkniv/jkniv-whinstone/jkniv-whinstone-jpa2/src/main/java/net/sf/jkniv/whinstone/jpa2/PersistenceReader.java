/* 
 * JKNIV, whinstone one contract to access your database.
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
package net.sf.jkniv.whinstone.jpa2;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class PersistenceReader
{
    private static final String                 PERSISTENCE_FILE       = "META-INF/persistence.xml";
    private static Document                     doc                    = null;
    private static PersistenceInfo              persistenceInfoDefault = null;
    private static Map<String, PersistenceInfo> cacheProvider          = new HashMap<String, PersistenceInfo>();
    private static XPath                        xpath                  = XPathFactory.newInstance().newXPath();
    
    public static PersistenceInfo getPersistenceInfo()
    {
        init();
        return readXMLProvider(null);
    }
    
    public static PersistenceInfo getPersistenceInfo(String unitName)
    {
        init();
        return readXMLProvider(unitName);
    }
    
    private static void init()
    {
        if (doc == null)
        {
            
            InputStream is = DefaultClassLoader.getResourceAsStream(PERSISTENCE_FILE);
            if (is != null)
            {
                loadXML(is);
                readPersistenceUnit();
            }
            else
                throw new RuntimeException("Cannot read the file " + PERSISTENCE_FILE);
        }
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
    private static void loadXML(InputStream xml)
    {
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;
            docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse(xml);
            // doc.getDocumentElement().normalize();
        }
        catch (ParserConfigurationException e)
        {
            // TODO re-design exception
            throw new RuntimeException(
                    "Error in parser the xml file ["
                            + xml
                            + "]. ParserConfigurationException: "
                            + e.getMessage()
                            + ". Verify if the name from file start with '/' and contains the package, because the path is absolute");
        }
        catch (SAXException e)
        {
            // TODO re-design exception
            throw new RuntimeException("Error in parser the xml file [" + xml + "]. SAXException: " + e.getMessage());
        }
        catch (IOException e)
        {
            // TODO re-design exception
            throw new RuntimeException("Error in parser the xml file [" + xml + "]. IOException: " + e.getMessage());
        }
    }
    
    private static PersistenceInfo readXMLProvider(String unitName)
    {
        PersistenceInfo info = null;
        if (unitName == null)
            info = persistenceInfoDefault;
        else if (persistenceInfoDefault != null && unitName.equals(persistenceInfoDefault.getUnitName().equals(info)))
            info = persistenceInfoDefault;
        else
        {
            info = cacheProvider.get(unitName);
        }
        
        if (info == null || info.getProvider() == null)
        {
            /*
            info = new PersistenceInfo();
            XPath xpath = XPathFactory.newInstance().newXPath();
            try
            {
                NodeList nodes = null;
                nodes = (NodeList) xpath
                        .evaluate("/persistence/persistence-unit/provider", doc, XPathConstants.NODESET);
                if (unitName != null && !"".equals(unitName))
                {
                    info.setProvider((String) xpath.evaluate("/persistence/persistence-unit[@name='" + unitName
                            + "']/provider", doc, XPathConstants.STRING));
                    info.setUnitName(unitName);
                    cacheProvider.put(info.getUnitName(), info);
                }
                else
                {
                    nodes = (NodeList) xpath.evaluate("/persistence/persistence-unit", doc, XPathConstants.NODESET);
                    if (nodes != null)
                    {
                        for (int i = 0; i < nodes.getLength(); i++)
                        {
                            Node node = nodes.item(i);
                            info = new PersistenceInfo();
                            info.setUnitName(node.getAttributes().getNamedItem("name").getTextContent());
                            info.setProvider((String) xpath.evaluate(
                                    "/persistence/persistence-unit[@name='" + info.getUnitName() + "']/provider", doc,
                                    XPathConstants.STRING));
                            cacheProvider.put(info.getUnitName(), info);
                            if (info.getUnitName() != null && !"".equals(info.getUnitName()))
                                break;
                        }
                    }
                }
            }
            catch (XPathExpressionException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */
        }
        return info;
    }
    
    public static void readPersistenceUnit()
    {
        NodeList nodes = null;
        PersistenceInfo info;
        String expression = "";
        try
        {
            nodes = (NodeList) xpath.evaluate("/persistence/persistence-unit", doc, XPathConstants.NODESET);
            if (nodes != null)
            {
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    Node node = nodes.item(i);
                    info = new PersistenceInfo();
                    info.setUnitName(getNodeTextContent(node, "name"));
                    info.setTransactionType(getNodeTextContent(node, "transaction-type"));
                    
                    expression = "/persistence/persistence-unit[@name='" + info.getUnitName() + "']/provider";
                    info.setProvider((String) xpath.evaluate(expression, doc, XPathConstants.STRING));
                    
                    expression = "/persistence/persistence-unit[@name='" + info.getUnitName() + "']/provider";
                    info.setProvider((String) xpath.evaluate(expression, doc, XPathConstants.STRING));
             
                    setClasses(info.getUnitName(), info);
                    setProperties(info.getUnitName(), info);
                    cacheProvider.put(info.getUnitName(), info);
                    if (i == 0)
                        persistenceInfoDefault = info;
                }
            }
        }
        catch (XPathExpressionException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static void setClasses(String unitName, PersistenceInfo info)
    {
        String expression = "/persistence/persistence-unit[@name='" + unitName + "']/class";
        try
        {
            NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
            if (nodes != null)
            {
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    Node node = nodes.item(i);
                    if (node != null)
                    {
                        info.addClass(node.getTextContent());
                    }
                }
            }
        }
        catch (XPathExpressionException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }
    
    private static void setProperties(String unitName, PersistenceInfo info)
    {
        String expression = "/persistence/persistence-unit[@name='" + unitName + "']/properties/property";
        try
        {
            NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
            if (nodes != null)
            {
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    Node node = nodes.item(i);
                    if (node != null)
                    {
                        info.addProperty(getNodeTextContent(node, "name"), getNodeTextContent(node, "value"));
                    }
                }
            }
        }
        catch (XPathExpressionException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
    private static String getNodeTextContent(Node node, String item)
    {
        String value = "";
        Node n = node.getAttributes().getNamedItem(item);
        if (n != null)
            value = n.getTextContent();
        return value;
    }
}
