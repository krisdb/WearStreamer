package com.wear.streamer;

import java.net.MalformedURLException;
import java.net.URL;

public class PodcastItem implements Comparable<PodcastItem>
{
    private String title, description, date;
    private URL url, mediaurl;
    private int pid, eid;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title.trim();
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        try
        {
            this.url = new URL(url);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public URL getMediaUrl() {
        return mediaurl;
    }

    public void setMediaUrl(final String url) {
        try
        {
            this.mediaurl = new URL(url);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }
    public int getPodcastId() { return pid; }

    public void setPodcastId(final int id) { this.pid = id; }

    public int getEpisodeId() { return eid; }

    public void setEpisodeId(final int id) { this.eid = id; }



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