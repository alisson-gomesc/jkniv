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
package net.sf.jkniv.sqlegance.validation;

import java.util.List;
import java.util.Map;

import net.sf.jkniv.sqlegance.ConstraintException;

public interface Validatory
{
    /**
     * Use validator (JSR 303) to perform validation against domain model
     * @param params the domain model to validate
     * @param validateType type of validation
     * @throws ConstraintException when some constraint is violated
     */
    void assertValidate(Object params, ValidateType  validateType);

    /**
     * Use validator (JSR 303) to perform validation against domain model
     * @param params the domain model to validate
     * @param validateGroup type of validation
     * @throws ConstraintException when some constraint is violated
     */
    <T> void assertValidate(Object params, Class<T> validateGroup);
    
    /**
     * Use validator (JSR 303) to perform validation against domain model
     * @param params the domain model to validate
     * @param validateType type of validation
     * @return the pairs field and constraints violated, an empty Map is return when any constraint is violated
     */
    Map<String, String> validate(Object params, ValidateType  validateType);
    
    /**
     * Use validator (JSR 303) to perform validation against domain model
     * @param params the domain model to validate
     * @param validateGroup type of validation
     * @return the pairs field and constraints violated, an empty Map is return when any constraint is violated
     */
    <T> Map<String, String> validate(Object params, Class<T> validateGroup);

    /**
     * Use validator (JSR 303) to perform validation against domain model
     * @param params the domain model to validate
     * @param validateType type of validation
     * @return the list of messages with constraints violated, an empty List is return when any constraint is violated
     */
    List<ValidationMessage> validateI18n(Object params, ValidateType  validateType);

    /**
     * Use validator (JSR 303) to perform validation against domain model
     * @param params the domain model to validate
     * @param validateGroup type of validation
     * @return the list of messages with constraints violated, an empty List is return when any constraint is violated
     */
    <T> List<ValidationMessage> validateI18n(Object params, Class<T> validateGroup);

}
