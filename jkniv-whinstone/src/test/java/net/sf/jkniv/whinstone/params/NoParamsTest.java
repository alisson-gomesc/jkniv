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
package net.sf.jkniv.whinstone.params;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.whinstone.statement.StatementAdapter;


public class NoParamsTest
{
    private StatementAdapter<?, ?> stmtAdapter;
    
    @Before
    public void setUp()
    {
        this.stmtAdapter = mock(StatementAdapter.class);
        given(this.stmtAdapter.execute()).willReturn(1);
    }
    
    @Test
    public void whenInvokeOn()
    {
        NoParams noParams = new NoParams(this.stmtAdapter);
        noParams.on();
        verify(stmtAdapter, never()).execute();
    }

    @Test
    public void whenInvokeOnBulk()
    {
        NoParams noParams = new NoParams(this.stmtAdapter);
        assertThat(noParams.onBulk(), is(1));
        verify(stmtAdapter).execute();
    }
    
    
}
