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
package net.sf.jkniv.sqlegance.params;

/**
 * Strategy to parser the parameters from query extracting yours names or replacing
 * the names for question mark <code>'?'</code>, used at JDBC prepared statements.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface ParamParser
{

    /**
     * extract the parameters names from query.
     * @param query the SQL sentence
     * @return array parameters from query
     */
    String[] find(final String query);
    
    /**
     * Replace the parameters names for question marks
     * @param query the SQL sentence
     * @param params parameters used at query, its necessary when the query use IN clause 
     * @return the new SQL sentence with question marks.
     */
    String replaceForQuestionMark(final String query, final Object params);

    String replaceForQuestionMarkWithNumber(final String query, Object params);

    /**
     * Replace the parameters names for question marks
     * @param query the SQL sentence
     * @return the new SQL sentence with question marks.
     */
    String replaceForQuestionMark(final String query);

    
    ParamMarkType getType();
}
