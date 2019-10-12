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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.JdbcQueryMock;
import net.sf.jkniv.whinstone.jdbc.domain.acme.FlatBook;

public class SqlStatsTest
{

    @Rule
    public ExpectedException catcher = ExpectedException.none();
    
    @Test
    public void whenSelectAllRecords()
    {
//        JdbcQueryMock jdbcMock = new JdbcQueryMock(FlatBook.class);
//        Repository repository = jdbcMock.columns(new String[]
//        { "id", "isbn", "name", "author", "author_id" }).buildFifteenFlatBook();
//        Queryable q = QueryFactory.of("15 FlatBook");
//        List<FlatBook> books = repository.list(q);
//        assertThat("There are 15 rows", books.size(), equalTo(15));
//        assertThat("Row is a FlatBook object", books.get(0), instanceOf(FlatBook.class));
//        for (FlatBook b : books)
//        {
//            assertThat(b.getAuthor(), notNullValue());
//            assertThat(b.getAuthorId(), notNullValue());
//            assertThat(b.getId(), notNullValue());
//            assertThat(b.getIsbn(), notNullValue());
//            assertThat(b.getName(), notNullValue());
//        }
//       // verifyClose(jdbcMock, repository);
    }

}
