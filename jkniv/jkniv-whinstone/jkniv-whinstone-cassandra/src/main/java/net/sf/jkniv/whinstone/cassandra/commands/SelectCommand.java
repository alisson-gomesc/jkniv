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

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.CommandHandler;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.cassandra.statement.CassandraStatementAdapter;

@SuppressWarnings("unchecked")
public class SelectCommand implements Command
{
    //private static final Logger LOG = LoggerFactory.getLogger(SelectCommand.class);
    //private String body;
    private CassandraStatementAdapter<?, String> stmt;
    //private Queryable queryable;
    
    public SelectCommand(CassandraStatementAdapter<?, String> stmt, Queryable queryable)
    {
        super();
        //this.queryable = queryable;
        this.stmt = stmt;
        //stmt.rows();
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
