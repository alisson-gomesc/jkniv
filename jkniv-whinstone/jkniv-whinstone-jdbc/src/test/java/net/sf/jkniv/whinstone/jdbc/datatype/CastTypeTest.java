package net.sf.jkniv.whinstone.jdbc.datatype;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Date;

import org.junit.Test;

import net.sf.jkniv.whinstone.jdbc.Database;

public class CastTypeTest
{
    @Test
    public void whenEnumGenericIsInstanceOfAnotherEnum()
    {
        Database DB = Database.Oracle;
        assertThat(Enum.class.isInstance(DB), is(true));
        assertThat(Enum.class.isInstance(null), is(false));
    }

    @Test
    public void whenDateIsInstanceOfSubclassDates()
    {
        java.util.Date date = new java.util.Date();
        java.sql.Time time = new java.sql.Time(date.getTime());
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

        assertThat(date instanceof java.util.Date, is(true));
        assertThat(time instanceof java.util.Date, is(true));
        assertThat(timestamp instanceof java.util.Date, is(true));

    }

}
