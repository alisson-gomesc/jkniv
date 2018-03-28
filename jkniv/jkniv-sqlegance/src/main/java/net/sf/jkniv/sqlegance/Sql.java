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

import java.util.Date;

import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetHoldability;
import net.sf.jkniv.sqlegance.statement.ResultSetType;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.sqlegance.validation.ValidateType;

/**
 * This interface represent Structured Query Language (SQL and some derivations like JPQL, HQL, JPQL, CQL...) 
 * in XML file, each node at XML file is represented by this class. 
 * Those queries can be dynamic or static and to recover a dynamic query (built on-the-fly) is necessary to
 * pass the parameters that can be mounted.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface Sql
{
    String getName();
    
    /**
     * Retrieve static sql from a node, the dynamic parts is skipped.
     * 
     * @return Sql sentence
     */
    String getSql();
    
    /**
     * Retrieve dynamic sql from a node, the dynamic parts is evaluated at time
     * to create the sql.
     * 
     * @param params
     *            parameters to evaluate dynamic sql, can be a object like
     *            Author, Book, etc or a java.util.Map where yours keys is like
     *            the properties.
     * @return Dynamic sql sentence
     */
    String getSql(Object params);
    
    /**
     * Retrieve node type: INSERT, UPDATE, DELETE, SELECT or PROCEDURE.
     * 
     * @return node type declared at xml file.
     */
    SqlType getSqlType();
    
    /**
     * Verify if command is a SELECT
     * @return {@code true} when is SELECT, {@code false} otherwise
     */
    boolean isSelectable();
    
    /**
     * {@link Selectable} instance
     * @return this instance
     * @throws UnsupportedOperationException when this instance isn't {@link Selectable}
     */
    Selectable asSelectable();
    
    /**
     * Verify if command is a INSERT
     * @return {@code true} when is INSERT, {@code false} otherwise
     */
    boolean isInsertable();

    /**
     * {@link Insertable} instance
     * @return this instance
     * @throws UnsupportedOperationException when this instance isn't {@link Insertable}
     */
    Insertable asInsertable();
    
    /**
     * Verify if command is a UPDATE
     * @return {@code true} when is UPDATE, {@code false} otherwise
     */
    boolean isUpdateable();
    
    /**
     * {@link Updateable} instance
     * @return this instance
     * @throws UnsupportedOperationException when this instance isn't {@link Updateable}
     */
    Updateable asUpdateable();
    
    /**
     * Verify if command is a DELETE
     * @return {@code true} when is DELETE, {@code false} otherwise
     */
    boolean isDeletable();
    
    /**
     * {@link Deletable} instance
     * @return this instance
     * @throws UnsupportedOperationException when this instance isn't {@link Deletable}
     */
    Deletable asDeletable();
    
    /**
     * {@link Storable} instance
     * @return this instance
     * @throws UnsupportedOperationException when this instance isn't {@link Storable}
     */
    Storable asStorable();
    
    /**
     * Retrieve the language type used at sql sentence.
     * @return language type declared at xml file.
     */
    LanguageType getLanguageType();
    
    /**
     * A SQL hint can be used on certain database platforms to define how the
     * query uses indexes and other such low level usages. The SQL hint will be
     * included in the SQL, after the SELECT/INSERT/UPDATE/DELETE command. - See
     * more at:
     * http://www.eclipse.org/eclipselink/documentation/2.5/jpa/extensions/q_sql_hint.htm
     * 
     * @return The full hint string, including the comment \ delimiters.
     */
    String getHint();
    
    /**
     * Retrieves the current transaction isolation level for the query.
     * 
     * @return the current transaction isolation level.
     */
    Isolation getIsolation();
    
    /**
     * Retrieves the number of seconds the repository will wait for a Query
     * object to execute. If the limit is exceeded, a RepositoryException is
     * thrown.
     * 
     * @return the current query timeout limit in seconds; zero means there is
     *         no limit
     */
    int getTimeout();
    
    /**
     * Indicate if query is a batch of commands.
     * @return true means is a batch command, false otherwise.
     */
    boolean isBatch();

    ResultSetType getResultSetType();
    
    ResultSetConcurrency getResultSetConcurrency();
    
    ResultSetHoldability getResultSetHoldability();
    
    String getReturnType();

    Class<?> getReturnTypeAsClass(); // TODO test me, null when haven't return type

    //void setResultRow(ResultRow<?, ?> resultRow);

    //ResultRow<?, ?> getResultRow();
    /**
     * Check if return type is managed as {@code javax.persistence.Entity}
     * @return return {@code true} if is annotated as {@code javax.persistence.Entity}, {@code false} otherwise
     */
    boolean isReturnTypeManaged();
    
    /**
     * Timestamp when sql was read from xml
     * @return when sql was read from xml file
     */
    Date getTimestamp();
    
    /**
     * The filename that sql belong to
     * @return return the filename, relative to absolute classpath, where the sql was read.
     */
    String getResourceName();
    
    /**
     * Expression XPATH to read sql
     * @return the XPATH expression to read the sql at xml file.
     */
     String getXPath();
     
     /***
      * strategy to parser the parameters from sql.
      * @return return the implementation of parser (colon, hash or question mark)
      */
     ParamParser getParamParser();
     
     /**
      * Extract the name parameters from dynamic query 
      * @param params parameters from query
      * @return array of parameters names from query, array based-zero length when haven't param.
      * Array of questions marks (?) is returned when the isn't name based.
      */
     String[] extractNames(Object params);
     
     /**
      * Extract the name parameters from sql
      * @param sql sentence
      * @return array of parameters names from query, array based-zero length when haven't param.
      * Array of questions marks (?) is returned when the isn't name based.
      */
     String[] extractNames(String sql);
     
     
     ValidateType getValidateType();
     
     void bind(SqlDialect sqlDialect);
     
     SqlDialect getSqlDialect();
     
     /*
      * Use validator (JSR 303) to perform validation against params
      * @param params parameters of query
      * @throws ConstraintException when some constraint is violated
      */
     //void checkConstraints(Object params);
     
}
