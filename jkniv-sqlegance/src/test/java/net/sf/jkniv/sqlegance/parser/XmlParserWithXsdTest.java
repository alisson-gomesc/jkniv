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
package net.sf.jkniv.sqlegance.parser;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;

public class XmlParserWithXsdTest
{
    @Rule
    public ExpectedException    catcher  = ExpectedException.none();
    private static final String XSD_PATH_WIN = "file:/C:/dev/wks/wks-jkniv-git/jkniv-sqlegance/target/classes/net/sf/jkniv/sqlegance/builder/xml/";
    private static final String XSD_PATH_GITHUB = "file:/home/runner/work/jkniv/jkniv/jkniv-sqlegance/target/classes/net/sf/jkniv/sqlegance/builder/xml/";    
    @Test
    public void whenLoadFileWithXsdError()
    {
        String path = XSD_PATH_WIN;
        if(new File("").getAbsolutePath().startsWith("/home/"))
            path = XSD_PATH_GITHUB;
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("Fail validate [/customxml/sql-stmt-wrong.xml] against XSD=[[" + path
                + "sqlegance-stmt.xsd, " + path
                + "sqlegance-cache.xsd]]. cvc-complex-type.3.2.2: Attribute 'name' is not allowed to appear in element 'statements'");
        SqlContextFactory.newInstance("/customxml/sql-stmt-wrong.xml");
    }
}
