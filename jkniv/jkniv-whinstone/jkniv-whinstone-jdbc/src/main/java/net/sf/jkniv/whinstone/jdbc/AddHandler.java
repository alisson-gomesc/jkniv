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
package net.sf.jkniv.whinstone.jdbc;

import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.DefaultCommandHandler;

/**
 * JDBC Command to handler the {@code Add} life-cycle.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class AddHandler extends DefaultCommandHandler
{
    public AddHandler(ConnectionAdapter connAdapter)
    {
        super(connAdapter);
        with(this);
    }
    
    @Override
    public Command asCommand()
    {
        Command c = getConnectionAdapter().asAddCommand(queryable);
        c.with(this);
        c.with(this.handleableException);
        return c;
    }
}
