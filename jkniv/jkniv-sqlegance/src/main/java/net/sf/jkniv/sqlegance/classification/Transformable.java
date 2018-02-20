package net.sf.jkniv.sqlegance.classification;

/**
 * Allow transform a tabular row to one-to-many objects.
 * 
 * <b>Note: Implementation must be thread-safe</b>
 * 
 * @param <R> data row type
 *
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface Transformable<R>
{
    public enum TransformableType  { MAP, OBJECT};//, RESULT_SET };
    
    <T> T transform(R row, Class<T> type);
    
    void transform(R row, Object instance);
    
}
