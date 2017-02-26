package com.myousic.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myousic.models.QueuedSong;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by brian on 2/26/17.
 */

public class CustomPlaylistListener implements ChildEventListener {
    private static final String TAG = "CustomPlaylistListener";
    private int index;

    List<QueuedSong> songs;

    public CustomPlaylistListener() {
        index = 0;
        songs = new LinkedList<>();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "Song found");
        QueuedSong song = dataSnapshot.getValue(QueuedSong.class);
        songs.add(song);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
