package net.sf.jkniv.reflect.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class GetterMethodTest
{
    @Test
    public void whenCapitalizeSetterMethods()
    {
        Capitalize getterCapital = new GetterMethod();
        assertThat(getterCapital.does("identityName"), is("getIdentityName"));
        assertThat(getterCapital.does("getIdentityName"), is("getIdentityName"));
        assertThat(getterCapital.does("getSetIdentityName"), is("getSetIdentityName"));
    }
    
    @Test
    public void whenUnCapitalizeSetterMethods()
    {
        Capitalize getterCapital = new GetterMethod();
        assertThat(getterCapital.undo("IdentityName"), is("identityName"));
        assertThat(getterCapital.undo("getIdentityName"), is("identityName"));
    }
    
}
