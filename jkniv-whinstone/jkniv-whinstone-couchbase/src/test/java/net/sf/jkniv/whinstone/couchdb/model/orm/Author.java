
package net.sf.jkniv.whinstone.couchdb.model.orm;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import net.sf.jkniv.whinstone.CallbackScope;
import net.sf.jkniv.whinstone.PreCallBack;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "nationality",
    "books"
})
public class Author {

    @JsonProperty("_id")
    private String id;
    @JsonProperty("_rev")
    private String rev;
    
    @JsonProperty("name")
    private String name;
    @JsonProperty("nationality")
    private String nationality;
    @JsonProperty("books")
    private List<Book> books = null;
    private Date updateAt;
    private Date addAt;
    
    private Integer born;

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("_rev")
    public String getRev() {
        return rev;
    }

    @JsonProperty("_rev")
    public void setRev(String rev) {
        this.rev = rev;
    }
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("born")
    public Integer getBorn()
    {
        return born;
    }
    
    @JsonProperty("born")
    public void setBorn(Integer born)
    {
        this.born = born;
    }
    
    @JsonProperty("nationality")
    public String getNationality() {
        return nationality;
    }

    @JsonProperty("nationality")
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @JsonProperty("books")
    public List<Book> getBooks() {
        return books;
    }

    @JsonProperty("books")
    public void setBooks(List<Book> books) {
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

    @PreCallBack(scope=CallbackScope.UPDATE)
    public void updateAt()
    {
        this.updateAt = new Date();
    }

    @PreCallBack(scope=CallbackScope.ADD)
    public void addAt()
    {
        this.addAt = new Date();
    }

    @Override
    public String toString()
    {
        return "Author [id=" + id + ", rev=" + rev + ", name=" + name + ", nationality=" + nationality + ", books="
                + books + ", updateAt=" + updateAt + ", addAt=" + addAt + ", born=" + born + "]";
    }



}
