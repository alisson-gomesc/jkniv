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

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;

/**
 * A command handler to keep the life-cycle to be executed
 * the {@link Queryable} and {@link Command} for a repository. 
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface CommandHandler
{
    /**
     * Configure the life-cycle with a {@link Queryable}
     * @param queryable query name and yours parameters
     * @return a reference to this object.
     */
    CommandHandler with(Queryable queryable);

    /**
     * Configure the life-cycle with a {@link Sql} 
     * @param sql Dynamic SQL
     * @return a reference to this object.
     */
    CommandHandler with(Sql sql);
    
    /*
     * return type
     * @param returnType type of return
     * @return instance of this object
     */
   // CommandHandler returnType(Class<?> returnType);

    /**
     * Configure the life-cycle with a properties configuration of the repository
     * @param repositoryConfig configuration from repository
     * @return a reference to this object.
     */
    CommandHandler with(RepositoryConfig repositoryConfig);

    /**
     * Configure this life-cycle of command handler
     * @param handler The Command Handler with life-cycle implementation
     * @return a reference to this object.
     */
    CommandHandler with(CommandHandler handler);
    
    /**
     * Configure the life-cycle with a custom result parser rows of query.
     * @param customResultRow customized result row to parser
     * @return a reference to this object.
     */
    CommandHandler with(ResultRow<?, ?> customResultRow);

    /**
     * Configure the life-cycle with the handler exception
     * @param handlerException rules to handler the all exceptions
     * @return a reference to this object.
     */
    CommandHandler with(HandleableException handlerException);

    /**
     * Retrieve the command implementation for the 
     * Repository like: Update, Select, Batch etc
     * @return the specific implementation for the command.
     */
    Command asCommand();
    
    /**
     * Invoke all pre callback methods configured 
     * for the parameters of {@link Queryable}
     * @return a reference to this object.
     */
    CommandHandler preCallback();

    /**
     * Invoke all post callback methods configured 
     * for the parameters of {@link Queryable}
     * @return a reference to this object.
     */
    CommandHandler postCallback();

    /**
     * Invoke all post callback methods, configured to be
     * executed after a successful commit, for the 
     * parameters of {@link Queryable}.
     * @return a reference to this object.
     */
    CommandHandler postCommit();

    /**
     * Invoke all post callback methods, configured to be
     * executed after a failure, for the parameters of 
     * {@link Queryable}.
     * @return a reference to this object.
     */
    CommandHandler postException();
    
    /**
     * Execute the database command
     * @param <T> Generic type of return, example: rows affected by a command or list of objects.
     * @return the result of the command execution
     */
    <T> T run();
    
    
    /**
     * If the command isn't the sql type expected an illegal argument exception is throw.
     * @param expected type of SQL expected
     * @return a reference to this object.
     * @throws IllegalArgumentException if {@link Sql} isn't the expected {@code SqlType}
     */
    CommandHandler checkSqlType(SqlType expected);
}
