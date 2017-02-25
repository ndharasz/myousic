package com.myousic.models;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by brian on 2/12/17.
 */

public class Song {

    private String song;
    private String artist;
    private String album;
    private String uri;

    public Song(){};

    public Song(String song, String artist, String album, String uri) {
        this.song = song;
        this.artist = artist;
        this.album = album;
        this.uri = uri;
    }

    public String getName() {
        return song;
    }

    public void setName(String song) {
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
