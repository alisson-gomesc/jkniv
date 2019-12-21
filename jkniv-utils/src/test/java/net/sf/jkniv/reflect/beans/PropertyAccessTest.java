package net.sf.jkniv.reflect.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.sf.jkniv.acme.domain.Author;
import net.sf.jkniv.acme.domain.Book;

public class PropertyAccessTest
{
    @Test
    public void whenBuildPropertyAccess()
    {
        PropertyAccess access = new PropertyAccess("name");
        assertThat(access.getFieldName(), is("name"));
        assertThat(access.getReadMethodName(), is("getName"));
        assertThat(access.getWriterMethodName(), is("setName"));
        assertThat(access.isNestedField(), is(false));
    }

    @Test
    public void whenBuildPropertyAccessByConstructor()
    {
        PropertyAccess access = new PropertyAccess("name", "getName", "setName");
        assertThat(access.getFieldName(), is("name"));
        assertThat(access.getReadMethodName(), is("getName"));
        assertThat(access.getWriterMethodName(), is("setName"));
        assertThat(access.isNestedField(), is(false));
        assertThat(access.getTargetClass(), nullValue());

        access = new PropertyAccess("name", "withName", null);
        assertThat(access.getFieldName(), is("name"));
        assertThat(access.getReadMethodName(), is("withName"));
        assertThat(access.getWriterMethodName(), nullValue());
        assertThat(access.getTargetClass(), nullValue());
        assertThat(access.isNestedField(), is(false));
    }

    
    @Test
    public void whenBuildNestedPropertyAccess()
    {
        PropertyAccess access = new PropertyAccess("author.name");
        assertThat(access.getFieldName(), is("author.name"));
        assertThat(access.getReadMethodName(), is("getName"));
        assertThat(access.getWriterMethodName(), is("setName"));
        assertThat(access.isNestedField(), is(true));
    }

    @Test
    public void whenBuildNestedPropertyAccessResolveMethodAndField()
    {
        PropertyAccess access = new PropertyAccess("author.name", Book.class);
        assertThat(access.getFieldName(), is("author.name"));
        assertThat(access.getReadMethodName(), is("getName"));
        assertThat(access.getWriterMethodName(), is("setName"));
        assertThat(access.isNestedField(), is(true));
        assertThat(access.getField().getName(), is("name"));
        assertThat(access.getField().getDeclaringClass().getName(), is(Author.class.getName()));
        assertThat(access.getReadMethod().getName(), is(access.getReadMethodName()));
        assertThat(access.getReadMethod().getDeclaringClass().getName(), is(Author.class.getName()));
        assertThat(access.getWriterMethod().getName(), is(access.getWriterMethodName()));
        assertThat(access.getWriterMethod().getDeclaringClass().getName(), is(Author.class.getName()));
    }
}
