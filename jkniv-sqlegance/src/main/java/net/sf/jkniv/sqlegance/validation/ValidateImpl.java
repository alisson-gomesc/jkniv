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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.ConstraintException;

class ValidateImpl implements Validatory
{
    private final static Logger LOG = LoggerFactory.getLogger(ValidateImpl.class);
    private static Validator    validator;
    static
    {
        
        try
        {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }
        catch (Exception e)
        {
            LOG.warn(
                    "Implementation for JSR Bean Validation not found! Add validator jar at classpah like hibernate-validator to works.");
        }
    }
    
    @Override
    public void assertValidate(Object params, ValidateType validateType)
    {
        if (validator == null)
            return;
        List<ValidationMessage> constraints = validateI18n(params, validateType.getValidateGroup());
        if (!constraints.isEmpty())
            throw new ConstraintException(constraints);
    }
    
    @Override
    public <T> void assertValidate(Object params, Class<T> validateGroup)
    {
        List<ValidationMessage> constraints = validateI18n(params, validateGroup);
        if (!constraints.isEmpty())
            throw new ConstraintException(constraints);
    }
    
    @Override
    public Map<String, String> validate(Object params, ValidateType validateType)
    {
        return validate(params, validateType.getValidateGroup());
    }
    
    @Override
    public <T> Map<String, String> validate(Object params, Class<T> validateGroup)
    {
        if (validator == null)
            return Collections.emptyMap();
        Set<ConstraintViolation<Object>> violations = validator.validate(params, validateGroup);
        Map<String, String> constraints = new HashMap<String, String>(violations.size());
        
        for (ConstraintViolation<Object> violation : violations)
        {
            /*
            LOG.info("ConstraintDescriptor={}", violation.getConstraintDescriptor());
            LOG.info("InvalidValue={}", violation.getInvalidValue());
            LOG.info("PropertyPath={}", violation.getPropertyPath());
            LOG.info("PropertyPath={}", violation.getPropertyPath());
            LOG.info("PropertyPath.Node={}", violation.getPropertyPath().iterator().next().getName());            
            */
            String name = violation.getPropertyPath().iterator().next().getName();
            if (name == null && violation.getRootBeanClass() != null)
                name = violation.getRootBeanClass().getSimpleName();
            constraints.put(name, violation.getMessage());
        }
        return constraints;
    }

    @Override
    public List<ValidationMessage> validateI18n(Object params, ValidateType validateType)
    {
        return validateI18n(params, validateType.getValidateGroup());
    }

    @Override
    public <T> List<ValidationMessage> validateI18n(Object params, Class<T> validateGroup)
    {
        if (validator == null)
            return Collections.emptyList();
        
        Set<ConstraintViolation<Object>> violations = validator.validate(params, validateGroup);
        List<ValidationMessage> constraints = new ArrayList<ValidationMessage>(violations.size());
        
        for (ConstraintViolation<Object> violation : violations)
        {
            String field = violation.getPropertyPath().iterator().next().getName();
            if (field == null && violation.getRootBeanClass() != null)
                field = violation.getRootBeanClass().getSimpleName();
            
            
            //{javax.validation.constraints.NotNull.message} -> javax.validation.constraints.NotNull.message
            String key = violation.getMessageTemplate().substring(1, violation.getMessageTemplate().length()-1);
            constraints.add(ValidationMessageImpl.of(field, violation.getMessage(), key));
        }
        return constraints;
    }
}
