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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import net.sf.jkniv.domain.flat.AuthorFlat;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.xml.TagFactory;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandHandler;
import net.sf.jkniv.whinstone.commands.DefaultCommandHandler;
import net.sf.jkniv.whinstone.commands.NoCommandHandler;
import net.sf.jkniv.whinstone.types.Convertible;
import net.sf.jkniv.whinstone.types.NoConverterType;

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
        
        //command.with(newCommandHandler());
        command.with(new HandlerException());
        command.with(QueryFactory.of("dummy"));
        //command.with(new RepositoryConfig());
        command.with(newResultRow());
        command.with(TagFactory.newSelect("dummy", LanguageType.NATIVE));
        
        assertThat(command.postCallback(), instanceOf(CommandHandler.class));
        assertThat(command.postCommit(), instanceOf(CommandHandler.class));
        assertThat(command.postException(), instanceOf(CommandHandler.class));
        assertThat(command.preCallback(), instanceOf(CommandHandler.class));
        assertThat(command.run(), nullValue());
    }
    
    private CommandHandler newCommandHandler()
    {
        CommandHandler handler = new DefaultCommandHandler(null)
        {
            
            @Override
            public Command asCommand()
            {
                return null;
            }
        };
        return handler;
    }
    
    private ResultRow<AuthorFlat,ResultSet> newResultRow() {
        
        return new ResultRow<AuthorFlat, ResultSet>()
        {

            @Override
            public AuthorFlat row(ResultSet rs, int rownum) throws SQLException
            {
                return null;
            }

            @Override
            public Transformable<AuthorFlat> getTransformable()
            {
                return null;
            }

            @Override
            public void setColumns(JdbcColumn<ResultSet>[] columns)
            {
            }
        };
    }
}
