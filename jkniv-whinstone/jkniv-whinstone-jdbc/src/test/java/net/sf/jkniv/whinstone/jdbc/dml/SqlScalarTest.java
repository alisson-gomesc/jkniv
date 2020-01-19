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

import net.sf.jkniv.whinstone.QueryFactory;
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
        Queryable q = QueryFactory.of("getSequenceAuthor");
        Number seq = repositoryDerby.scalar(q);
        assertThat(seq, notNullValue());
        assertThat(seq.intValue(), is(Integer.MIN_VALUE));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueNumber()
    {
        Queryable q = QueryFactory.of("getScalarSmallint");
        Number number = repositoryDerby.scalar(q);
        assertThat(number, notNullValue());
        assertThat(number, instanceOf(Number.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueSmallint()
    {
        Queryable q = QueryFactory.of("getScalarSmallint");
        Number number = repositoryDerby.scalar(q);
        assertThat(number, notNullValue());
        assertThat(number, instanceOf(Integer.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueInteger()
    {
        Queryable q = QueryFactory.of("getScalarInteger");
        Number number = repositoryDerby.scalar(q);
        assertThat(number, notNullValue());
        assertThat(number, instanceOf(Integer.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueBigInt()
    {
        Queryable q = QueryFactory.of("getScalarBigInt");
        Number number = repositoryDerby.scalar(q);
        assertThat(number, notNullValue());
        assertThat(number, instanceOf(Long.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueFloat()
    {
        Queryable q = QueryFactory.of("getScalarFloat");
        Number number = repositoryDerby.scalar(q);
        assertThat(number, notNullValue());
        assertThat(number, instanceOf(Float.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueDecimal()
    {
        Queryable q = QueryFactory.of("getScalarDecimal");
        Number number = repositoryDerby.scalar(q);
        assertThat(number, notNullValue());
        assertThat(number, instanceOf(BigDecimal.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueVarchar()
    {
        Queryable q = QueryFactory.of("getScalarVarchar");
        String s = repositoryDerby.scalar(q);
        assertThat(s, notNullValue());
        assertThat(s, instanceOf(String.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueChar()
    {
        Queryable q = QueryFactory.of("getScalarChar");
        String s = repositoryDerby.scalar(q);
        assertThat(s, notNullValue());
        assertThat(s, instanceOf(String.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueDate()
    {
        Queryable q = QueryFactory.of("getScalarDate");
        Date d = repositoryDerby.scalar(q);
        assertThat(d, notNullValue());
        assertThat(d, instanceOf(Date.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueTime()
    {
        Queryable q = QueryFactory.of("getScalarTime");
        Date d = repositoryDerby.scalar(q);
        assertThat(d, notNullValue());
        assertThat(d, instanceOf(Date.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    public void whenGetScalarValueTimestamp()
    {
        Queryable q = QueryFactory.of("getScalarTimestamp");
        Date d = repositoryDerby.scalar(q);
        assertThat(d, notNullValue());
        assertThat(d, instanceOf(Date.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    @Ignore("needs make insert blob type")
    public void whenGetScalarValueBlob()
    {
        Queryable q = QueryFactory.of("getScalarBlob");
        String s = repositoryDerby.scalar(q);
        assertThat(s, notNullValue());
        assertThat(s, instanceOf(String.class));
        assertThat(q.isScalar(), is(true));
    }
    
    @Test
    @Ignore("needs make insert clob type")
    public void whenGetScalarValueClob()
    {
        Queryable q = QueryFactory.of("getScalarBlob");
        String s = repositoryDerby.scalar(q);
        assertThat(s, notNullValue());
        assertThat(s, instanceOf(String.class));
        assertThat(q.isScalar(), is(true));
    }
}
