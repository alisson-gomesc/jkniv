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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;

public class OracleDataTypeTest extends BaseJdbc
{
    @Autowired
    Repository repositoryOra;
 
    @Test 
    public void whenPrepareStatementWithDateUsingBasicParamsWorks() throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("2016-02-01");
        Queryable queryable = QueryFactory.of("myTypeByDate", date);
 
        List<Map<String, Object>> rows = repositoryOra.list(queryable);
        assertThat(rows.size(), is(3));
    }    

    @Test 
    public void whenPrepareStatementWithDateUsingNamedParamsWorks() throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("2016-02-01");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("date", date);
        Queryable queryable = QueryFactory.of("myTypeByDate", params);
 
        List<Map<String, Object>> rows = repositoryOra.list(queryable);
        assertThat(rows.size(), is(3));
    }    
}
