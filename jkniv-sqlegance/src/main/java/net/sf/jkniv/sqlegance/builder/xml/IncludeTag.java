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

import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.SqlType;

/**
 * Tag of include xml file. Include tag is used to organize xml sentences at
 * many files separated.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
public class IncludeTag extends AbstractSqlTag implements SqlTag
{
    public static final String TAG_NAME       = "include";
    public static final String ATTRIBUTE_HREF = "href";
    
    /**
     * Build a new <code>include</code> tag from XML file.
     * 
     * @param id Name/Identify from tag
     * @param languageType type of language from tag
     */
    public IncludeTag(String id, LanguageType languageType)
    {
        super(id, languageType);
    }
    
    /**
     * @return NULL is returned, because this tag isn't SQL sentence.
     */
    @Override
    public String getSql(Object params)
    {
        return null;
    }
    
    /**
     * Retrieve the tag name.
     * 
     * @return name from tag <code>include</code>.
     */
    @Override
    public String getTagName()
    {
        return TAG_NAME;
    }
    
    /**
     * Command type to execute.
     * 
     * @return the type of command used, <code>UNKNOW</code>.
     */
    @Override
    public SqlType getSqlType()
    {
        return SqlType.UNKNOWN;
    }
}
