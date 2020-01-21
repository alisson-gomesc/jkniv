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
package net.sf.jkniv.whinstone.jdbc.convertible;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetType;
import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.JdbcCommandMock;
import net.sf.jkniv.whinstone.jdbc.domain.acme.Author;
import net.sf.jkniv.whinstone.jdbc.domain.acme.Book;
import net.sf.jkniv.whinstone.jdbc.domain.acme.FlatBook;
import net.sf.jkniv.whinstone.jdbc.domain.flat.MyTypes;
import net.sf.jkniv.whinstone.statement.StatementAdapter;
import net.sf.jkniv.whinstone.types.RegisterType;

public class ConvertibleDataTypeTest extends BaseJdbc
{
    private static final String INSERT_WITH_CONVERT_VALUES = "INSERT INTO TABLE (active, languageType, repositoryType) " +
      " VALUES (:active, :languageType, :repositoryType)";

    private static final String INSERT_WITH_CONVERT_VALUES_PARSED = "INSERT INTO TABLE (active, languageType, repositoryType) " + 
            " VALUES (?      , ?            , ?              )";

    @Autowired
    Repository repositoryDerby;
    
    @Rule
    public ExpectedException  catcher = ExpectedException.none();

    private StatementAdapter<Boo, ResultSet> stmtAdapter;
    private Sql sql;
    private AnsiDialect dialect;
    private ParamParser paramParser;
    
    @Before
    public void setUp()
    {
        this.stmtAdapter = mock(StatementAdapter.class);
        this.sql = mock(Selectable.class);
        this.dialect = mock(AnsiDialect.class);
        this.paramParser= mock(ParamParser.class);
    }
    
    @Test
    public void whenGetDataAnnotationUsingConvertible() throws ParseException
    {
        Queryable q = QueryFactory.of("by-id", "id", 1000);
        MyTypes data = repositoryDerby.get(q);

        SimpleDateFormat sfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sfDateInt = new SimpleDateFormat("yyyyMMdd");
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
        assertThat(data.getMyBoolCharOverride(), is(Boolean.TRUE));
        assertThat(data.getMyDateInt(), is(sfDateInt.parse("20190228")));
        
        assertThat(data.getTimeUnit1(), is(TimeUnit.HOURS));
        assertThat(data.getTimeUnit2(), is(TimeUnit.MINUTES));
    }
    
    @Test
    public void whenGetDataAnnotationUsingConvertibleWithNested()
    {
        Queryable q = QueryFactory.of("listNestedBooks");
        List<Book> list = repositoryDerby.list(q);
        for(Book b : list)
        {
            System.out.println(b);
            assertThat(b, notNullValue());
            assertThat(b.getAuthor(), notNullValue());
            assertThat(b.getAuthor().getId(), notNullValue());
            assertThat(b.getAuthor().getName(), notNullValue());
            assertThat(b.getAuthor().getPrintTypePref(), is(Author.PrintType.HARD));
            assertThat(b.getAuthor().getBooks(), notNullValue());
            assertThat(b.getAuthor().getBooks().size(), is(0));
        }
    }
    
