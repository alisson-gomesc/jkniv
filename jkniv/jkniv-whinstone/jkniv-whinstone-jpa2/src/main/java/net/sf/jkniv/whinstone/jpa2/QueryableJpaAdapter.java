package net.sf.jkniv.whinstone.jpa2;

import java.util.List;

import javax.persistence.Query;

import net.sf.jkniv.whinstone.Queryable;

/**
 * Adapter for different Queries from JPA, like: {@code Query}, {@code NativeQuery}, {@code NamedQuery}, {@code TypedQuery}.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface QueryableJpaAdapter
{
    
    <T> T getSingleResult();
    
    <T> List<T> getResultList();
    
    void setQueryJpaForPaging(Query queryPaging);
    
    Query getQueryJpaForPaging();
    
    int executeUpdate();
    
    //void setTotalPaging(Queryable queryable);
//    QueryableJpaAdapter setFirstResult(int offset);
//    
//    QueryableJpaAdapter setOffsetResult(int max);
//    
//    QueryableJpaAdapter setMaxResult(int max);

    
}
