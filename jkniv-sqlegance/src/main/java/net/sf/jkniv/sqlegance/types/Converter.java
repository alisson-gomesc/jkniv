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
package net.sf.jkniv.sqlegance.types;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The converter is used to change the value/type from original object
 * to a destiny object.
 * 
 * Just methods can be annotated because whinstone not violate yours objects.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Converter
{
    /**
     * Specifies the converter to be used.
     */
    Class converter() default void.class;
    
    /**
     * The pattern describing to be used to define or formatter a value.
     */
    String pattern() default "";

    /**
     * Used to make constraints when null values is not allowed into converter process.
     */
    boolean allowNull() default true;
}
