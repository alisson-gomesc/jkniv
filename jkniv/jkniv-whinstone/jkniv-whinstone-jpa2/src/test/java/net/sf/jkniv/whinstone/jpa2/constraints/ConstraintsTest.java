package net.sf.jkniv.whinstone.jpa2.constraints;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class ConstraintsTest extends BaseTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();

    
    @Test
    public void whenUseGroupByWithJPQL()
    {
        catcher.expect(IllegalArgumentException.class);
        catcher.expectMessage("JPQL cannot have group by, just NATIVE or STORED, change the type [JPQL] from SQL [oneToManyBooksFromAuthorsJPQL] to execute");
        Repository repository = getRepository();
        Queryable q = QueryFactory.newInstance("oneToManyBooksFromAuthorsJPQL");
        repository.list(q);
    }
    
    @Test
    public void whenNativeQueryHaventReturnTypeGetArrayOfObject()
    {
        //catcher.expect(RepositoryException.class);
        //catcher.expectMessage("JPA NATIVE [constraintNoReturnType] query require returnType with appropriate constructor with parameters from");
        
        Repository repository = getRepository();
        Queryable queryable = QueryFactory.newInstance("constraintNoReturnType");
        List<Object[]> list = repository.list(queryable);
        assertThat(list.size(), greaterThan(0));
        assertThat(list.get(0), instanceOf(Object[].class));
    }

}
