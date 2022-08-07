package net.sf.jkniv.sqlegance.validation;

class ValidationMessageImpl implements ValidationMessage
{
    private String value;
    private String field;
    private String qualifiedField;
    private String key;
    
    
    static ValidationMessage of(String field, String qualifiedField, String message, String key)
    {
        ValidationMessageImpl validMessage = new ValidationMessageImpl();
        validMessage.field = field;
        validMessage.qualifiedField = qualifiedField;
        validMessage.value = message;
        validMessage.key = key;
        return validMessage;
    }
    
    @Override
    public String getValue()
    {
        return value;
    }
    
    @Override
    public String getField()
    {
        return field;
    }

    @Override
    public String getQualifiedField()
    {
        return qualifiedField;
    }
    
    @Override
    public String getKey()
    {
        return key;
    }

    @Override
    public String toString()
    {
        return "ValidationMessage [field=" + field + ", value=" + value + ", key=" + key + "]";
    }
    

}
