/* 
 * JKNIV, utils - Helper utilities for jdk code.
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
package net.sf.jkniv.reflect.beans;

import java.util.Locale;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.reflect.ReflectionException;

class GetterMethod implements Capitalize
{
    private static final Assertable NOT_NULL = AssertsFactory.getNotNull();
    
    /**
     * Append prefix <code>get<code> to attributeColumnName and capitalize it.
     * @param attributeColumnName attribute name to capitalize with <code>get</code> prefix
     * @return return capitalize attribute name, sample: identityName -> getIdentityName
     */
    @Override
    public String does(String attributeColumnName)
    {
        NOT_NULL.verify(attributeColumnName);
        
        if (attributeColumnName.startsWith(GET))
            return attributeColumnName;
        
        String capitalize = "";
        
        if (attributeColumnName != null)
        {
            int length = attributeColumnName.length();
            capitalize = attributeColumnName.substring(0, 1).toUpperCase(Locale.ENGLISH);
            if (length > 1)
                capitalize += attributeColumnName.substring(1, length);
        }
        return GET + capitalize;
    }
    
    @Override
    public String undo(String attributeColumnName)
    {
        NOT_NULL.verify(attributeColumnName);
        String name = attributeColumnName;
        if (attributeColumnName.startsWith(GET))
            name = attributeColumnName.substring(3);
        
        int length = name.length();
        if (length == 0)
            throw new ReflectionException("Cannot uncapitalize the value of [" + attributeColumnName + "]");
        
        String uncapitalize = "";
        
        uncapitalize = name.substring(0, 1).toLowerCase(Locale.ENGLISH);
        if (length > 1)
            uncapitalize += name.substring(1, length);
        
        return uncapitalize;
    }
    
}
