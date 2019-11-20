/* 
 * JKNIV, whinstone one contract to access your database.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jkniv.whinstone.jdbc.datatype;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.domain.flat.MyTypes;

public class DerbyNullSafeDataTypeTest extends BaseJdbc
{
    @Autowired
    Repository repositoryDerby;
    
    @Test
    public void whenNullValuesDoesnotGenerateNullPointerException()
    {
        Queryable q = getQuery("getNulValues");
        Map<String, Object> data = repositoryDerby.get(q);
        assertThat(data.get("MY_SMALLINT"), nullValue());
        assertThat(data.get("MY_INTEGER"), nullValue());
        assertThat(data.get("MY_BIGINT"), nullValue());
        assertThat(data.get("MY_FLOAT"), nullValue());
        assertThat(data.get("MY_DECIMAL"), nullValue());
        assertThat(data.get("MY_VARCHAR"), nullValue());
        assertThat(data.get("MY_CHAR"), nullValue());
        assertThat(data.get("MY_BLOB"), nullValue());
        assertThat(data.get("MY_CLOB"), nullValue());
        assertThat(data.get("MY_DATE"), nullValue());
        assertThat(data.get("MY_TIME"), nullValue());
        assertThat(data.get("MY_TIMESTAMP"), nullValue());
    }

    @Test
    public void whenById()
    {
        Queryable q = QueryFactory.of("by-id", "id", 1000);
        MyTypes data = repositoryDerby.get(q);

        SimpleDateFormat sfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sfTime = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        assertThat(data.getId(), is(1000L));
        assertThat(data.getMySmallint(), is(Short.valueOf("1001")));
        assertThat(data.getMyInteger(), is(1002));
        assertThat(data.getMyBigint(), is(1003L));
        assertThat(data.getMyFloat(), is(1.004F));
        assertThat(data.getMyDecimal(), is(1.005D));
        assertThat(data.getMyVarchar(), is("1006"));
        assertThat(data.getMyChar(), is("1007      "));
        assertThat(sfDate.format(data.getMyDate()), is("2016-02-01"));
        assertThat(sfTime.format(data.getMyTime()), is("13:00:00"));
        assertThat(sfTimestamp.format(data.getMyTimestamp()), is("2016-02-01 13:00:00"));
        assertThat(data.getMyBoolChar(), is(Boolean.TRUE));
        
    }
}
