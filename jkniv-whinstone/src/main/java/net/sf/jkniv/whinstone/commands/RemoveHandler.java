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
package net.sf.jkniv.whinstone.commands;

import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandAdapter;
import net.sf.jkniv.whinstone.commands.DefaultCommandHandler;

/**
 * JDBC Command to handler the {@code Remove} life-cycle.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class RemoveHandler extends DefaultCommandHandler
{
    public RemoveHandler(CommandAdapter cmdAdapter)
    {
        super(cmdAdapter);
    }
    
    @Override
    public Command asCommand()
    {
        Command c = getCommandAdapter().asRemoveCommand(queryable);
        c.with(this);
        c.with(this.handleableException);
        return c;
    }
}
