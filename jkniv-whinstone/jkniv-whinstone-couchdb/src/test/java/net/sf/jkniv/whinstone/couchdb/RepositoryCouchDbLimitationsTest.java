package net.sf.jkniv.whinstone.couchdb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;

import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;

public class RepositoryCouchDbLimitationsTest
extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenViewWithParameterAttachmentsIsnotSupported()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("Query Parameters [attachments] isn't supported yet!");
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("docs/_view/natio", asParams("attachments","true"));
        repositoryDb.list(q);
    }
    
}
