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

/**
 * Types of supported repositories.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public enum RepositoryType
{
    JDBC, JPA, CASSANDRA, COUCHDB, COUCHBASE
    ;
    
    /**
     * Return the type of repository, default is JDBC
     * @param s enumeration name of the type
     * @return the type that match {@code s} or JDBC as default.
     */
    public static RepositoryType get(String s)
    {
        RepositoryType answer = RepositoryType.JDBC;
        for(RepositoryType type : RepositoryType.values())
        {
            if (type.name().equalsIgnoreCase(s))
                answer = type;
        }
        return answer;
    }

}
