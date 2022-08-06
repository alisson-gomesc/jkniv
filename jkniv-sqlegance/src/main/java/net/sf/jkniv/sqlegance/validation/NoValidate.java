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

import java.util.Collections;
import java.util.List;
import java.util.Map;

class NoValidate implements Validatory
{
    
    @Override
    public void assertValidate(Object params, ValidateType validateType)
    {
    }

    @Override
    public <T> void assertValidate(Object params, Class<T> validateGroup)
    {
    }
    
    @Override
    public Map<String, String> validate(Object params, ValidateType validateType)
    {
        return Collections.emptyMap();
    }
    
    @Override
    public <T> Map<String, String> validate(Object params, Class<T> validateGroup)
    {
        return Collections.emptyMap();
    }

    @Override
    public List<ValidationMessage> validateI18n(Object params, ValidateType validateType)
    {
        return Collections.emptyList();
    }

    @Override
    public <T> List<ValidationMessage> validateI18n(Object params, Class<T> validateGroup)
    {
        return Collections.emptyList();
    }
    
}
