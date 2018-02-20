package net.sf.jkniv.sqlegance.builder.xml;

/**
 * 
 * TODO implementations needs to implement select-columns element in XML.
 * 
 * @author Alisson Gomes
 *
 */
public class SelectColumnsTag
{
    public static final String TAG_NAME           = "select-columns";
    public static final String ATTRIBUTE_PROPERTY = "id";
    
    private String             id;
    private String             text;
    
    public SelectColumnsTag(String id, String text)
    {
        super();
        this.id = id;
        this.text = text;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
    
}
