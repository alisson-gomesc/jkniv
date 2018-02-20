/**
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
package net.sf.jkniv.sqlegance;

/**
 * Convert string names from <code>underscore</code> 
 * to <code>camel case</code> java attribute style,
 * underscores or dashes which is skipped.
 * 
 * <p>For example <tt>CUSTOMER_ID</tt> is mapped as <tt>customerId</tt>.</p>
 * 
 * @author Alisson Gomes
 */
public class UnderscoreToCamelCaseMapper implements JdbcColumnMapper
{
    public String map(final String column)
    {
        //if (column.indexOf("_") < 0 )
        //    return Character.toLowerCase(column.charAt(0)) + column.substring(1, column.length());
            
        StringBuilder sb = new StringBuilder();
        boolean toUpper = false;
        String columnCopy = new String(column).toLowerCase();
        for (char ch : columnCopy.toCharArray())
        {
            char cased = ch;
            if (ch == '_' || ch == '-')
            {
                toUpper = true;
                continue;
            }
            if (toUpper)
            {
                cased = Character.toUpperCase(ch);
                toUpper = false;
            }
            sb.append(cased);
        }
        return sb.toString();
    }
}
