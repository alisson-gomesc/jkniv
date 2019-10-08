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
package net.sf.jkniv.asserts;

/**
 * Assertion enable the programmer to validate arguments, useful for catch error at runtime 
 * when the developer broke a contract.
 * 
 * <p>For example, if a public method does not allow {@code null} arguments
 * or must return not null values, asserts can be used to protect against 
 * contract violations or class's invariants, ensuring preconditions and postconditions. 
 * 
 * @author Alisson Gomes
 * @since 0.6
 */
public interface Assertable
{
    /**
     * Verify if a set objects assure the assertion.
     * @param args set object must assure the assertion.
     * throws Throw a {@code RuntimeException} exception or subclass when the the assertion fails.
     */
    void verify(Object...args);

    /**
     * 
     * @param e The exception must be throw when arguments the assertion fails.
     * @param args Arguments to verify against the assertion
     * throws Throw a specific {@code RuntimeException} exception when the the assertion fails.
     */
    void verify(RuntimeException e, Object...args);

    /**
     * Verify if a the array assure the assertion.
     * @param args the array to check the assertion.
     * throws Throw a {@code RuntimeException} exception or subclass when the the assertion fails.
     */
    void verifyArray(Object[] args);

    /**
     * 
     * @param e The exception must be throw when arguments the assertion fails.
     * @param args the array to check the assertion.
     * throws Throw a specific {@code RuntimeException} exception when the the assertion fails.
     */
    void verifyArray(RuntimeException e, Object[] args);

}
