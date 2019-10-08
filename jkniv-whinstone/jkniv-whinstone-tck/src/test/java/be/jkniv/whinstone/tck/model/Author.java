
package be.jkniv.whinstone.tck.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import net.sf.jkniv.whinstone.CallbackScope;
import net.sf.jkniv.whinstone.PreCallBack;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "name", "nationality", "books" })
public class Author
{
    
    @JsonProperty("_id")
    private String     id;
    @JsonProperty("_rev")
    private String     rev;
    @JsonProperty("name")
    private String     name;
    @JsonProperty("nationality")
    private String     nationality;
    @JsonProperty("books")
    private List<Book> books = null;
    private Date       updateAt;
    private Date       addAt;
    private Integer    born;
    
    private int totalAdd;
    private int totalUpdate;
    private int totalRemove;
    private int totalSelect;
    
    public Author()
    {
        this.totalAdd = 0;
        this.totalRemove = 0;
        this.totalSelect = 0;
        this.totalUpdate = 0;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getRev()
    {
        return rev;
    }
    
    public void setRev(String rev)
    {
        this.rev = rev;
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
    
    public Date getUpdateAt()
    {
        return updateAt;
    }
    
    public void setUpdateAt(Date updateAt)
    {
        this.updateAt = updateAt;
    }
    
    public Date getAddAt()
    {
        return addAt;
    }
    
    public void setAddAt(Date addAt)
    {
        this.addAt = addAt;
    }
    
    @PreCallBack(scope = CallbackScope.UPDATE)
    public void updateAt()
    {
        this.updateAt = new Date();
    }
    
    @PreCallBack(scope = CallbackScope.ADD)
    public void addAt()
    {
        this.addAt = new Date();
    }

    @PreCallBack(scope = CallbackScope.ADD)
    public void incrementAdd()
    {
        this.totalAdd++;
    }

    @PreCallBack(scope = CallbackScope.SELECT)
    public void incrementSelect()
    {
        this.totalAdd++;
    }

    public int getTotalAdd()
    {
        return this.totalAdd;
    }
    
    public int getTotalUpdate()
    {
        return totalUpdate;
    }

    public int getTotalRemove()
    {
        return totalRemove;
    }

    public int getTotalSelect()
    {
        return totalSelect;
    }

    @Override
    public String toString()
    {
        return "Author [id=" + id + ", rev=" + rev + ", name=" + name + ", nationality=" + nationality + ", books="
                + books + ", updateAt=" + updateAt + ", addAt=" + addAt + ", born=" + born + "]";
    }
}
