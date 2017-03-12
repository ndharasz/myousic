package com.myousic.util;

import com.myousic.models.Song;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brian on 3/6/17.
 *
 * #Singleton
 *
 * A simple queue to push and pop a song
 * with the ability to swap songs
 *
 */

public class LocalPlaylistController implements Iterable<Song> {
    public static LocalPlaylistController instance;
    public List<Song> playlist;

    public LocalPlaylistController() {
        playlist = new LinkedList<>();
    }

    public static LocalPlaylistController getInstance() {
        if (instance == null) {
            instance = new LocalPlaylistController();
        }
        return instance;
    }

    public void move(int oldPos, int newPos) {
        Song song = playlist.remove(oldPos);
        playlist.add(newPos, song);
    }

    public void remove(int index) {
        playlist.remove(index);
    }

    public void push(Song song) {
        playlist.add(song);
    }

    public Song pop() {
        if (playlist.size() == 0) {
            return null;
        }
        Song song = playlist.get(0);
        playlist.remove(0);
        return song;
    }

    @Override
    public Iterator<Song> iterator() {
        return new Iterator<Song>() {
            int index = 0;
            @Override
            public boolean hasNext() {
                return index < playlist.size();
            }

            @Override
            public Song next() {
                Song song = playlist.get(index);
                index++;
                return song;
            }
        };
    }
}
