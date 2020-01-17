package net.sf.jkniv.whinstone.convertible;

import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.types.BooleanCharType;
import net.sf.jkniv.sqlegance.types.Converter;
import net.sf.jkniv.sqlegance.types.Converter.EnumType;

public class Foo
{
    @Converter(converter = BooleanCharType.class, pattern = "T|F")
    private Boolean      active;
    @Converter(converter = LanguageType.class, isEnum = EnumType.ORDINAL)
    private LanguageType languageType;
    
    public Foo(Boolean active, LanguageType languageType)
    {
        super();
        this.active = active;
        this.languageType = languageType;
    }

    public Boolean getActive()
    {
        return active;
    }
    
    public void setActive(Boolean active)
    {
        this.active = active;
    }
    
    public LanguageType getLanguageType()
    {
        return languageType;
    }
    
    public void setLanguageType(LanguageType languageType)
    {
        this.languageType = languageType;
    }
    
}
