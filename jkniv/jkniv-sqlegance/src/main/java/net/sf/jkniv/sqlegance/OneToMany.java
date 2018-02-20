package net.sf.jkniv.sqlegance;

public interface OneToMany
{
    public static final String TAG_NAME           = "one-to-many";
    public static final String ATTRIBUTE_PROPERTY = "property";
    public static final String ATTRIBUTE_TYPEOF   = "typeOf";
    public static final String ATTRIBUTE_IMPL     = "impl";

    String getProperty();
    
    String getTypeOf();
    
    String getImpl();
    
}
