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
package net.sf.jkniv.sqlegance.statement;

/**
 * Flags to indicate the type of <code>ResultSet</code> objects with the given holdability.
 * 
 * @author Alisson Gomes
 * @see java.sql.ResultSet
 * @since 0.6.0
 *
 */
public enum ResultSetHoldability
{
    /**  */
    DEFAULT,
    /**  */
    HOLD_CURSORS_OVER_COMMIT, 
    /**  */
    CLOSE_CURSORS_AT_COMMIT;    

    /**
     * @param type String that represent enum ignoring case
     * @return the value of <code>type</code>, type of not found return <code>DEFAULT</code> enum. 
     */
    public static ResultSetHoldability get(String type)
    {
        ResultSetHoldability rs = ResultSetHoldability.DEFAULT;
        for (ResultSetHoldability r : ResultSetHoldability.values())
        {
            if (String.valueOf(type).equalsIgnoreCase(r.name()))
            {
                rs = r;
                break;
            }
        }
        return rs;
    }

}
