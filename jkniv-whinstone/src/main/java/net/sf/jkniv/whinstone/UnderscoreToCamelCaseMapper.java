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
package net.sf.jkniv.whinstone;

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
        StringBuilder sb = new StringBuilder();
        boolean toUpper = false;
        char[] columnCopy = null;
        if(column.indexOf(".") < 0)
            columnCopy = column.toLowerCase().toCharArray();
        else 
            columnCopy = column.toCharArray();
        for (char ch : columnCopy)
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
