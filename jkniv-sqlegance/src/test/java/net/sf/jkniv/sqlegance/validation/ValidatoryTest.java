package net.sf.jkniv.sqlegance.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import java.util.Map;

import org.junit.Test;

import net.sf.jkniv.sqlegance.ConstraintException;
import net.sf.jkniv.sqlegance.domain.orm.Author;


public class ValidatoryTest
{
    @Test
    public void shouldBeValidAuthorWhenAnyValidationIsViolated()
    {
        Map<String, String> messages = null;
        
        messages = ValidateType.validate(new Author(), ValidateType.ADD);
        assertThat(messages).isEmpty();
        messages = ValidateType.validate(new Author(), AddValidate.class);
        assertThat(messages).isEmpty();
        
        ValidateType.assertValidate(new Author(), ValidateType.ADD);
        ValidateType.assertValidate(new Author(), AddValidate.class);
    }

    @Test
    public void shoudlReturnErrorMessageWhenFieldIsNotNullForUpdate()
    {
        Map<String, String> messages = ValidateType.validate(new Author(), ValidateType.UPDATE);
        assertThat(messages).containsKey("username");
        assertThat(messages).containsValue("may not be null");        
    }
    
    @Test
    public void shoudlThrowConstraintExceptionWhenFieldIsNotNullForUpdate()
    {
        ConstraintException thrown = catchThrowableOfType(() -> {
            ValidateType.assertValidate(new Author(), ValidateType.UPDATE);
        }, ConstraintException.class);
        
        assertThat(thrown).isInstanceOf(ConstraintException.class);
        assertThat(thrown.getViolationsI18n()).extracting(ValidationMessage::getField)
            .contains("username");
        assertThat(thrown.getViolationsI18n()).extracting(ValidationMessage::getValue)
        .contains("may not be null");
        assertThat(thrown.getViolationsI18n()).extracting(ValidationMessage::getKey)
            .contains("javax.validation.constraints.NotNull.message");
                
    }
}
