package vandy.skyver.model.mediator.webdata;

import java.util.Objects;
import java.util.Set;

/**
 * This "Plain Ol' Java Object" (POJO) class represents meta-data of
 * interest downloaded in Json from the Video Service via the
 * VideoSvcApi.
 */
public class Video {
    /**
     * Various fields corresponding to data downloaded in Json from
     * the Video WebService.
     */
    private long id;
    private String title;
    private long duration;
    private String location;
    private String subject;
    private String contentType;
    private String url;
    private long likes;
    private double starRating;
	
    /**
     * No-op constructor
     */
    public Video() {
    }
    
    /**
     * Constructor that initializes title, duration, and contentType.
     */
    public Video( String name, long duration, String contentType
                 ) {

        this.title = name;
        this.contentType = contentType;
        this.duration = duration;

    }

    /**
     * Constructor that initializes all the fields of interest.
     */
    public Video(long id,
                 String title,
                 long duration,
                 String contentType,
                 String dataUrl, double starRating) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.contentType = contentType;
        this.url = dataUrl;
        this.starRating = starRating;
    }

    /*
     * Getters and setters to access Video.
     */

    public String getTitle() {return title;	}

    public void setTitle(String title) {this.title = title;}

    public String getUrl() {return url;	}

    public void setUrl(String url) {this.url = url;}

    public long getDuration() {return duration;}

    public void setDuration(long duration) {this.duration = duration;}

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public double getStarRating() {return starRating;}

    public void setStarRating(double starRating) {this.starRating = starRating;}


    /**
     * @return the textual representation of Video object.
     */

    @Override
    public String toString() {
        return "{" +
            "Id: "+ id + ", "+
            "Title: "+ title + ", "+
            "Duration: "+ duration + ", "+
            "ContentType: "+ contentType + ", "+
            "Data URL: "+ url + ", "+
            "}";
    }

    /** 
     * @return an Integer hash code for this object. 
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTitle(),
                            getDuration());
    }

    /**
     * @return Compares this Video instance with specified 
     *         Video and indicates if they are equal.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Video)
            && Objects.equals(getTitle(),
                              ((Video) obj).getTitle())
            && getDuration() == ((Video) obj).getDuration();
    }
}
