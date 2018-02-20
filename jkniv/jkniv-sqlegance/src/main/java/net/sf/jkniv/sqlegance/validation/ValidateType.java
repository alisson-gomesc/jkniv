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

import java.util.Map;

import javax.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.ConstraintException;

public enum ValidateType
{
    NONE {
        /*
        public boolean applyForGet()    { return false; }
        public boolean applyForList()   { return false; }
        public boolean applyForScalar() { return false; }
        public boolean applyForAdd()    { return false; }
        public boolean applyForUpdate() { return false; }
        public boolean applyForEnrich() { return false; }
        public boolean applyForRemove() { return false; }*/
        public Class<?> getValidateGroup() { return javax.validation.groups.Default.class; }
        Validatory getValidatory() { return ValidateType.novalidator; }
    }, 
    ALL {
        /*public boolean applyForGet()    { return true; }
        public boolean applyForList()   { return true; }
        public boolean applyForScalar() { return true; }
        public boolean applyForAdd()    { return true; }
        public boolean applyForUpdate() { return true; }
        public boolean applyForEnrich() { return true; }
        public boolean applyForRemove() { return true; }*/
        public Class<?> getValidateGroup() { return AllValidate.class; }
        Validatory getValidatory() { return ValidateType.validator; }
    },
    GET {
        /*public boolean applyForGet()    { return true; }
        public boolean applyForList()   { return false; }
        public boolean applyForScalar() { return false; }
        public boolean applyForAdd()    { return false; }
        public boolean applyForUpdate() { return false; }
        public boolean applyForEnrich() { return false; }
        public boolean applyForRemove() { return false; }*/
        public Class<?> getValidateGroup() { return GetValidate.class; }
        Validatory getValidatory() { return ValidateType.validator; }
    }, 
    LIST {
        /*public boolean applyForGet()    { return false; }
        public boolean applyForList()   { return true; }
        public boolean applyForScalar() { return false; }
        public boolean applyForAdd()    { return false; }
        public boolean applyForUpdate() { return false; }
        public boolean applyForEnrich() { return false; }
        public boolean applyForRemove() { return false; }*/
        public Class<?> getValidateGroup() { return ListValidate.class; }
        Validatory getValidatory() { return ValidateType.validator; }
    }, 
    SELECT {
        /*public boolean applyForGet()    { return false; }
        public boolean applyForList()   { return true; }
        public boolean applyForScalar() { return false; }
        public boolean applyForAdd()    { return false; }
        public boolean applyForUpdate() { return false; }
        public boolean applyForEnrich() { return false; }
        public boolean applyForRemove() { return false; }*/
        public Class<?> getValidateGroup() { return SelectValidate.class; }
        Validatory getValidatory() { return ValidateType.validator; }
    }, 
    SCALAR {
        /*public boolean applyForGet()    { return false; }
        public boolean applyForList()   { return false; }
        public boolean applyForScalar() { return false; }
        public boolean applyForAdd()    { return false; }
        public boolean applyForUpdate() { return false; }
        public boolean applyForEnrich() { return false; }
        public boolean applyForRemove() { return false; }*/
        public Class<?> getValidateGroup() { return ScalarValidate.class; }
        Validatory getValidatory() { return ValidateType.validator; }
    }, 
    ADD {
        /*public boolean applyForGet()    { return false; }
        public boolean applyForList()   { return false; }
        public boolean applyForScalar() { return false; }
        public boolean applyForAdd()    { return false; }
        public boolean applyForUpdate() { return false; }
        public boolean applyForEnrich() { return false; }
        public boolean applyForRemove() { return false; }*/
        public Class<?> getValidateGroup() { return AddValidate.class; }
        Validatory getValidatory() { return ValidateType.validator; }
    }, 
    UPDATE {
        /*public boolean applyForGet()    { return false; }
        public boolean applyForList()   { return false; }
        public boolean applyForScalar() { return false; }
        public boolean applyForAdd()    { return false; }
        public boolean applyForUpdate() { return false; }
        public boolean applyForEnrich() { return false; }
        public boolean applyForRemove() { return false; }*/
        public Class<?> getValidateGroup() { return UpdateValidate.class; }
        Validatory getValidatory() { return ValidateType.validator; }
    }, 
    ENRICH {
        /*public boolean applyForGet()    { return false; }
        public boolean applyForList()   { return false; }
        public boolean applyForScalar() { return false; }
        public boolean applyForAdd()    { return false; }
        public boolean applyForUpdate() { return false; }
        public boolean applyForEnrich() { return false; }
        public boolean applyForRemove() { return false; }*/
        public Class<?> getValidateGroup() { return EnrichValidate.class; }
        Validatory getValidatory() { return ValidateType.validator; }
    }, 
    REMOVE {
        /*public boolean applyForGet()    { return false; }
        public boolean applyForList()   { return false; }
        public boolean applyForScalar() { return false; }
        public boolean applyForAdd()    { return false; }
        public boolean applyForUpdate() { return false; }
        public boolean applyForEnrich() { return false; }
        public boolean applyForRemove() { return false; }*/
        public Class<?> getValidateGroup() { return RemoveValidate.class; }
        Validatory getValidatory() { return ValidateType.validator; }
    }; 
    /*
    public abstract boolean applyForGet();
    public abstract boolean applyForList();
    public abstract boolean applyForScalar();
    public abstract boolean applyForAdd();
    public abstract boolean applyForUpdate();
    public abstract boolean applyForEnrich();
    public abstract boolean applyForRemove();
    */
    public abstract Class<?> getValidateGroup();
    abstract Validatory getValidatory();
    
    private static final Validatory novalidator = new NoValidate();
    private static Validatory validator;
    private static final Logger LOG = LoggerFactory.getLogger(ValidateType.class);
    static {
        try 
        {
            validator = new ValidateImpl();
        }
        catch (Exception e)
        {
            LOG.warn("Default Bean validator Factory not found, cannot apply validator");
            validator = new NoValidate();
        }
    }
    
    /**
     * @param type of validate
     * @return enum with valueof {@code type}, ValidateType.NONE if doesn't match
     */
    public static ValidateType get(String type)
    {
        ValidateType validateType = ValidateType.NONE;
        for (ValidateType t : ValidateType.values())
        {
            if (String.valueOf(type).equalsIgnoreCase(t.name()))
            {
                validateType = t;
                break;
            }
        }
        return validateType;
    }

    /**
     * Use validator (JSR 303) to perform validation against domain model,
     * when some constraint is violated {@code ConstraintException} is throw 
     * @param params the domain model to validate
     * @throws ConstraintException when some constraint is violated
     */
    public void assertValidate(Object params)
    {
        getValidatory().assertValidate(params, this);
    }

    /**
     * Use validator (JSR 303) to perform validation against domain model
     * @param params the domain model to validate
     * @return the pairs field and constraints violated, an empty Map is return when any constraint is violated
     */
    public Map<String, String> validate(Object params)
    {
        return getValidatory().validate(params, this);
    }

}
