/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.sqlegance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.sf.jkniv.sqlegance.builder.XmlBuilderSql;
import net.sf.jkniv.sqlegance.params.ParamMarkType;

public class XmlBuiderSqlPackageTest
{
    @Test
    public void whenNoPackageIsDefineJustNameQueryIsEnoughCase1()
    {
        Sql sql = XmlBuilderSql.getQuery("test-nopack-0");
        assertThat(sql.getSql().toLowerCase(), is("select id, name from Users where id = 0".toLowerCase()));
        assertThat(sql.getXPath(), is("statements/select[@id='test-nopack-0']"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
    }
    
    @Test
    public void whenNoPackageIsDefineJustNameQueryIsEnoughCase2()
    {
        Sql sql = XmlBuilderSql.getQuery("test-nopack-7");
        assertThat(sql.getSql().toLowerCase(), is("select id, name from Users where id = 7".toLowerCase()));
        assertThat(sql.getXPath(), is("statements/select[@id='test-nopack-7']"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
    }
    
    @Test
    public void whenNoPackageIsDefineJustNameQueryIsEnoughCase3()
    {
        Sql sql = XmlBuilderSql.getQuery("test-nopack-8");
        assertThat(sql.getSql().toLowerCase(), is("select id, name from Users where id = 8".toLowerCase()));
        assertThat(sql.getXPath(), is("statements/select[@id='test-nopack-8']"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
    }
    
    @Test
    public void whenPackageIsDefineFullnameIsRequiredCase1()
    {
        Sql sql = XmlBuilderSql.getQuery("com.acme.one.test-pack-1");
        assertThat(sql.getSql().toLowerCase(), is("select id, name from Users where id = 1".toLowerCase()));
        assertThat(sql.getXPath(), is("statements/package[@name='com.acme.one']/select[@id='test-pack-1']"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
    }
    
    @Test
    public void whenPackageIsDefineFullnameIsRequiredCase2()
    {
        Sql sql = XmlBuilderSql.getQuery("com.acme.one.test-pack-2");
        assertThat(sql.getSql().toLowerCase(), is("select id, name from Users where id = 2".toLowerCase()));
        assertThat(sql.getXPath(), is("statements/package[@name='com.acme.one']/select[@id='test-pack-2']"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
    }
    
    @Test
    public void whenPackageIsDefineFullnameIsRequiredCase3()
    {
        Sql sql = XmlBuilderSql.getQuery("com.acme.one.test-pack-3");
        assertThat(sql.getSql().toLowerCase(), is("select id, name from Users where id = 3".toLowerCase()));
        assertThat(sql.getXPath(), is("statements/package[@name='com.acme.one']/select[@id='test-pack-3']"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
    }
    
    @Test
    public void whenPackageIsDefineFullnameIsRequiredCase4()
    {
        Sql sql = XmlBuilderSql.getQuery("com.acme.two.test-pack-4");
        assertThat(sql.getSql().toLowerCase(), is("select id, name from Users where id = 4".toLowerCase()));
        assertThat(sql.getXPath(), is("statements/package[@name='com.acme.two']/select[@id='test-pack-4']"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
    }
    
    @Test
    public void whenPackageIsDefineFullnameIsRequiredCase5()
    {
        Sql sql = XmlBuilderSql.getQuery("com.acme.two.test-pack-5");
        assertThat(sql.getSql().toLowerCase(), is("select id, name from Users where id = 5".toLowerCase()));
        assertThat(sql.getXPath(), is("statements/package[@name='com.acme.two']/select[@id='test-pack-5']"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
    }
    
    @Test
    public void whenPackageIsDefineFullnameIsRequiredCase6()
    {
        Sql sql = XmlBuilderSql.getQuery("com.acme.two.test-pack-6");
        assertThat(sql.getSql().toLowerCase(), is("select id, name from Users where id = 6".toLowerCase()));
        assertThat(sql.getXPath(), is("statements/package[@name='com.acme.two']/select[@id='test-pack-6']"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
    }
}
