package net.sf.jkniv.hibernate4.types;

//import org.hibernate.type.AbstractSingleColumnStandardBasicType;
//import org.hibernate.type.DiscriminatorType;

/**
 * 
 * https://github.com/marschall/threeten-jpa
 *
 */
public class IntervalDsType 
//extends AbstractSingleColumnStandardBasicType<Duration> implements DiscriminatorType<Duration>
{
    
    public static final IntervalDsType INSTANCE = new IntervalDsType();
    
    public IntervalDsType() {
        //super( VarcharTypeDescriptor.INSTANCE, IntervalDsType.INSTANCE );
    }
}
