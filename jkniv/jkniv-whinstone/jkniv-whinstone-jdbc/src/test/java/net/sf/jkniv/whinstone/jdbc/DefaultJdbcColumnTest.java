package net.sf.jkniv.whinstone.jdbc;

import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Types;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class DefaultJdbcColumnTest
{
    @Test
    public void whenMapperColumnNameToProperty()
    {
        DefaultJdbcColumn jdbcColumn1 = new DefaultJdbcColumn(0, "unit_code", Types.VARCHAR);
        assertThat(jdbcColumn1.getAttributeName(), is("unitCode"));
        assertThat(jdbcColumn1.getMethodName(), is("setUnitCode"));

        DefaultJdbcColumn jdbcColumn2 = new DefaultJdbcColumn(0, "fds.status", Types.VARCHAR);
        assertThat(jdbcColumn2.getAttributeName(), is("fds.status"));
        assertThat(jdbcColumn2.getMethodName(), is("fds.status"));

        DefaultJdbcColumn jdbcColumn3 = new DefaultJdbcColumn(0, "fds.statusCode", Types.VARCHAR);
        assertThat(jdbcColumn3.getAttributeName(), is("fds.statusCode"));
        assertThat(jdbcColumn3.getMethodName(), is("fds.statusCode"));

    }
    
}
