package net.sf.jkniv.reflect.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

public class PropertyAccessTest
{
    @Test
    public void whenBuildPropertyAccess()
    {
        PropertyAccess access = new PropertyAccess("name");
        assertThat(access.getFieldName(), is("name"));
        assertThat(access.getReadMethod(), is("getName"));
        assertThat(access.getWriterMethod(), is("setName"));
    }

    @Test @Ignore("needs to implement nested property access")
    public void whenBuildNestedPropertyAccess()
    {
        PropertyAccess access = new PropertyAccess("author.name");
        assertThat(access.getFieldName(), is("name"));
        assertThat(access.getReadMethod(), is("getName"));
        assertThat(access.getWriterMethod(), is("setName"));
    }

}
