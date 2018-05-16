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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatAuthor;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatBook;

public class SqlUpdateTest extends BaseJdbc
{
    @Autowired
    Repository repositoryDerby;
    
    @Test
    public void whenUpdateRecord()
    {
        Queryable q = QueryFactory.newInstance("getBookByISBN");
        final String NAME = "Friedrich Wilhelm Nietzsche";
        final Long ID = 1L;
        FlatAuthor author = new FlatAuthor();
        author.setId(1L);
        author.setName(NAME);
        repositoryDerby.update(author);
        
        List<FlatBook> list = repositoryDerby.list(q);
        System.out.println(list);
        for (FlatBook b : list)
        {
            if (b.getAuthorId() == ID)
                Assert.assertTrue(NAME.equals(b.getName()));
        }
    }
    

}
