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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.builder.xml.XMLFileStatus;

/**
 * Represent each XML file declared at SqlConfig.xml or your include files.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 * @deprecated DON'T use this class WILL BE REMOVED at 1.0.0 version
 */
public class SqlFile
{
    private static final Logger LOG = LoggerFactory.getLogger(SqlFile.class);
    private String        name;
    private XMLFileStatus status;
    
    /**
     * Build a new sql file with <code>UNPROCESSED</code> status.
     * 
     * @param name
     *            XML relative name
     */
    public SqlFile(String name)
    {
        this.name = name;
        this.status = XMLFileStatus.UNPROCESSED;
        LOG.warn("DON'T use this class [{}] WILL BE REMOVED at 1.0.0 version", getClass().getCanonicalName());
    }
    
    /**
     * Retrieve the XML relative name.
     * 
     * @return relative name of XML file.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Status from XML file.
     * 
     * @return <code>UNPROCESSED</code> or <code>PROCESSED</code>
     */
    public XMLFileStatus getStatus()
    {
        return status;
    }
    
    /**
     * put the status of file as <code>PROCESSED</code>
     */
    public void processed()
    {
        this.status = XMLFileStatus.PROCESSED;
    }
    
    @Override
    public String toString()
    {
        return name + ": " + status;
    }
}
