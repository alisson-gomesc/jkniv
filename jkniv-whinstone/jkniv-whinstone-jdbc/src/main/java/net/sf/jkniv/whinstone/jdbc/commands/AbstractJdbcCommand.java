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
package net.sf.jkniv.whinstone.jdbc.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandAdapter;
import net.sf.jkniv.whinstone.commands.CommandHandler;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

public abstract class AbstractJdbcCommand implements Command
{
    private final static Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractJdbcCommand.class);
    protected HandleableException             handlerException;
    protected CommandHandler                  commandHandler;
    //protected final PreparedStatementStrategy stmtStrategy;
    protected final StatementAdapter<?, ResultSet> stmt;
    protected final Connection                conn;
    protected final Queryable                 queryable;

    protected AbstractJdbcCommand(Queryable queryable, Connection conn) 
    {
        //this.stmtStrategy = new DefaultPreparedStatementStrategy(queryable);
        this.stmt = null;
        this.queryable = queryable;
        this.conn = conn;
    }
    protected AbstractJdbcCommand(StatementAdapter stmt, Queryable queryable, Connection conn) 
    {
        //this.stmtStrategy = new DefaultPreparedStatementStrategy(queryable);
        this.stmt = stmt;
        this.queryable = queryable;
        this.conn = conn;
    }
    
    @Override
    public Command with(HandleableException handlerException)
    {
        this.handlerException = handlerException;
        return this;
    }
    
    @Override
    public Command with(CommandHandler commandHandler)
    {
        this.commandHandler = commandHandler;
        return this;
    }
    
//    public static CommandHandler ofSelect(CommandAdapter cmdAdapter)
//    {
//        return new SelectHandler(cmdAdapter);
//    }

    /*
    UNUSED
    protected PreparedStatement prepareInsertStatement()
    {
        Insertable isql = queryable.getDynamicSql().asInsertable();
        PreparedStatement stmt = null;
        if (isql.isAutoGenerateKey() && isql.getAutoGeneratedKey().isAutoStrategy())
        {
            String[] columns = isql.getAutoGeneratedKey().getColumnsAsArray();
            stmt = stmtStrategy.prepareStatement(conn, columns);
        }
        else
        {
            stmt = stmtStrategy.prepareStatement(conn);
        }
        return stmt;
    }
    */
    /*
    UNUSED
    protected PreparedStatement prepareStatement()
    {
        PreparedStatement stmt = null;
        stmt = stmtStrategy.prepareStatement(conn);
        return stmt;
    }
    */
    protected int batchExecute()
    {
        return queryable.bind(stmt).onBulk();
    }
    
    protected int simpleExecute()
    {
        queryable.bind(stmt).on();
        return stmt.execute();
    }

    
    protected void close(PreparedStatement stmt)
    {
        if (stmt != null)// TODO test me (create test with FOR EACH to insert many rows, Cannot insert record. ORA-01000: maximo de cursores abertos excedido
        {
            try
            {
                stmt.close();
            }
            catch (SQLException ignore)
            {
                LOG.warn("Cannot close PreparedStament state: {}, errorCode: {}, message: {}", ignore.getSQLState(), ignore.getErrorCode(), ignore.getMessage());
            }
        }
    }
}