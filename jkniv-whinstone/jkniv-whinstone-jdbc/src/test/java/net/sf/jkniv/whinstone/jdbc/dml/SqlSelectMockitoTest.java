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
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.CustomResultRow;
import net.sf.jkniv.whinstone.jdbc.JdbcQueryMock;
import net.sf.jkniv.whinstone.jdbc.domain.acme.Book;
import net.sf.jkniv.whinstone.jdbc.domain.acme.FlatAuthor;
import net.sf.jkniv.whinstone.jdbc.domain.acme.FlatBook;
import net.sf.jkniv.whinstone.transaction.TransactionStatus;

public class SqlSelectMockitoTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();
    
    @Test
    public void whenSelectAllRecords()
    {
        JdbcQueryMock jdbcMock = new JdbcQueryMock(FlatBook.class);
        Repository repository = jdbcMock.columns(new String[]
        { "id", "isbn", "name", "author", "author_id" }).buildFifteenFlatBook();
        Queryable q = QueryFactory.of("15 FlatBook");
        List<FlatBook> books = repository.list(q);
        assertThat("There are 15 rows", books.size(), equalTo(15));
        assertThat("Row is a FlatBook object", books.get(0), instanceOf(FlatBook.class));
        for (FlatBook b : books)
        {
            assertThat(b.getAuthor(), notNullValue());
            assertThat(b.getAuthorId(), notNullValue());
            assertThat(b.getId(), notNullValue());
            assertThat(b.getIsbn(), notNullValue());
            assertThat(b.getName(), notNullValue());
        }
        verifyClose(jdbcMock, repository);
    }
    
    @Test
    public void whenUsingMockitto()
    {
        JdbcQueryMock jdbcMock = new JdbcQueryMock(FlatAuthor.class);
        Repository repository = jdbcMock.columns(new String[]
        { "id", "name", "book" }).buildThreeFlatAuthor();
        
        Queryable q = QueryFactory.of("2 FlatAuthor");
        List<FlatAuthor> books = repository.list(q);
        assertThat("There are 2 rows", books.size(), equalTo(2));
        assertThat("Row is a FlatAuthor object", books.get(0), instanceOf(FlatAuthor.class));
        for (FlatAuthor a : books)
        {
            assertThat(a.getName(), notNullValue());
            assertThat(a.getId(), notNullValue());
            assertThat(a.getBookId(), notNullValue());
        }
        verifyClose(jdbcMock, repository);
    }
    

    
    @Test
    public void whenSelectNonRows()
    {
        JdbcQueryMock jdbcMock = new JdbcQueryMock(FlatAuthor.class);
        Repository repository = jdbcMock.columns(new String[]
        { "id", "name", "book" }).getRepository();
        
        Queryable q = QueryFactory.of("0 records");
        List<FlatAuthor> books = repository.list(q);
        assertThat("Query result is Empty", books.size(), equalTo(0));
        verifyClose(jdbcMock, repository);
    }
    
    @Test
    public void whenSelectWrongClassType()
    {
        catcher.expect(ClassCastException.class);
        JdbcQueryMock jdbcMock = new JdbcQueryMock(FlatBook.class);
        Repository repository = jdbcMock.columns(new String[]
        { "id", "name", "book" }).buildOneFlatBook();
        
        Queryable q = QueryFactory.of("select");
        FlatAuthor o = repository.get(q);
        assertThat("Query result is Empty", o, notNullValue());
        assertThat("Row is a FlatAuthor object", o, instanceOf(FlatBook.class));
    }

    @Test
    public void whenSelectOverloadReturnType()
    {
        JdbcQueryMock jdbcMock = new JdbcQueryMock(FlatBook.class);
        Repository repository = jdbcMock.columns(new String[]
                { "id", "isbn", "name"}).buildFifteenFlatBook();
        Queryable q = QueryFactory.of("select");
        
        List<Book> list = repository.list(q, Book.class);
        assertThat("There are 15 rows", list.size(), equalTo(15));
        for (Book b : list)
        {
            assertThat(b, instanceOf(Book.class));
            assertThat(b.getId(), notNullValue());
            assertThat(b.getName(), notNullValue());
            assertThat(b.getIsbn(), notNullValue());
        }
        verifyClose(jdbcMock, repository);
    }
    
    @Test
    public void whenSelectDoesntDefinedReturnType()
    {
        JdbcQueryMock jdbcMock = new JdbcQueryMock(Map.class);
        Repository repository = jdbcMock.columns(new String[]
                { "id", "isbn", "name"}).buildFifteenFlatBook();

        Queryable q = QueryFactory.of("listBooksNoSpecificType");
        List<Map> list = repository.list(q);
        assertThat(list.size(), is(15));
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(HashMap.class));
    }
    
    @Test//error
    public void whenSelectWithoutTypeAndCustomResultSetParser()
    {
        JdbcQueryMock jdbcMock = new JdbcQueryMock(Map.class);
        Repository repository = jdbcMock.columns(new String[]
                { "id", "isbn", "name"}).buildFifteenFlatBook();
        CustomResultRow resultRow = new CustomResultRow();
        Queryable q = QueryFactory.of("listBooksNoSpecificType");
        List<HashMap<String, Object>> list = repository.list(q, resultRow);
        assertThat(list.get(0), instanceOf(Map.class));
        assertThat(list.get(0), instanceOf(HashMap.class));
        assertThat(String.valueOf(list.get(0).get("0")), is("1001"));
        assertThat(String.valueOf(list.get(0).get("JUNIT")), is("true"));
        verifyClose(jdbcMock, repository);
    }

    @Test 
    public void whenSelectDoesntDefinedReturnTypeButForceOne()
    {
        JdbcQueryMock jdbcMock = new JdbcQueryMock(FlatAuthor.class);
        Repository repository = jdbcMock.columns(new String[]
        { "id", "name", "book" }).buildThreeFlatAuthor();

        Queryable q = QueryFactory.of("listBooksNoSpecificType");
        List<Book> list = repository.list(q, Book.class);
        assertThat(list.size(), is(2));
        assertThat(list.get(0), instanceOf(Book.class));
        verifyClose(jdbcMock, repository);
    }

    @Test
    public void whenSelectOneRecordByUniqueValueWithMapParams()
    {
        JdbcQueryMock jdbcMock = new JdbcQueryMock(FlatBook.class);
        Repository repository = jdbcMock.columns(new String[]
        { "id", "isbn", "name", "author" }).buildOneFlatBook();

        Map<String, String> map = new HashMap<String, String>();
        map.put("isbn", "978-1503250888");
        Queryable q = QueryFactory.of("getBookByISBNWithMap", map);
        List<FlatBook> list = repository.list(q);
        assertThat(list.size(), is(1));
        assertThat(list.get(0).getId(), is(1001L));
        assertThat(list.get(0).getIsbn(), is("978-1503250888"));
        assertThat(list.get(0).getName(), is("Beyond Good and Evil"));
        assertThat(list.get(0).getAuthor(), is("Friedrich Nietzsche"));
        verifyClose(jdbcMock, repository);
    }
    
    @Test
    public void whenGetRecordWithoutQueryable()
    {
        JdbcQueryMock jdbcMock = new JdbcQueryMock(FlatBook.class);
        Repository repository = jdbcMock.columns(new String[]
        { "id", "isbn", "name", "author" }).buildOneFlatBook();

        FlatBook b = new FlatBook();
        b.setIsbn("978-0321826626");
        FlatBook b1 = repository.get(b);
        assertThat(b1, is(notNullValue()));
        assertThat(b1.getId(), is(1001L));
        assertThat(b1.getIsbn(), is("978-1503250888"));
        assertThat(b1.getName(), is("Beyond Good and Evil"));
        assertThat(b1.getAuthor(), is("Friedrich Nietzsche"));
        verifyClose(jdbcMock, repository);
    }

    private void verifyClose(JdbcQueryMock jdbcMock, Repository repository)
    {
        try
        {
            assertThat(repository.getTransaction().getStatus(), is(TransactionStatus.NO_TRANSACTION));
            verify(jdbcMock.getRs()).close();
            verify(jdbcMock.getStmt()).close();
            verify(jdbcMock.getConnection(), atLeast(1)).close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Cannot verify close methods for connection, statement and resultset", e);
        }
    }
}
