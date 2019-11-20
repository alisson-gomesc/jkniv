package net.sf.jkniv.reflect.beans;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class SetterMethodTest
{

    @Test
    public void whenCapitalizeSetterMethods()
    {
        Capitalize capitalSet = new SetterMethod();
        assertThat(capitalSet.does("identityName"), is("setIdentityName"));
        assertThat(capitalSet.does("setIdentityName"), is("setIdentityName"));
        assertThat(capitalSet.does("getIdentityName"), is("setGetIdentityName"));
        assertThat(capitalSet.getPropertyType(), is(Capitalize.PropertyType.SET));
    }
    
    @Test
    public void whenUnCapitalizeSetterMethods()
    {
        SetterMethod capitalSet = new SetterMethod();
        assertThat(capitalSet.undo("IdentityName"), is("identityName"));
        assertThat(capitalSet.undo("setIdentityName"), is("identityName"));
        assertThat(capitalSet.getPropertyType(), is(Capitalize.PropertyType.SET));
    }

}
