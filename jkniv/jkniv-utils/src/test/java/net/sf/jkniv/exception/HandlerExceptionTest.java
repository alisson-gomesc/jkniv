/* 
 * JKNIV, utils - Helper utilities for jdk code.
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
package net.sf.jkniv.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.overload.Foo;

public class HandlerExceptionTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();
    
    @Test
    public void whenMessageFormatWithArray()
    {
        Object[] args = {"5", "3", "8"};
        String msg = "when calculate %s + %s = %s";
        String formatted = String.format(msg, args);
        assertThat(formatted, is("when calculate 5 + 3 = 8"));
    }
    
    @Test(expected=NullPointerException.class)
    public void whenThrowNull()
    {
        throw null;
    }
    
    @Test
    public void whenHandleModifyOriginalMessageWithParams()
    {
        catcher.expect(MyUncheckedException.class);
        catcher.expectMessage("Hit IllegalStateException cannot be invoked");
        HandleableException handler = new HandlerException(MyUncheckedException.class, "");
        handler.config(IllegalStateException.class, "%s cannot be invoked");
        try 
        {
            throw new IllegalStateException("Hit IllegalStateException");
        }
        catch(IllegalStateException e)
        {
            handler.handle(e);
        }
    }
    
    
    /*
    @Test
    public void whenThrowDefaultException() throws Exception
    {
        final String message = "General Error";
        HandleableException handler = new DefaultHandleException(message);
        assertExpectedException(handler, handler.getDefaultException(), message);
    }
    */
    
    /*
    @Test
    public void whenThrowMyException() throws Exception
    {
        final String message = "My General Error";
        HandleableException handler = new DefaultHandleException();
        handler.config(Exception.class, MyUncheckedException.class, message);
        assertExpectedException(handler, MyUncheckedException.class, message);
    }
    */
    
    @Test(expected=RuntimeException.class)
    public void whenTryChangeMuteStatusFromExceptionExists() throws Exception
    {
        HandleableException handler = new HandlerException();
        handler.config(NullPointerException.class, RuntimeException.class, "The Nullpointer is not mute");
        handler.mute(NullPointerException.class);
    }
    
    /*
    @Test
    public void whenMuteAllExceptions() throws Exception
    {
        HandleableException handler = new DefaultHandleException(true);
        try
        {
            throwException();
        }
        catch (Exception e)
        {
            handler.handle(e);
        }
    }
    

    @Test
    public void whenMuteSpecificException() throws Exception
    {
        HandleableException handler = new DefaultHandleException();
        handler.mute(NullPointerException.class);
        try
        {
            throwNullPointerException();
        }
        catch (Exception e)
        {
            handler.handle(e);
        }
    }
*/
    /*
    @Test(expected=RuntimeException.class)
    public void whenNotMuteAllExceptions() throws Exception
    {
        HandleableException handler = new DefaultHandleException();
        try
        {
            throwException();
        }
        catch (Exception e)
        {
            handler.handle(e);
        }
    }
    
*/
    private void assertExpectedException(HandleableException handler, Class<?> expected, String message)
            throws Exception
    {
        try
        {
            throwException();
        }
        catch (Exception e)
        {
            try
            {
                handler.handle(e);
            }
            catch (Exception cautgh)
            {
                assertException(cautgh, expected, message);
            }
        }
    }
    
    
    private void assertException(Exception result, Class<?> expected, String messageExpected)
    {
        //result.printStackTrace();
        Assert.assertThat("Classname not matcch", result.getClass().getName(), is(expected.getName()));
        Assert.assertThat("Exception message not match", result.getMessage(), is(messageExpected));
    }
    
    private void throwRuntimeException()
    {
        throw new RuntimeException("RuntimeException error");
    }
    
    private void throwException() throws Exception
    {
        throw new Exception("Exception error");
    }
    
    private void throwNullPointerException()
    {
        throw new NullPointerException("NullPointerException error");
    }
    
    private void throwIllegalArgumentException()
    {
        throw new IllegalArgumentException("IllegalArgumentException error");
    }
    
}
