package net.sf.jkniv.sqlegance.builder.xml;

public class TagFactory
{

    public static OneToManyTag newOneToMany(String property, String typeOf, String impl)
    {
        return new OneToManyTag(property, typeOf, impl);
    }
}
