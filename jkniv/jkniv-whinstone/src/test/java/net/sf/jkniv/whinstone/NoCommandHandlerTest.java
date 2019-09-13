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
package net.sf.jkniv.whinstone;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.sf.jkniv.sqlegance.SqlType;

/**
 * Dummy/Empty implementation for {@link CommandHandler}
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
public class NoCommandHandlerTest
{
    @Test
    public void whenUseNoCommandHander() 
    {
        CommandHandler command = NoCommandHandler.getInstance();
        
        assertThat(command.asCommand(), instanceOf(Command.class));
        assertThat(command.checkSqlType(SqlType.SELECT), instanceOf(CommandHandler.class));
        assertThat(command.checkSqlType(SqlType.INSERT), instanceOf(CommandHandler.class));
        assertThat(command.checkSqlType(SqlType.DELETE), instanceOf(CommandHandler.class));
        assertThat(command.checkSqlType(SqlType.UPDATE), instanceOf(CommandHandler.class));
        assertThat(command.checkSqlType(SqlType.PROCEDURE), instanceOf(CommandHandler.class));
        assertThat(command.checkSqlType(SqlType.UNKNOWN), instanceOf(CommandHandler.class));
        assertThat(command.postCallback(), instanceOf(CommandHandler.class));
        assertThat(command.postCommit(), instanceOf(CommandHandler.class));
        assertThat(command.postException(), instanceOf(CommandHandler.class));
        assertThat(command.preCallback(), instanceOf(CommandHandler.class));
        assertThat(command.run(), nullValue());
    }
}
