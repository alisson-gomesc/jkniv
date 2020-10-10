package net.sf.jkniv.sqlegance.validation;

import org.junit.Test;

import net.sf.jkniv.sqlegance.domain.orm.Author;


public class ValidatoryTest
{
    @Test
    public void whenGetInstanceOfValidatory()
    {
        ValidateType.validate(new Author(), ValidateType.ADD);
        ValidateType.assertValidate(new Author(), ValidateType.ADD);

        ValidateType.validate(new Author(), AddValidate.class);
        ValidateType.assertValidate(new Author(), AddValidate.class);
    }
    
}
