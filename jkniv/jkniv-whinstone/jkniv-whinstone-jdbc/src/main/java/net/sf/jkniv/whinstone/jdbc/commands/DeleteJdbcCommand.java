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

import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.jdbc.PreparedStatementStrategy;
import net.sf.jkniv.whinstone.jdbc.params.PreparedStatementAdapterOld;
import net.sf.jkniv.whinstone.params.AutoBindParams;
import net.sf.jkniv.whinstone.params.PrepareParamsFactory;
import net.sf.jkniv.whinstone.params.StatementAdapterOld;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

public class DeleteJdbcCommand extends AbstractJdbcCommand
{
    public DeleteJdbcCommand(final Queryable queryable, final Connection conn)
    {
        super(queryable, conn);
    }
    
    public DeleteJdbcCommand(StatementAdapter stmt, Queryable queryable, Connection conn)
    {
        super(stmt, queryable, conn);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T execute()
    {
        Integer rowsAffected = 0;
        try
        {
            if (queryable.getDynamicSql().isBatch() || queryable.isTypeOfBulk())
            {
                rowsAffected = batchExecute();
            }
            else
            {
                rowsAffected = simpleExecute();
            }
        }
        finally
        {
            stmt.close();
        }        
        //  //
        //        PreparedStatement stmt = null;
        //        stmt = prepareStatement();
        //        StatementAdapterOld stmtAdapter = new PreparedStatementAdapterOld(stmt);
        //        AutoBindParams prepareParams = PrepareParamsFactory.newPrepareParams(stmtAdapter, isql.getParamParser(),queryable);
        //        prepareParams.parameterized(queryable.getParamsNames());
        //        rowsAffected = stmt.executeUpdate();
        //  //
        return (T) rowsAffected;
    }
    
    //    public <T> T _execute()
    //    {
    //        Integer affected = 0;
    //        queryable.bind(stmt).on();
    //        affected = stmt.execute();
    //        return (T) affected;
    //    }
    
    private int batchExecute()
    {
        return queryable.bind(stmt).onBatch();
    }
    
    private int simpleExecute()
    {
        queryable.bind(stmt).on();
        return stmt.execute();
    }
    
}
