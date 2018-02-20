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
package net.sf.jkniv.sqlegance.builder.xml;

import java.util.List;

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.ITextTag;

/**
 * Represent specifics attribute from tag to build SQL sentences from XML files.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
public interface SqlTag extends Sql
{
    /**
     * Retrieve the identifier from tag.
     * 
     * @return name from tag
     */
    String getName();
    
    /**
     * Retrieve the tag name.
     * 
     * @return name from tag.
     */
    String getTagName();
    
    /**
     * add a set of static text from tag elements.
     * @param text content
     */
    void addTag(String text);
    
    /**
     * add a new text tag.
     * @param tag content as another tag
     */
    void addTag(ITextTag tag);
    
    /**
     * add a set of text tags (static or dynamic).
     * @param tags content as list of text tags
     */
    void addTag(List<ITextTag> tags);
    
    /**
     * set the XPATH expression to retrieve the query at xml file.
     * @param xpath expression
     */
    void setXpath(String xpath);
    
    /**
     * File name from Sql
     * @param resourceName filename from sql as resource
     */
    void setResourceName(String resourceName);
    
    
//    /**
//     * Set the time to keep the query in memory, after that a new parser must be
//     * make at XML file, that is reload it.
//     * @param seconds time in seconds
//     */
//    void setTimeToLive(long seconds);
}
