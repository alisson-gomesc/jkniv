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
package net.sf.jkniv.whinstone.jpa2.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Iterator;

import org.slf4j.Logger;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.CommandHandler;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class BulkJpaCommand implements Command//extends AbstractJdbcCommand
{
        private final static Logger LOG = org.slf4j.LoggerFactory.getLogger(BulkJpaCommand.class);
        protected HandleableException             handlerException;
        protected CommandHandler                  commandHandler;
        protected final StatementAdapter<?, ResultSet> stmt;
        protected final Queryable                 queryable;
        //protected final PreparedStatementStrategy stmtStrategy;
        //protected final Connection                conn;

    public BulkJpaCommand(StatementAdapter stmt, Queryable queryable, Connection conn)
    {
        this.stmt = stmt;
        this.queryable = queryable;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T execute()
    {
        Integer rowsAffected = 0;
        try
        {
            Iterator<Object> it = queryable.iterator();
            while (it.hasNext())
            {
                Queryable queryableIt = QueryFactory.of(queryable.getName(), it.next(), queryable.getOffset(), queryable.getMax());
                //QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(em, sqlContext, queryableIt, null);
                //rowsAffected += queryableJpaAdapter.executeUpdate();
            }

            //if (queryable.getDynamicSql().isBatch() || queryable.isTypeOfBulk())
            //rowsAffected = this.queryable.bind(stmt).onBulk();
        }
        finally
        {
            stmt.close();
        }
        return (T) rowsAffected;
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
}
