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
 * <p>
 * A set of rules for determining the query name to a set of methods (get, list,
 * add, remove, update) from Repository that don't have a query object as
 * parameter. To change the strategy implements this interface and configure at
 * persistence.xml one property called <code>jkniv.repository.query_namestrategy</code>.
 *
 * <p>
 * Sample:
 * 
 * <pre>
 *   &lt;properties&gt;
 *     &lt;property name="jkniv.repository.query_namestrategy" value="com.acme.MyQueryNameStrategy" /&gt;
 *   &lt;/properties&gt;
 * </pre>
 * 
 * @author Alisson Gomes
 * @since 0.5.0
 */
public interface QueryNameStrategy
{
    //public static String PROPERTY_NAME_STRATEGY = "jkniv.repository.namestrategy";
    
    /**
     * Return the default query name at XML file to retrieve an object by 'get'
     * method.
     * 
     * @param o
     *            instance of object that will be persisted
     * @return name of query method
     */
    String toGetName(Object o);
    
    /**
     * Return the default query name at XML file to retrieve an object by 'add'
     * method.
     * 
     * @param o
     *            instance of object that will be persisted
     * @return name of query method
     */
    String toAddName(Object o);
    
    /**
     * Return the default query name at XML file to retrieve an object by
     * 'remove' method.
     * 
     * @param o
     *            instance of object that will be persisted
     * @return name of query method
     */
    String toRemoveName(Object o);
    
    /**
     * Return the default query name at XML file to retrieve an object by
     * 'update' method.
     * 
     * @param o
     *            instance of object that will be persisted
     * @return name of query method
     */
    String toUpdateName(Object o);
    
    /**
     * Return the default query name at XML file to retrieve an object by 'list'
     * method.
     * 
     * @param o
     *            instance of object that will be persisted
     * @return name of query method
     */
    String toListName(Object o);
}