    @Test
    public void whenAddWithConvertibleAnnotationFlatObject()
    {
        Queryable q = QueryFactory.of("getBookByISBN");
        Long ID1 = 2000L, ID2 = 2001L;
        FlatBook book = new FlatBook();
        book.setAuthorId(1L);
        book.setId(ID1);
        book.setIsbn(ID1+"-006");
        book.setName("My "+ID1+" Book");
        book.setInStock(false);
        repositoryDerby.add(book);

        book = new FlatBook();
        book.setAuthorId(1L);
        book.setId(ID2);
        book.setIsbn(ID2+"-006");
        book.setName("My "+ID2+"Book");
        book.setInStock(true);
        repositoryDerby.add(book);

        FlatBook b = new FlatBook();
        b.setId(ID1);
        FlatBook bookSaved= repositoryDerby.get(b);
        assertThat(bookSaved, instanceOf(FlatBook.class));
        assertThat(bookSaved.getId(), is(ID1));
        assertThat(bookSaved.isInStock(), is(false));
        repositoryDerby.remove(bookSaved);

        b.setId(ID2);
        bookSaved = repositoryDerby.get(b);
        assertThat(bookSaved, instanceOf(FlatBook.class));
        assertThat(bookSaved.getId(), is(ID2));
        assertThat(bookSaved.isInStock(), is(true));
        repositoryDerby.remove(bookSaved);
    }
/////////////////////////////////////////////////////////////////////////////////////

    
    @Test
    public void whenBindValuesWithPojo()
    {
        Boo param1 = new Boo(Boolean.TRUE, LanguageType.CRITERIA, RepositoryType.CASSANDRA),
            param2 = new Boo(Boolean.TRUE, LanguageType.HQL, RepositoryType.CASSANDRA),
            param3 = new Boo(Boolean.TRUE, LanguageType.JPQL, RepositoryType.CASSANDRA),
            param4 = new Boo(Boolean.FALSE, LanguageType.NATIVE, RepositoryType.CASSANDRA),
            param5 = new Boo(Boolean.FALSE, LanguageType.STORED, RepositoryType.CASSANDRA);
        Queryable query = QueryFactory.of("dummy", param1);
        given(this.sql.isSelectable()).willReturn(true);
        given(this.sql.getSqlDialect()).willReturn(this.dialect);
        given(this.sql.getParamParser()).willReturn(this.paramParser);
        given(this.sql.getSql(anyObject())).willReturn("select id, name, description from author where active = :active and language = :languageType");
        given(this.paramParser.find(anyString())).willReturn(new String[]{"active","languageType"});
        given(this.paramParser.replaceForPlaceholder(anyString(), anyObject())).willReturn("select id, name, description from author where active = ? and language = ?");
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
        
        query.bind(this.sql);
        query.bind(stmtAdapter);
        assertThat(query.isBoundSql(), is(true));
        assertThat(query.isBoundParams(), is(true));
        assertThat(query.isTypeOfPojo(), is(true));
        assertThat(query.query(), is("select id, name, description from author where active = ? and language = ?"));
        assertThat(query.getParamsNames(), is(arrayWithSize(2)));
        assertThat(query.getParamsNames(), arrayContaining("active","languageType"));
        assertThat(query.values(), is(arrayWithSize(2)));
        assertThat(query.values(), arrayContaining(new Param(param1.getActive(),"T", "active",0),
                                                   new Param(param1.getLanguageType(),
                                                             param1.getLanguageType().ordinal(), "languageType",1)));
        Param[] values = query.values();
        assertThat(values[0].getValueAs().toString(), is("T"));
        assertThat(values[1].getValueAs().toString(), is(String.valueOf(LanguageType.CRITERIA.ordinal())));
        
        query = QueryFactory.of("dummy", param2);
        query.bind(this.sql);
        query.bind(stmtAdapter);
        values = query.values();
        assertThat(values[0].getValueAs().toString(), is("T"));
        assertThat(values[1].getValueAs().toString(), is(String.valueOf(LanguageType.HQL.ordinal())));

        query = QueryFactory.of("dummy", param3);
        query.bind(this.sql);
        query.bind(stmtAdapter);
        values = query.values();
        assertThat(values[0].getValueAs().toString(), is("T"));
        assertThat(values[1].getValueAs().toString(), is(String.valueOf(LanguageType.JPQL.ordinal())));

        query = QueryFactory.of("dummy", param4);
        query.bind(this.sql);
        query.bind(stmtAdapter);
        values = query.values();
        assertThat(values[0].getValueAs().toString(), is("F"));
        assertThat(values[1].getValueAs().toString(), is(String.valueOf(LanguageType.NATIVE.ordinal())));

        query = QueryFactory.of("dummy", param5);
        query.bind(this.sql);
        query.bind(stmtAdapter);
        values = query.values();
        assertThat(values[0].getValueAs().toString(), is("F"));
        assertThat(values[1].getValueAs().toString(), is(String.valueOf(LanguageType.STORED.ordinal())));
    }

    
    @Test @Ignore
    public void _duplicate_whenBulkArrayPojo() throws SQLException
    {
        JdbcCommandMock jdbcMock = new JdbcCommandMock(FlatBook.class);
        Repository repository = jdbcMock.withInsertable().getRepository();
        Object[] books = new Object[7];
        books[0] = new FlatBook();
        books[1] = new FlatBook();
        books[2] = new FlatBook();
        books[3] = new FlatBook();
        books[4] = new FlatBook();
        books[5] = new FlatBook();
        books[6] = new FlatBook();
        
        int rows = repository.add(QueryFactory.ofArray("insert", books));
        assertThat("7 rows was affected", rows, equalTo(7));
        verify(jdbcMock.getStmt(), times(7)).executeUpdate();
        verify(jdbcMock.getStmt(), times(1)).close();
        verify(jdbcMock.getConnection(), times(2)).close();
    }
    
