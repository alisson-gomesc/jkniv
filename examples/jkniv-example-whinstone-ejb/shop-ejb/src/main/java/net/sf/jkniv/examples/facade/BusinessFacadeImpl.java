package net.sf.jkniv.examples.facade;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import net.sf.jkniv.examples.client.BusinessFacade;
import net.sf.jkniv.examples.entities.Book;
import net.sf.jkniv.sqlegance.QueryName;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;

@Stateless
@Local(BusinessFacade.class)
public class BusinessFacadeImpl extends BaseFacade implements BusinessFacade
{

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void save(Book book)
    {
        Repository repository = getRepository();
        Queryable queryable = new QueryName("saveBook", book);
        repository.add(queryable);
    }
 }
