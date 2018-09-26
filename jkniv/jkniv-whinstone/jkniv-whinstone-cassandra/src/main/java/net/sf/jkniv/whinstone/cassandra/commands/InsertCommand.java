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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.cassandra.statement.CassandraStatementAdapter;

public class InsertCommand implements Command
{
    private static final Logger                  LOG = LoggerFactory.getLogger(InsertCommand.class);
    private CassandraStatementAdapter<?, String> stmt;
    
    public InsertCommand(CassandraStatementAdapter<?, String> stmt, Queryable queryable)
    {
        super();
        this.stmt = stmt;
    }
    
    @Override
    public <T> T execute()
    {
        Integer rows = stmt.execute();
        return (T) rows;
    }
    
}
