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
import java.util.Arrays;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import net.sf.jkniv.sqlegance.DefaultClassLoader;
import net.sf.jkniv.sqlegance.RepositoryException;

class XmlValidator
{
    private static final Logger LOG        = LoggerFactory.getLogger(XmlValidator.class);
    /* Pseudo URL prefix for loading from the class path: "classpath:" */
    //private static final String  CLASSPATH_URL_PREFIX = "classpath:";
    //private static final String XSD_CONFIG = "/net/sf/jkniv/sqlegance/builder/xml/sqlegance-config.xsd";
    //private static final String XSD_STMT   = "/net/sf/jkniv/sqlegance/builder/xml/sqlegance-stmt.xsd";
    
    //private String              version;
    //private String              resourceName;
    //private XmlStatement        doc;
    //private boolean             isXmlStmt;
    //private boolean             isXmlConfig;
    /*
    
    private XmlValidator(String resourceName, XmlStatement doc)
    {
        this.resourceName = resourceName;
        this.doc = doc;
        this.load();
    }
    private void load()
    {
        if (doc.exists())
        {
            Document xmldoc = doc.get();
            
            Node first = xmldoc.getFirstChild();
            if (first instanceof Element)
            {
                Element e = (Element) first;
                this.version = e.getAttribute("version");
                if (RepositoryConfig.XPATH_ROOT_NODE.equals(e.getTagName()))
                    isXmlConfig = true;
                
            }
            isXmlStmt = !isXmlConfig;
        }
    }
    */
    /*
     * Validate XML file against XSD schema.
     * @throws RepositoryException if XML it's not valid or some error happen
     *
    private void validate()
    {
        StreamSource xmlFile = null;
        String xsd = XSD_STMT;
        try
        {
            if (isXmlConfig)
                xsd = XSD_CONFIG;
            
            if (this.version != null && !"".equals(version.trim()))
                xsd = xsd.replaceAll(".xsd", "-" + version + ".xsd");// sample sqlegance-config.xsd -> sqlegance-config-0.6.xsd
                
            URL schemaFile = getClass().getResource(xsd);
            //xmlFile = new StreamSource(XmlDocument.class.getResourceAsStream(resourceName));
            xmlFile = new StreamSource(DefaultClassLoader.getResourceAsStream(resourceName));
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            xmlFile.setSystemId(resourceName);
            validator.validate(xmlFile);
        }
        catch (SAXException e)
        {
            throw new RepositoryException(
                    "Fail validate [" + resourceName + "] against XSD=[" + xsd + "]. " + e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new RepositoryException(
                    "Fail validate [" + resourceName + "] against XSD=[" + xsd + "]. " + e.getMessage(), e);
        }
        finally
        {
            if (xmlFile != null)
                closeStream(xmlFile.getInputStream(), resourceName);
        }
    }
*/
    public static void validate(URL[] schemaFiles, String resourceName)
    {
        StreamSource xmlFile = null;
        try
        {
            xmlFile = new StreamSource(DefaultClassLoader.getResourceAsStream(resourceName));
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            //Schema schema = schemaFactory.newSchema(new Source[] { new StreamSource(schemaFiles[0])});//, new StreamSource(schemaFiles[1])});
            
            //Schema schema = schemaFactory.newSchema(XmlValidator.class.getResource(schemaFiles[0]));
            Source source1 = new StreamSource(schemaFiles[0].toExternalForm());
            Source source2 = new StreamSource(schemaFiles[1].toExternalForm());
            Schema schema = null;
            
            if (resourceName.endsWith("repository-config.xml"))
                schema = schemaFactory.newSchema(new Source[] { source1});
            else
                schema = schemaFactory.newSchema(new Source[] { source1, source2});
            Validator validator = schema.newValidator();
            xmlFile.setSystemId(resourceName);
            validator.validate(xmlFile);
        }
        catch (SAXException e)
        {
            List<URL> schemas = Arrays.asList(schemaFiles);
            throw new RepositoryException(
                    "Fail validate [" + resourceName + "] against XSD=[" + schemas + "]. " + e.getMessage(), e);
        }
        catch (IOException e)
        {
            List<URL> schemas = Arrays.asList(schemaFiles);
            throw new RepositoryException(
                    "Fail validate [" + resourceName + "] against XSD=[" + schemas + "]. " + e.getMessage(), e);
        }
        finally
        {
            if (xmlFile != null)
                closeStream(xmlFile.getInputStream(), resourceName);
        }
    }

    /**
     * Close InputStream quietly
     * @param is stream to be closed
     */
    private static void closeStream(InputStream is, String resourceName)
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
    
}
