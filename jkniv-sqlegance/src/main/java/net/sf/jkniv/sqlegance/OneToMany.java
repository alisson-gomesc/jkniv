package net.sf.jkniv.sqlegance;

/**
 * Mapping one-to-many objects relationship
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface OneToMany
{
    public static final String TAG_NAME           = "one-to-many";
    public static final String ATTRIBUTE_PROPERTY = "property";
    public static final String ATTRIBUTE_TYPEOF   = "typeOf";
    public static final String ATTRIBUTE_IMPL     = "impl";

    /**
     * The attribute name that hold the collection (many parts)
     * @return property name
     */
    String getProperty();
    
    /**
     * Class name that represents the type of collection (many parts)
     * @return type of object
     */
    String getTypeOf();
    
    /**
     * Class name of the collection to supports relationship (like a {@code java.util.ArrayList}
     * @return name of collection class
     */
    String getImpl();
    
}
