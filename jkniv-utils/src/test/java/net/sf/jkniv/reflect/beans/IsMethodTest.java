package net.sf.jkniv.reflect.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class IsMethodTest
{
    @Test
    public void whenCapitalizeSetterMethods()
    {
        Capitalize capitalIs = new IsMethod();
        assertThat(capitalIs.does("identityName"), is("isIdentityName"));
        assertThat(capitalIs.does("isIdentityName"), is("isIdentityName"));
        assertThat(capitalIs.does("isSetIdentityName"), is("isSetIdentityName"));
        assertThat(capitalIs.getPropertyType(), is(Capitalize.PropertyType.IS));
    }
    
    @Test
    public void whenUnCapitalizeSetterMethods()
    {
        Capitalize capitalIs = new IsMethod();
        assertThat(capitalIs.undo("IdentityName"), is("identityName"));
        assertThat(capitalIs.undo("isIdentityName"), is("identityName"));
        assertThat(capitalIs.getPropertyType(), is(Capitalize.PropertyType.IS));
    }
    
}
