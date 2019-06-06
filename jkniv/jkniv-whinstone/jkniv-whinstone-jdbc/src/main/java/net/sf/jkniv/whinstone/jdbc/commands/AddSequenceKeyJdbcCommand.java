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
import java.sql.SQLException;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.CommandHandler;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.jdbc.PreparedStatementStrategy;
import net.sf.jkniv.whinstone.jdbc.params.PreparedStatementAdapterOld;
import net.sf.jkniv.whinstone.params.AutoBindParams;
import net.sf.jkniv.whinstone.params.PrepareParamsFactory;
import net.sf.jkniv.whinstone.params.StatementAdapterOld;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
<<<<<<< Upstream, based on origin/0.6.0.M47
public class AddSequenceKeyJdbcCommand extends JdbcAbstractCommand
{
    private final Insertable isql;
    
    public AddSequenceKeyJdbcCommand(Queryable queryable, PreparedStatementStrategy stmtStrategy, Connection conn)
    {
        super(queryable, stmtStrategy, conn);
        this.isql = queryable.getDynamicSql().asInsertable();
    }
    
    @Override
    public Command with(HandleableException handlerException)
    {
        this.handlerException = handlerException;
        return this;
    }
    
    @Override
    public Command with(CommandHandler commandHandler)
    {
        this.commandHandler = commandHandler;
        return this;
    }
    
    @Override
    public <T> T execute()
    {
        Integer affected = 0;
        PreparedStatement stmt = null;
        try
        {
            // first get sequence after execute insert
            new SettingSequenceGeneratedKey(queryable, isql, conn, handlerException).set();
            stmt = prepareStatement();
=======
public class AddSequenceKeyJdbcCommand extends AbstractJdbcCommand
{
    private final Insertable isql;
    
    public AddSequenceKeyJdbcCommand(Queryable queryable, Connection conn)
    {
        super(queryable, conn);
        this.isql = queryable.getDynamicSql().asInsertable();
    }
    
    @Override
    public Command with(HandleableException handlerException)
    {
        this.handlerException = handlerException;
        return this;
    }
    
    @Override
    public Command with(CommandHandler commandHandler)
    {
        this.commandHandler = commandHandler;
        return this;
    }
    
    @Override
    public <T> T execute()
    {
        Integer affected = 0;
        PreparedStatement stmt = null;
        try
        {
            // first get sequence after execute insert
            new SettingSequenceGeneratedKey(queryable, isql, conn, handlerException).set();
            stmt = prepareInsertStatement();
>>>>>>> 3a27083 whinstone-jdbc move code REMOVE to work with Command and CommandHandler
            StatementAdapterOld stmtAdapter = new PreparedStatementAdapterOld(stmt);
            AutoBindParams prepareParams = PrepareParamsFactory.newPrepareParams(stmtAdapter, isql.getParamParser(),
                    queryable);
            prepareParams.parameterized(queryable.getParamsNames());
            affected = stmt.executeUpdate();
        }
        catch (Exception e)
        {
            handlerException.handle(e);
        }
        finally
        {
            close(stmt);
        }
        return (T) affected;
    }
    
}
