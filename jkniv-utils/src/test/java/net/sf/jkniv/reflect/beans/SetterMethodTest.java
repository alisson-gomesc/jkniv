package net.sf.jkniv.reflect.beans;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class SetterMethodTest
{

    @Test
    public void whenCapitalizeSetterMethods()
    {
        Capitalize setterCapital = new SetterMethod();
        assertThat(setterCapital.does("identityName"), is("setIdentityName"));
        assertThat(setterCapital.does("setIdentityName"), is("setIdentityName"));
        assertThat(setterCapital.does("getIdentityName"), is("setGetIdentityName"));
    }
    
    @Test
    public void whenUnCapitalizeSetterMethods()
    {
        SetterMethod setterCapital = new SetterMethod();
        assertThat(setterCapital.undo("IdentityName"), is("identityName"));
        assertThat(setterCapital.undo("setIdentityName"), is("identityName"));
    }

}
