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
package net.sf.jkniv.whinstone.jdbc.transaction;

import java.sql.ResultSet;
import java.util.List;

import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.transaction.TransactionStatus;
import net.sf.jkniv.sqlegance.transaction.Transactional;

public interface Work
{
    String getContextName();
    
    TransactionStatus getTransactionStatus();
    
    Transactional getTransaction();
    
    int execute(final Queryable queryable);
    
    <T> List<T> select(final Queryable queryable);
    
    <T> List<T> select(final Queryable queryable, Class<T> returnType);
    
    <T> List<T> select(final Queryable query, Class<T> returnType, ResultRow<T, ResultSet> parserRow);
    
    void close();
}
