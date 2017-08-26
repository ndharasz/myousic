package com.myousic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.myousic.R;
import com.myousic.models.QueuedSong;

import static android.content.Context.MODE_PRIVATE;

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
        SharedPreferences prefs = context.getSharedPreferences("loginPrefs", MODE_PRIVATE);
        final String token = prefs.getString("token", null);
        WebAPIWrapper.getInstance(context).getAlbumCover(token, uri, new WebAPIWrapper.AlbumCoverListener() {
            @Override
            public void onResponse(Bitmap bitmap) {
                song_img.setImageBitmap(bitmap);
                NotificationController.updateImage(bitmap);
            }
        });
    }

    private void showSong(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getKey().equals("current")) {
            QueuedSong song = dataSnapshot.getValue(QueuedSong.class);
            song_name.setText(song.getName());
            song_artist.setText(song.getArtist());
            displayAlbumCover(song.getUri());
            NotificationController.notify(context, song);
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        showSong(dataSnapshot);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        showSong(dataSnapshot);
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
        showSong(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public boolean songPlaying() {
        return !song_name.getText().toString().equals("No song playing");
    }
}
