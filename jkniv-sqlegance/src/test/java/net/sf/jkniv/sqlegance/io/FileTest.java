package net.sf.jkniv.sqlegance.io;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.text.SimpleDateFormat;

import org.junit.Test;

import net.sf.jkniv.sqlegance.DefaultClassLoader;

public class FileTest
{
    @Test
    public void whenVerifyLastModifyDateFromFile()
    {
        File file = new File(DefaultClassLoader.getResource("/repository-config.xml").getFile());
        assertThat(file.lastModified(), is(not(0L)));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("After Format : " + sdf.format(file.lastModified()));
    }
    
}
