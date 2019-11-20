package net.sf.jkniv.reflect.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class GetterMethodTest
{
    @Test
    public void whenCapitalizeSetterMethods()
    {
        Capitalize capitalGet = new GetterMethod();
        assertThat(capitalGet.does("identityName"), is("getIdentityName"));
        assertThat(capitalGet.does("getIdentityName"), is("getIdentityName"));
        assertThat(capitalGet.does("getSetIdentityName"), is("getSetIdentityName"));
        assertThat(capitalGet.getPropertyType(), is(Capitalize.PropertyType.GET));
    }
    
    @Test
    public void whenUnCapitalizeSetterMethods()
    {
        Capitalize capitalGet = new GetterMethod();
        assertThat(capitalGet.undo("IdentityName"), is("identityName"));
        assertThat(capitalGet.undo("getIdentityName"), is("identityName"));
        assertThat(capitalGet.getPropertyType(), is(Capitalize.PropertyType.GET));
    }
    
}