    @Test
    public void whenBindValuesWithConverterUsingBulkCommandOfCollectionOfPojo() throws SQLException
    {
        Boo param1 = new Boo(Boolean.TRUE, LanguageType.CRITERIA, RepositoryType.JDBC), 
            param2 = new Boo(Boolean.FALSE, LanguageType.HQL, RepositoryType.CASSANDRA), 
            param3 = new Boo(Boolean.TRUE, LanguageType.JPQL, RepositoryType.COUCHDB);
        List<Boo> params = new ArrayList<Boo>();
        params.add(param1); params.add(param2); params.add(param3);
        Queryable query = QueryFactory.of("dummy", params);
        
        JdbcCommandMock jdbcMock = new JdbcCommandMock(Boo.class).withInsertable();
//        given(this.sql.isSelectable()).willReturn(false);
//        given(this.sql.getSqlDialect()).willReturn(this.dialect);
//        given(this.sql.getParamParser()).willReturn(this.paramParser);
        given(jdbcMock.withSql().getParamParser())
            .willReturn(jdbcMock.withParamParser());
        given(jdbcMock.withInsertable().withSql().getSql(anyObject()))
            .willReturn(INSERT_WITH_CONVERT_VALUES);
        given(jdbcMock.withParamParser().find(anyString()))
            .willReturn(new String[]{"active","languageType", "repositoryType"});
        given(jdbcMock.withParamParser().replaceForPlaceholder(anyString(), anyObject()))
            .willReturn(INSERT_WITH_CONVERT_VALUES_PARSED);
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
        
        query.bind(jdbcMock.withSql());
        query.bind(stmtAdapter);
        
        //Repository repository = mock(Repository.class);
        
        assertThat(query.isTypeOfCollectionPojo(), is(true));
        assertThat(query.query(), is(INSERT_WITH_CONVERT_VALUES_PARSED));
        assertThat(query.getParamsNames(), is(arrayWithSize(3)));
        assertThat(query.getParamsNames(), arrayContaining("active","languageType", "repositoryType"));

        Repository repository = jdbcMock.getRepository();
        repository.add(query);
        
        verify(jdbcMock.getStmt(), times(3)).executeUpdate();
        
        verify(jdbcMock.getConnection()).prepareStatement(INSERT_WITH_CONVERT_VALUES_PARSED, ResultSetType.DEFAULT.getTypeScroll(), ResultSetConcurrency.DEFAULT.getConcurrencyMode());
        verify(jdbcMock.getStmt(), atLeast(1)).setObject(1, "T");
        verify(jdbcMock.getStmt(), atMost(1)).setObject(2, LanguageType.CRITERIA.ordinal());
        verify(jdbcMock.getStmt(), atMost(1)).setObject(3,  RepositoryType.JDBC.name());
        
        verify(jdbcMock.getStmt(), atMost(1)).setObject(1, "F");
        verify(jdbcMock.getStmt(), atMost(1)).setObject(2, LanguageType.HQL.ordinal());
        verify(jdbcMock.getStmt(), atMost(1)).setObject(3,  RepositoryType.CASSANDRA.name());
        
        verify(jdbcMock.getStmt(), atMost(2)).setObject(1, "T");
        verify(jdbcMock.getStmt(), atMost(1)).setObject(2, LanguageType.JPQL.ordinal());
        verify(jdbcMock.getStmt(), atMost(1)).setObject(3,  RepositoryType.COUCHDB.name());
        
        verify(jdbcMock.getStmt(), times(1)).close();
        verify(jdbcMock.getConnection(), times(2)).close();
        
        assertThat(query.values(), is(arrayWithSize(3)));
        assertThat(query.values(), arrayContaining(new Param(param1,0),new Param(param2, 1), new Param(param3, 2)));
    }
}
