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
package net.sf.jkniv.whinstone.couchbase.commands;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandHandler;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class N1qlCommand implements Command
{
    private StatementAdapter stmt;
    
    public N1qlCommand(Queryable queryable)
    {
        super();
    }

    @Override
    public <T> Command with(T stmt)
    {
        this.stmt = (StatementAdapter)stmt;
        return this;
    }
    @Override
    public Command with(HandleableException handleableException)
    {
        return this;
    }

    @Override
    public Command with(CommandHandler commandHandler)
    {
        return this;
    }

    @Override
    public <T> T execute()
    {
        T list = (T) stmt.rows();
      return list;
    }
}
