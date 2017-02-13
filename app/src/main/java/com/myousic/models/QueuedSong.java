package com.myousic.models;

import android.os.SystemClock;

import java.util.Date;

/**
 * Created by brian on 2/12/17.
 *
 * This is the model that's stored in the database and displayed in the queue
 */

public class QueuedSong {
    private String name;
    private String artist;
    private String uri;
    private long timestamp;

    public QueuedSong() {}

    public QueuedSong(SearchResult searchResult) {
        this(searchResult.getSong(), searchResult.getArtist(), searchResult.getUri());
    }

    public QueuedSong(String name, String artist, String uri) {
        this.name = name;
        this.artist = artist;
        this.uri = uri;
        timestamp = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
