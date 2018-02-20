package net.sf.jkniv.reflect.beans;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class SetterMethodTest
{

    @Test
    public void whenCapitalizeSetterMethods()
    {
        SetterMethod setter = new SetterMethod();
        
        assertThat(setter.capitalize("identityName"), is("setIdentityName"));
        assertThat(setter.capitalize("setIdentityName"), is("setIdentityName"));
        assertThat(setter.capitalize("getIdentityName"), is("setGetIdentityName"));
    }
}
