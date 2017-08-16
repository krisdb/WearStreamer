package com.wear.streamer;

import java.net.MalformedURLException;
import java.net.URL;

public class RssItem implements Comparable<RssItem>
{
    private String title, description, date;
    private URL link, media;
    private Boolean isRadio = false;
    private int id;

    public Boolean IsRadio() {
        return isRadio;
    }

    public void setIsRadio(final Boolean isRadio) {
        this.isRadio = isRadio;
    }

    public URL getMedia() {
        return media;
    }

    public void setMedia(final String url) {
        try
        {
            this.media = new URL(url);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setTitle(final String title) {
        this.title = title.trim();
    }

    public void setId(final int id) {
        this.id = id;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(final String link) {
        try
        {
            this.link = new URL(link);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String d) {
        this.date = d;
    }

    public int compareTo(final RssItem another) {
        if (another == null) return 1;
        // sort descending, most recent first
        return another.date.compareTo(date);
    }
}