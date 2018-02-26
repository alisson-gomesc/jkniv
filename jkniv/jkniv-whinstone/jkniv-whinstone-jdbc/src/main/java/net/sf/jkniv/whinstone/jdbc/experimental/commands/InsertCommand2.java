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
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.params.AutoBindParams;
import net.sf.jkniv.sqlegance.params.PrepareParamsFactory;
import net.sf.jkniv.sqlegance.params.StatementAdapterOld;
import net.sf.jkniv.whinstone.jdbc.PreparedStatementStrategy;
import net.sf.jkniv.whinstone.jdbc.params.PreparedStatementAdapterOld;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class InsertCommand2 extends AbstractCommand
{
    private final Insertable isql;
    
    public InsertCommand2(Queryable queryable, PreparedStatementStrategy stmtStrategy, Connection conn)
    {
        super(queryable, stmtStrategy, conn);
        this.isql = queryable.getSql().asInsertable();
    }
    
    public <T> T execute()
    {
        Integer affected = 0;
        PreparedStatement stmt = null;
        try
        {
            if (isql.isAutoGenerateKey())
            {
                if (isql.getAutoGeneratedKeyTag().isAutoStrategy())
                {
                    // first execute insert after get keys
                    stmt = prepareStatement();
                    StatementAdapterOld stmtAdapter = new PreparedStatementAdapterOld(stmt, stmtStrategy.getSqlLogger());
                    AutoBindParams prepareParams = PrepareParamsFactory.newPrepareParams(stmtAdapter,
                            isql.getParamParser(), queryable);
                    prepareParams.parameterized(queryable.getParamsNames());
                    affected = stmt.executeUpdate();
                    new SettingAutoGeneratedKey(queryable, isql, handlerException).set(stmt);
                }
                else if (isql.getAutoGeneratedKeyTag().isSequenceStrategy())
                {
                    // first get sequence after execute insert
                    new SettingSequenceGeneratedKey(queryable, isql, conn, handlerException).set();
                    stmt = prepareStatement();
                    StatementAdapterOld stmtAdapter = new PreparedStatementAdapterOld(stmt, stmtStrategy.getSqlLogger());
                    AutoBindParams prepareParams = PrepareParamsFactory.newPrepareParams(stmtAdapter,
                            isql.getParamParser(), queryable);
                    prepareParams.parameterized(queryable.getParamsNames());
                    affected = stmt.executeUpdate();
                }
            }
            else
            {
                stmt = prepareStatement();
                StatementAdapterOld stmtAdapter = new PreparedStatementAdapterOld(stmt, stmtStrategy.getSqlLogger());
                AutoBindParams prepareParams = PrepareParamsFactory.newPrepareParams(stmtAdapter, isql.getParamParser(), queryable);
                prepareParams.parameterized(queryable.getParamsNames());
                affected = stmt.executeUpdate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            handlerException.handle(e);
        }
        finally
        {
            if (stmt != null)// TODO test me (create test with FOR EACH to insert many rows, Cannot insert record. ORA-01000: maximo de cursores abertos excedido
            {
                try
                {
                    stmt.close();
                }
                catch (SQLException ignore)
                {
                }
            }
        }
        return (T) affected;
    }
}
