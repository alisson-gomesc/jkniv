package net.sf.jkniv.sqlegance.classification;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Create group of objects with a tabular result {@code <R>}.
 *
 * @param <T> type of grouped objects
 * @param <R> The driver result of a query like {@link ResultSet}
 * 
 * @author Alisson Gomes
 *
 * @since 0.6.0
 */
public interface Groupable<T, R>
{
    
    void classifier(R row);
    
    Collection<T> asCollection();
    
    List<T> asList();
    
    Set<T> asSet();
    
}
