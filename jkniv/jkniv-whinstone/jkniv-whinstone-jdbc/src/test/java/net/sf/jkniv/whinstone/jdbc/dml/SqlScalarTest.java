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
package net.sf.jkniv.whinstone.jdbc.dml;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;

public class SqlScalarTest extends BaseJdbc
{
    @Autowired
    Repository repositoryDerby;
    
    @Test
    public void whenGetScalarFromSequence()
    {
        Queryable q = getQuery("getSequenceAuthor");
        Number seq = repositoryDerby.scalar(q);
        assertThat(seq, notNullValue());
        assertThat(seq.intValue(), is(Integer.MIN_VALUE));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueNumber()
    {
        Queryable q = getQuery("getScalarSmallint");
        Number number = repositoryDerby.scalar(q);
        assertThat(number, notNullValue());
        assertThat(number, instanceOf(Number.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueSmallint()
    {
        Queryable q = getQuery("getScalarSmallint");
        Number number = repositoryDerby.scalar(q);
        assertThat(number, notNullValue());
        assertThat(number, instanceOf(Integer.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueInteger()
    {
        Queryable q = getQuery("getScalarInteger");
        Number number = repositoryDerby.scalar(q);
        assertThat(number, notNullValue());
        assertThat(number, instanceOf(Integer.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueBigInt()
    {
        Queryable q = getQuery("getScalarBigInt");
        Number number = repositoryDerby.scalar(q);
        assertThat(number, notNullValue());
        assertThat(number, instanceOf(Long.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueFloat()
    {
        Queryable q = getQuery("getScalarFloat");
        Number number = repositoryDerby.scalar(q);
        assertThat(number, notNullValue());
        assertThat(number, instanceOf(Double.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueDecimal()
    {
        Queryable q = getQuery("getScalarDecimal");
        Number number = repositoryDerby.scalar(q);
        assertThat(number, notNullValue());
        assertThat(number, instanceOf(BigDecimal.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueVarchar()
    {
        Queryable q = getQuery("getScalarVarchar");
        String s = repositoryDerby.scalar(q);
        assertThat(s, notNullValue());
        assertThat(s, instanceOf(String.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueChar()
    {
        Queryable q = getQuery("getScalarChar");
        String s = repositoryDerby.scalar(q);
        assertThat(s, notNullValue());
        assertThat(s, instanceOf(String.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueDate()
    {
        Queryable q = getQuery("getScalarDate");
        Date d = repositoryDerby.scalar(q);
        assertThat(d, notNullValue());
        assertThat(d, instanceOf(Date.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueTime()
    {
        Queryable q = getQuery("getScalarTime");
        Date d = repositoryDerby.scalar(q);
        assertThat(d, notNullValue());
        assertThat(d, instanceOf(Date.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueTimestamp()
    {
        Queryable q = getQuery("getScalarTimestamp");
        Date d = repositoryDerby.scalar(q);
        assertThat(d, notNullValue());
        assertThat(d, instanceOf(Date.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    @Ignore("needs make insert blob type")
    public void whenGetScalarValueBlob()
    {
        Queryable q = getQuery("getScalarBlob");
        String s = repositoryDerby.scalar(q);
        assertThat(s, notNullValue());
        assertThat(s, instanceOf(String.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    @Ignore("needs make insert clob type")
    public void whenGetScalarValueClob()
    {
        Queryable q = getQuery("getScalarBlob");
        String s = repositoryDerby.scalar(q);
        assertThat(s, notNullValue());
        assertThat(s, instanceOf(String.class));
        assertThat(q.isScalar(), is(true));
    }
}
