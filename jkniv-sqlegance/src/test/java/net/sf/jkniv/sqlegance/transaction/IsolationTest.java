/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.sqlegance.transaction;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.Test;

import net.sf.jkniv.sqlegance.transaction.Isolation;

public class IsolationTest
{
    
    @Test(expected=UnsupportedOperationException.class)
    public void whenIsolationLevelDefaultExpectedCatchException()
    {
        Isolation.DEFAULT.level();
    }

    @Test
    public void whenIsolationLevelNoneMatchConnectionValue()
    {
        Assert.assertTrue(Isolation.NONE.level() == Connection.TRANSACTION_NONE);
    }

    @Test
    public void whenIsolationLevelReadCommittedMatchConnectionValue()
    {
        Assert.assertTrue(Isolation.READ_COMMITTED.level() == Connection.TRANSACTION_READ_COMMITTED);
    }
    
    @Test
    public void whenIsolationLevelReadUncommittedMatchConnectionValue()
    {
        Assert.assertTrue(Isolation.READ_UNCOMMITTED.level() == Connection.TRANSACTION_READ_UNCOMMITTED);
    }
    
    @Test
    public void whenIsolationLevelSerializableMatchConnectionValue()
    {
        Assert.assertTrue(Isolation.SERIALIZABLE.level() == Connection.TRANSACTION_SERIALIZABLE);
    }
}
