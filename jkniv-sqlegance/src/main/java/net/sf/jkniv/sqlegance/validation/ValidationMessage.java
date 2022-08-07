package net.sf.jkniv.sqlegance.validation;

public interface ValidationMessage
{
    String getValue();
    
    String getField();

    String getQualifiedField();
    
    String getKey();
}
