package com.myousic.models;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by brian on 2/12/17.
 */

public class SearchResult {

    private String song;
    private String artist;
    private String album;
    private String imageurl;

    public SearchResult(String song, String artist, String album) {
        this.song = song;
        this.artist = artist;
        this.album = album;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
