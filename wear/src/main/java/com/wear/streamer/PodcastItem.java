package com.wear.streamer;

import java.net.MalformedURLException;
import java.net.URL;

public class PodcastItem implements Comparable<PodcastItem>
{
    private String title, description, date;
    private URL link;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title.trim();
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

    public int compareTo(final PodcastItem another) {
        if (another == null) return 1;
        // sort descending, most recent first
        return another.date.compareTo(date);
    }
}