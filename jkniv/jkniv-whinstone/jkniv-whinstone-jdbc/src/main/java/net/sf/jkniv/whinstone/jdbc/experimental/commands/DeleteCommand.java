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
package net.sf.jkniv.whinstone.jdbc.experimental.commands;

import java.sql.ResultSet;

import net.sf.jkniv.sqlegance.ConnectionAdapter;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.statement.StatementAdapter;
import net.sf.jkniv.whinstone.jdbc.PreparedStatementStrategy;

class DeleteCommand extends AbstractCommand
{
    public DeleteCommand(final Queryable queryable, final PreparedStatementStrategy stmtStrategy, final ConnectionAdapter conn)
    {
        super(queryable, stmtStrategy, conn);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T execute()
    {
        Integer rowsAffected  = 0;
        if (queryable.getSql().isBatch() || queryable.isTypeOfCollection())
        {
            rowsAffected = batchExecute();
        }
        else
        {
            rowsAffected = simpleExecute();
        }
        
        return (T)rowsAffected;
    }
    
    private int batchExecute()
    {
        StatementAdapter<Number, ResultSet> adapterStmt = adapterConn.newStatement(queryable);
        return queryable.bind(adapterStmt).onBatch();
    }
    
    private int simpleExecute()
    {
        StatementAdapter<Number, ResultSet> adapterStmt = adapterConn.newStatement(queryable);
        queryable.bind(adapterStmt).on();
        return adapterStmt.execute();
    }

}
