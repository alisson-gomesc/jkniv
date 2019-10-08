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
package net.sf.jkniv.whinstone.jpa2.test.infra;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Assert;

public class JndiTest
{
    
    public void testDummyContext() throws NamingException
    {
        Hashtable<String, String> environnement = new Hashtable<String, String>();
        environnement.put(Context.INITIAL_CONTEXT_FACTORY, "net.sf.jkniv.mock.jndi.MyContextFactory");
        
        Context ctx = new InitialContext(environnement);
        Object value = ctx.lookup("jndiName");
        ctx.close();
        Assert.assertEquals("stored value", value);
    }
}
