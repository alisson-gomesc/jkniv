
package net.sf.jkniv.whinstone.couchdb.model.orm;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "isbn",
    "name",
    "published",
    "lastView",
    "visualization"
})
public class Book {

    @JsonProperty("isbn")
    private String isbn;
    @JsonProperty("name")
    private String name;
    @JsonProperty("published")
    private Long published;
    @JsonProperty("lastView")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date lastView;
    @JsonProperty("visualization")
    private Long visualization;

    @JsonProperty("isbn")
    public String getIsbn() {
        return isbn;
    }

    @JsonProperty("isbn")
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("published")
    public Long getPublished() {
        return published;
    }

    @JsonProperty("published")
    public void setPublished(Long published) {
        this.published = published;
    }

    @JsonProperty("lastView")
    public Date getLastView() {
        return lastView;
    }

    @JsonProperty("lastView")
    public void setLastView(Date lastView) {
        this.lastView = lastView;
    }

    @JsonProperty("visualization")
    public Long getVisualization() {
        return visualization;
    }

    @JsonProperty("visualization")
    public void setVisualization(Long visualization) {
        this.visualization = visualization;
    }

    @Override
    public String toString()
    {
        return "Book [isbn=" + isbn + ", name=" + name + ", published=" + published + ", lastView=" + lastView
                + ", visualization=" + visualization + "]";
    }

    
}
