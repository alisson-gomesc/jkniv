
package net.sf.jkniv.whinstone.couchdb.model.orm;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorView
{
    
    private String     id;
    private String     key;
    private String     name;
    private String     nationality;
    private List<Book> books = null;
    
    private Integer    born;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public void setKey(String key)
    {
        this.key = key;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Integer getBorn()
    {
        return born;
    }
    
    public void setBorn(Integer born)
    {
        this.born = born;
    }
    
    public String getNationality()
    {
        return nationality;
    }
    
    public void setNationality(String nationality)
    {
        this.nationality = nationality;
    }
    
    public List<Book> getBooks()
    {
        return books;
    }
    
    public void setBooks(List<Book> books)
    {
        this.books = books;
    }
    
    @Override
    public String toString()
    {
        return "AuthorView [id=" + id + ", key=" + key + ", name=" + name + ", nationality=" + nationality + ", books="
                + books + ", born=" + born + "]";
    }
    
}
