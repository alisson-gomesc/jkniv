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
package net.sf.jkniv.whinstone.cassandra.commands;

import net.sf.jkniv.whinstone.Queryable;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class AddSequenceKeyJdbcCommand extends DefaultCommand
{
    public AddSequenceKeyJdbcCommand(Queryable queryable)
    {
        super(queryable);
    }

    @Override
    public <T> T execute()
    {
        Integer affected = 0;
        try
        {
            // first get sequence after execute insert
            stmt.bindKey();
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
        }
        return (T) affected;
    }
    
}
