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

import net.sf.jkniv.whinstone.Queryable;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
@SuppressWarnings({"unchecked"})
public class AddAutoKeyJdbcCommand extends AbstractJdbcCommand
{
    public AddAutoKeyJdbcCommand(Queryable queryable, Connection conn)
    {
        super(queryable, conn);
    }
    
    @Override
    public <T> T execute()
    {
        Integer affected = 0;
        queryable.bind(stmt).on();
        affected = stmt.execute();
        stmt.bindKey();
        return (T) affected;
    }
}
