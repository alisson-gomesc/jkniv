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
package net.sf.jkniv.whinstone.types;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The converter is used to change the value/type from original object
 * to a destiny object.
 * 
 * Just Methods and Fields can be annotated.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Converter
{
    enum EnumType {NONE, STRING, ORDINAL};
    
    /**
     * Specifies the converter to be used.
     * @return {@code void.class} as default
     */
    Class converter() default void.class;
    
    /**
     * The pattern describing to be used to define or formatter a value.
     * @return empty pattern as default
     */
    String pattern() default "";
    
    /**
     * the enum type {@code ORDINAL} or {@code STRING}
     * @return {@code EnumType.NONE} as default
     */
    EnumType isEnum() default EnumType.NONE;
}
