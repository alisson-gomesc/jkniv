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
package net.sf.jkniv.sqlegance.logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Masked the attributes <code>password</code>, <code>passwd</code> and <code>pwd</code> 
 * as <code>****</code>.
 * 
 * To add new attributes for masking rules see {@link #addAttrName(String)}
 * 
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class SimpleDataMasking implements DataMasking
{
    private static final List<String> sensibleAttr = new ArrayList<String>();
    private static final String MASK = "****";
    static
    {
       sensibleAttr.add("password");
       sensibleAttr.add("passwd");
       sensibleAttr.add("pwd");
    }
    
    /**
     * Set new attribute name to masking your value by log API.
     * @param nameAttribute attribute name to mask
     */
    public static void addAttrName(String nameAttribute)
    {
        sensibleAttr.add(nameAttribute);
    }
    
    public Object mask(String attributeName, Object data)
    {
        return (sensibleAttr.contains(attributeName) ? 
                (data != null ? MASK : data) 
                : data);
    }
    
}
