package com.myousic.models;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by brian on 2/25/17.
 */

// This is a model for the Spotify playlist object from the web API.
// It is not intended to model myousic playlists such as the queue.
public class Playlist {
    String name;
    String uri;
    List<Song> songs;

    public Playlist(String name, String uri) {
        this.name = name;
        this.uri = uri;
        songs = new LinkedList<>();
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return songs;
    }
}
