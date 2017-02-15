package com.myousic.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.myousic.R;
import com.myousic.models.QueuedSong;
import com.myousic.models.WebAPIWrapper;

/**
 * Created by brian on 2/14/17.
 */

public class NowPlayingEventListener implements ChildEventListener {
    private Context context;

    private ImageView artistImage;
    private TextView songView;
    private TextView artistAlbumView;

    public NowPlayingEventListener(Context context, ImageView artistImage, TextView songView, TextView artistAlbumView) {
        this.context = context;
        this.artistImage = artistImage;
        this.songView = songView;
        this.artistAlbumView = artistAlbumView;

        songView.setText("No song playing");
    }

    private void displayAlbumCover(String uri) {
        WebAPIWrapper instance = WebAPIWrapper.getInstance(context);
        instance.getAlbumCover(uri, new WebAPIWrapper.AlbumCoverListener() {
            @Override
            public void onResponse(Bitmap bitmap) {
                artistImage.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.getKey().equals("current")) {
            QueuedSong song = (QueuedSong) dataSnapshot.getValue(QueuedSong.class);
            songView.setText(song.getName());
            artistAlbumView.setText(song.getArtist());
            displayAlbumCover(song.getUri());
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.getKey().equals("current")) {
            QueuedSong song = (QueuedSong) dataSnapshot.getValue(QueuedSong.class);
            songView.setText(song.getName());
            artistAlbumView.setText(song.getArtist());
            displayAlbumCover(song.getUri());
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getKey().equals("current")) {
            songView.setText("No song playing");
            artistAlbumView.setText("");
            artistImage.setImageDrawable(null);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.getKey().equals("current")) {
            QueuedSong song = (QueuedSong) dataSnapshot.getValue(QueuedSong.class);
            songView.setText(song.getName());
            artistAlbumView.setText(song.getArtist());
            displayAlbumCover(song.getUri());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
