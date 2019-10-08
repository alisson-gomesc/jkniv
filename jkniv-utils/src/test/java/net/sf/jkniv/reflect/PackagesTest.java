package net.sf.jkniv.reflect;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Test;

public class PackagesTest
{
    @Test
    public void whenScanPackage() throws IOException
    {
        Packages p = new Packages("net.sf.jkniv.reflect.lab");
        p.scan();
        assertThat(p.asArray().length, is(7));
    }

    @Test
    public void whenScanRecursive() throws IOException
    {
        Packages p = new Packages("net.sf.jkniv.reflect.lab", true);
        p.scan();
        assertThat(p.asArray().length, is(14));
    }

    @Test
    public void whenScanOnlyEnums() throws IOException
    {
        Packages p = new Packages("net.sf.jkniv.reflect.lab");
        p.onlyEnums();
        p.scan();
        assertThat(p.asArray().length, is(1));
    }

    @Test
    public void whenScanOnlyInterfaces() throws IOException
    {
        Packages p = new Packages("net.sf.jkniv.reflect.lab");
        p.onlyInterfaces();
        p.scan();
        assertThat(p.asArray().length, is(1));
    }

    @Test
    public void whenScanOnlyAnonymousClasses() throws IOException
    {
        Packages p = new Packages("net.sf.jkniv.reflect.lab");
        p.onlyAnonymousClasses();
        p.scan();
        assertThat(p.asArray().length, is(1));
    }

    @Test
    public void whenScanOnlyClasses() throws IOException
    {
        Packages p = new Packages("net.sf.jkniv.reflect.lab");
        p.onlyClasses();
        p.scan();
        assertThat(p.asArray().length, is(4));// TODO test me review
    }


}
