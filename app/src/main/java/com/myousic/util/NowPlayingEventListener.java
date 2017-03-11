package com.myousic.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

    private RelativeLayout currSongWrapper;
    private View songRow;
    private ImageView song_img;
    private TextView song_name;
    private TextView song_artist;

    public NowPlayingEventListener(Context context, RelativeLayout currSongWrapper) {
        this.context = context;
        this.currSongWrapper = currSongWrapper;
        songRow = View.inflate(context, R.layout.layout_song_row, currSongWrapper);
        song_img = (ImageView)songRow.findViewById(R.id.song_image);
        song_name = (TextView)songRow.findViewById(R.id.song_name);
        song_artist = (TextView)songRow.findViewById(R.id.song_artist);

        song_name.setText("No song playing");
    }

    private void displayAlbumCover(String uri) {
        WebAPIWrapper.getInstance(context).getAlbumCover(uri, new WebAPIWrapper.AlbumCoverListener() {
            @Override
            public void onResponse(Bitmap bitmap) {
                song_img.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.getKey().equals("current")) {
            QueuedSong song = (QueuedSong) dataSnapshot.getValue(QueuedSong.class);
            song_name.setText(song.getName());
            song_artist.setText(song.getArtist());
            displayAlbumCover(song.getUri());
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.getKey().equals("current")) {
            QueuedSong song = (QueuedSong) dataSnapshot.getValue(QueuedSong.class);
            song_name.setText(song.getName());
            song_artist.setText(song.getArtist());
            displayAlbumCover(song.getUri());
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getKey().equals("current")) {
            song_name.setText("No song playing");
            song_artist.setText("");
            song_img.setImageDrawable(null);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.getKey().equals("current")) {
            QueuedSong song = (QueuedSong) dataSnapshot.getValue(QueuedSong.class);
            song_name.setText(song.getName());
            song_artist.setText(song.getArtist());
            displayAlbumCover(song.getUri());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public boolean songPlaying() {
        return !song_name.getText().toString().equals("No song playing");
    }
}
