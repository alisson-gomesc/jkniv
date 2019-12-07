package net.sf.jkniv.whinstone.couchbase.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "author", "content", "date", "ratings" })
public class Review
{
    
    @JsonProperty("author")
    private String              author;
    @JsonProperty("content")
    private String              content;
    @JsonProperty("date")
    private String              date;
    @JsonProperty("ratings")
    private Ratings             ratings;
    
    @JsonProperty("author")
    public String getAuthor()
    {
        return author;
    }
    
    @JsonProperty("author")
    public void setAuthor(String author)
    {
        this.author = author;
    }
    
    @JsonProperty("content")
    public String getContent()
    {
        return content;
    }
    
    @JsonProperty("content")
    public void setContent(String content)
    {
        this.content = content;
    }
    
    @JsonProperty("date")
    public String getDate()
    {
        return date;
    }
    
    @JsonProperty("date")
    public void setDate(String date)
    {
        this.date = date;
    }
    
    @JsonProperty("ratings")
    public Ratings getRatings()
    {
        return ratings;
    }
    
    @JsonProperty("ratings")
    public void setRatings(Ratings ratings)
    {
        this.ratings = ratings;
    }
}
