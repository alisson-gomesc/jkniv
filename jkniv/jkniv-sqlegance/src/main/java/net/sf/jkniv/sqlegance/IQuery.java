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
 * This class represent a query object to find the query to be performed and
 * your parameters like: query parameters, offset and max objects to return.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 * @deprecated use Queryable interface, WILL BE REMOVED at version 1.0.0
 */
public interface IQuery
{
    /**
     * The query name used to localize the query at in XML file.
     * 
     * @return Return the name of the query.
     */
    String getName();
    
    /**
     * Parameters of query. Can be objects like Author, Book, etc. or a
     * java.util.Map.
     * 
     * @return the parameters to use at query.
     */
    Object getParams();
    
    /**
     * Get the number from first row from query result. If not set, rows will be
     * retrieved beginning from row 0.
     * 
     * @return return a row number, numbered from 0.
     */
    int getStart();
    
    /**
     * Set the number from first row from query result. If not set, rows will be
     * retrieved beginning from row 0.
     * 
     * @param value initial row number
     */
    void setStart(int value);
    
    /**
     * Get the maximum number of rows from query result.
     * 
     * @return Return the maximum number.
     */
    int getMax();
    
    /**
     * Set the maximum number of rows from query result.
     * 
     * @param value max value for row number
     */
    void setMax(int value);
    
    /**
     * Get total of rows that query can retrieve, filled after query is
     * executed. Useful to make pagination.
     * 
     * @return return the number of rows from query result.
     */
    long getTotal();
    
}
