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

/**
 *  Helper to handle exceptions at configurable way keeping the code clean.
 *  
 * @author Alisson Gomes
 *
 */
public interface HandleableException
{
    /**
     * Mapping {@code root} checked exception to {@code translateTo} unchecked.
     * 
     * @param root checked exception expected
     * @param translateTo translate to another unchecked exception
     * @param message content of exception
     * @return a reference to this object.
     */
    HandleableException config(Class<? extends Exception> root, Class<? extends RuntimeException> translateTo, String message);
    
    /**
     * Mapping {@code root} checked exception to unchecked, {@code RuntimeException}.
     * 
     * @param root checked exception expected
     * @param message content of exception
     * @return a reference to this object.
     */
    HandleableException config(Class<? extends Exception> root, String message);

    void handle(Exception catched);

    void handle(Exception catched, String message);

    /**
     * Throw a Default Exception with a specific message using the specified formatted string and arguments. 
     * @param message a format message, like: {@code can not found method %s for class %s}
     * @param args Arguments referenced by the format specifiers in the format string.
     */
    void throwMessage(String message, Object... args); // TODO unit test

    Class<? extends RuntimeException> getDefaultException();

    void mute();
    
    void mute(Class<? extends Exception> ex);
    
    // TODO implements new methods muteAsWarn, muteAsInfo
    // TODO implements config exception to rethrow them (just
    
    boolean isMute();
    
    boolean isMute(Class<? extends Exception> ex);
}
