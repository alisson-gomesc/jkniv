package net.sf.jkniv.sqlegance.validation;

class ValidationMessageImpl implements ValidationMessage
{
    private String value;
    private String field;
    private String key;
    
    
    static ValidationMessage of(String field, String message, String key)
    {
        ValidationMessageImpl validMessage = new ValidationMessageImpl();
        validMessage.field = field;
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
