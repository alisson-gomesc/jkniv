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

import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class AddAutoKeyJdbcCommand extends AbstractJdbcCommand
{
    @SuppressWarnings("rawtypes")
    public AddAutoKeyJdbcCommand(StatementAdapter stmt, Queryable queryable, Connection conn)
    {
        super(stmt, queryable, conn);
    }
    
    @Override
    public <T> T execute()
    {
        Integer affected = 0;
        PreparedStatement stmt_off = null;
        try
        {
            queryable.bind(stmt).on();
            affected = stmt.execute();
            stmt.bindKeys();
        }
        catch (Exception e)
        {
            handlerException.handle(e);
        }
        finally
        {
            close(stmt_off);
        }
        return (T) affected;
    }
}
