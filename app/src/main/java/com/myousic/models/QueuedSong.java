package com.myousic.models;


import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by brian on 2/12/17.
 *
 * This is the model that's stored in the database and displayed in the queue
 */

public class QueuedSong extends Song {
    private long timestamp;

    public QueuedSong(){}

    public QueuedSong(Song song) {
        super(song.getName(), song.getArtist(), song.getAlbum(), song.getUri());
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
