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
package net.sf.jkniv.sqlegance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Type of sql sentence supported at xml files.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 * @since 0.6.0 default value change to NATIVE
 */
public enum LanguageType
{
    JPQL, HQL, NATIVE, CRITERIA, STORED;
    
    private static final Logger LOG = LoggerFactory.getLogger(LanguageType.class);
    
    /**
     * Get the language type from String
     * 
     * @param type of language: NATIVE, STORED, JPQL, HQL...
     * @return the type of query, if anyone {@code type} match the default value NATIVE is returned.
     */
    public static LanguageType get(String type)
    {
        LanguageType t = null;
        for (LanguageType qt : LanguageType.values())
        {
            if (String.valueOf(type).equalsIgnoreCase(qt.toString()))
            {
                t = qt;
                break;
            }
        }
        if (t == null)
        {
            t = LanguageType.NATIVE;
            LOG.trace("Type [" + type + "] query no defined (JPQL, HQL, NATIVE, STORED), default NATIVE");
        }
        return t;
    }

}
