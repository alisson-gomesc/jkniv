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

import java.sql.Connection;

import net.sf.jkniv.sqlegance.ConnectionAdapter;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.whinstone.jdbc.PreparedStatementStrategy;

public class DbCommandFactory
{
    
    public static DbCommand newInstance(
            Queryable queryable, 
            Connection conn,
            PreparedStatementStrategy stmtStrategy)
    {
        if(queryable.getDynamicSql().isInsertable())
            return new InsertCommand2(queryable, stmtStrategy, conn);
//        else if (queryable.getSql().isUpdateable())
//            return new UpdateCommand(queryable, stmtStrategy, conn);
//        else if (queryable.getSql().isUpdateable())
//            return new DeleteCommand(queryable, stmtStrategy, conn);
        else
            throw new UnsupportedOperationException("Cannot execute a command different of INSERT|UPDATE|DELETE");
    }
    
    public static DbCommand newInstance(
            Queryable queryable, ConnectionAdapter conn,
            PreparedStatementStrategy stmtStrategy)
    {
        //if(queryable.getSql().isInsertable())
        //    return new InsertCommand2(queryable, stmtStrategy, conn);
        //else 
        if (queryable.getDynamicSql().isUpdateable())
            return new UpdateCommand(queryable, stmtStrategy, conn);
        else if (queryable.getDynamicSql().isDeletable())
            return new DeleteCommand(queryable, stmtStrategy, conn);
        else
            throw new UnsupportedOperationException("Cannot execute a command different of INSERT|UPDATE|DELETE");
    }

}
