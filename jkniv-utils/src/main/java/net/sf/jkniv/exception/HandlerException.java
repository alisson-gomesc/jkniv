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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class HandlerException implements HandleableException
{
    private final static Logger               LOG     = LoggerFactory.getLogger(HandlerException.class);
    private final static Assertable           notNull = AssertsFactory.getNotNull();
    private Map<Class<?>, MapException>       exceptions;
    private Class<? extends RuntimeException> defaultException;
    private String                            defaultMessage;
    private boolean                           mute;
    private boolean                           enableLogInfo;
    
    public HandlerException()
    {
        this(RuntimeException.class, "%s", false);
    }
    
    public HandlerException(boolean mute)
    {
        this(RuntimeException.class, "%s", mute);
    }
    
    /**
     * Build a new handler exception where default Runtime exception is {@code RuntimeException}.
     * and your default message is {@code message}
     * @param message default message
     */
    public HandlerException(String message)
    {
        this(RuntimeException.class, message, false);
    }
    
    /**
     * Build a new handler exception where default Runtime exception is {@code defaultException}.
     * and your default message is {@code message}
     * @param defaultException default runtime exception when exception is catch for {@code try / catch} block.
     * @param message default message
     */
    public HandlerException(Class<? extends RuntimeException> defaultException, String message)
    {
        this(defaultException, message, false);
    }
    
    public HandlerException(Class<? extends RuntimeException> defaultException, String message, boolean mute)
    {
        notNull.verify(defaultException, message);
        this.exceptions = new HashMap<Class<?>, MapException>();
        this.defaultException = defaultException;
        this.defaultMessage = message;
        this.mute = mute;
        this.enableLogInfo = false;
        //config(defaultException, defaultException, message);
    }
    
    @Override
    public HandleableException config(Class<? extends Exception> caught, Class<? extends RuntimeException> translateTo,
            String message)
    {
        if (this.exceptions.containsKey(caught))
            throw new UnsupportedOperationException(
                    "Already exist an exception configured to exception [" + caught.getName() + "]");
        
        MapException map = new MapException(caught, translateTo, message, false);
        this.exceptions.put(caught, map);
        return this;
    }
    
    @Override
    public HandleableException config(Class<? extends Exception> caught, String message)
    {
        if (this.exceptions.containsKey(caught))
            throw new UnsupportedOperationException(
                    "Already exist an exception configured to exception [" + caught.getName() + "]");
        
        MapException map = new MapException(caught, this.defaultException, message, false);
        this.exceptions.put(caught, map);
        return this;
    }
    
    @Override
    public void handle(Exception caught)
    {
        if (this.defaultException.isAssignableFrom(caught.getClass()))
            throw (RuntimeException) caught;
        
        RuntimeException theException = prepareToThrowException(null, caught);
        if (theException != null)
            throw theException;
    }
    
    @Override
    public void handle(Exception caught, String customMessage)
    {
        if (this.defaultException.isAssignableFrom(caught.getClass()))
            throw (RuntimeException) caught;
        
        RuntimeException theException = prepareToThrowException(customMessage, caught);
        if (theException != null)
            throw theException;
    }
    
    @Override
    public void throwMessage(String message, Object... args)
    {
        String msg = String.format(message, args);
        RuntimeException re = (RuntimeException) prepareToThrowException(msg, null);
        throw re;
    }
    
    private RuntimeException prepareToThrowException(String customMessage, Exception caught)
    {
        RuntimeException theException = null;
        if (!isMute() && !isMute(caught))
        {
            MapException theMappedException = getMappedException(customMessage, caught);
            /*
             * TODO test me when caught instance of the mapped exception
             */
            try
            {
                Constructor<? extends RuntimeException> constructor = theMappedException.getTranslate()
                        .getConstructor(String.class, Throwable.class);
                theException = constructor
                        .newInstance(buildMessage(theMappedException.getMessage(), customMessage, caught), caught);
                if (caught != null)//  TODO alternative method when caught is null
                    theException.setStackTrace(caught.getStackTrace());
            }
            catch (Exception e)
            {
                if (caught != null)//  TODO alternative method when caught is null
                {
                    theException = new RuntimeException(caught);
                    theException.setStackTrace(caught.getStackTrace());
                }
            }
        }
        else if (enableLogInfo){
            LOG.info("Be careful the Handler exception is mute for configured exceptions [{}], message: {}",
                    caught.getClass().getName(), caught.getMessage());
        }
        return theException;
    }
    
    private MapException getMappedException(String message, Exception rootCause)
    {
        Class<? extends Exception> theException = null;
        String theMessage = null;
        MapException theMappedException = null;
        if (message == null)
            theMessage = this.defaultMessage;
        else
            theMessage = message;
        
        if (rootCause == null)
            theException = this.defaultException;
        else
        {
            theException = rootCause.getClass();
            theMappedException = this.exceptions.get(rootCause.getClass());
        }
        
        if (theMappedException == null)
        {
            theMappedException = new MapException(theException, this.defaultException, theMessage, false);
        }
        return theMappedException;
    }
    
    @Override
    public Class<? extends RuntimeException> getDefaultException()
    {
        return defaultException;
    }
    
    @Override
    public HandleableException mute()
    {
        this.mute = true;
        return this;
    }
    
    @Override
    public HandleableException mute(Class<? extends Exception> clazz)
    {
        //        if (this.exceptions.containsKey(clazz))
        //            throw new UnsupportedOperationException("Already exist an exception configured to exception ["
        //                    + clazz.getName() + "] cannot change mute status from exception");
        
        MapException map = this.exceptions.get(clazz);
        if (map == null)
            map = new MapException(clazz, RuntimeException.class, "", true);
        else
            map.mute();
        this.exceptions.put(clazz, map);
        
        return this;
    }
    
    @Override
    public boolean isMute()
    {
        return this.mute;
    }
    
    private boolean isMute(Exception ex)
    {
        boolean answer = false;
        if (ex == null)
            return answer;
        
        MapException mapException = this.exceptions.get(ex.getClass());
        if (mapException != null)
            answer = mapException.isMute();
        return answer;
    }
    
    @Override
    public boolean isMute(Class<? extends Exception> clazz)
    {
        boolean answer = false;
        MapException mapException = this.exceptions.get(clazz);
        if (mapException != null)
            answer = mapException.isMute();
        return answer;
    }
    
    @Override
    public HandleableException logInfoOn()
    {
        this.enableLogInfo = true;
        return this;
    }

    @Override
    public HandleableException logInfoOff()
    {
        this.enableLogInfo = false;
        return this;
    }

    /**
     * 
     * @param message
     * @param customMessage
     * @return
     */
    private String buildMessage(String message, String customMessage, Exception caught)
    {
        String newMessage = "";// FIXME nullpointer when message has more one parameter. Cannot set parameter [%s] value [%s]
        if (hasParameterAtMessage(message))
            newMessage = String.format(message,
                    (customMessage != null ? customMessage + " " : "") + caught.getMessage()); //newMessage = String.format(message, customMessage);
        else
            newMessage = message;
        return newMessage;
    }
    
    /**
     * Verify if {@code message}has inline parameter 
     * @param message the {@code message}
     * @return return <strong>true</strong> when {@code message} has parameter
     */
    private boolean hasParameterAtMessage(String message)
    {
        return (message.indexOf("%s") >= 0);
    }
}
