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

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.CommandHandler;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class AddSequenceKeyJdbcCommand extends AbstractJdbcCommand
{
    private final Insertable isql;
    
    @SuppressWarnings("rawtypes")
    public AddSequenceKeyJdbcCommand(StatementAdapter stmt, Queryable queryable, Connection conn)
    {
        super(stmt, queryable, conn);
        this.isql = queryable.getDynamicSql().asInsertable();
    }

    @Override
    public <T> T execute()
    {
        Integer affected = 0;
        PreparedStatement stmt_off = null;
        try
        {
            // first get sequence after execute insert
            new SettingSequenceGeneratedKey(queryable, isql, conn, handlerException).set();
            queryable.bind(stmt).on();
            affected = stmt.execute();
        }
        catch (Exception e)
        {
            handlerException.handle(e);
        }
        finally
        {
            stmt.close();
            close(stmt_off);
        }
        return (T) affected;
    }
    
}
