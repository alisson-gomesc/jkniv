package net.sf.jkniv.whinstone.jdbc.convertible;

import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.types.BooleanCharType;
import net.sf.jkniv.sqlegance.types.Converter;
import net.sf.jkniv.sqlegance.types.Converter.EnumType;

public class Boo
{
    @Converter(converter = BooleanCharType.class, pattern = "T|F")
    private Boolean        active;
    @Converter(converter = LanguageType.class, isEnum = EnumType.ORDINAL)
    private LanguageType   languageType;
    @Converter(converter = RepositoryType.class)
    private RepositoryType repositoryType;
    
    public Boo(Boolean active, LanguageType languageType, RepositoryType repositoryType)
    {
        super();
        this.active = active;
        this.languageType = languageType;
        this.repositoryType = repositoryType;
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
    
    public RepositoryType getRepositoryType()
    {
        return repositoryType;
    }
    
    public void setRepositoryType(RepositoryType repositoryType)
    {
        this.repositoryType = repositoryType;
    }
    
    @Override
    public String toString()
    {
        return "Boo [active=" + active + ", languageType=" + languageType + ", repositoryType=" + repositoryType + "]";
    }
    
}
