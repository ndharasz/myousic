package com.myousic.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.myousic.R;
import com.myousic.models.QueuedSong;
import com.myousic.models.WebAPIWrapper;
import com.myousic.widgets.WidgetSongRow;

import java.util.Queue;

import static com.myousic.R.layout.layout_song_row;

/**
 * Created by ndharasz on 2/13/2017.
 */

public class CustomQueueEventListener implements ChildEventListener {
    private String TAG = "ChildEventListener";
    TableLayout tableLayout;
    Context context;

    public CustomQueueEventListener(Context context, TableLayout tableLayout) {
        this.tableLayout = tableLayout;
        this.context = context;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.getKey().equals("current")) {
            return;
        }
        final QueuedSong result = dataSnapshot.getValue(QueuedSong.class);
        final WidgetSongRow row = (WidgetSongRow) LayoutInflater.from(context)
                .inflate(layout_song_row, null);
        row.setSong(result);
        ((TextView)row.findViewById(R.id.song_name)).setText(result.getName());
        ((TextView)row.findViewById(R.id.song_artist)).setText(result.getArtist());
        WebAPIWrapper.getInstance(context).getAlbumCover(result.getUri()  ,
                new WebAPIWrapper.AlbumCoverListener() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.d(TAG, "Setting bitmap now!");
                        ((ImageView)row.findViewById(R.id.song_image)).setImageBitmap(bitmap);
                        tableLayout.addView(row);
                        tableLayout.invalidate();
                    }
                });
        Log.d(TAG, "Song added: " + result.getName());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getKey().equals("current")) {
            return;
        }
        String removedSong = (String) dataSnapshot.child("name").getValue();
        for(int x = 0; x < tableLayout.getChildCount(); x++) {
            TableRow song = (TableRow) tableLayout.getChildAt(x);
            LinearLayout layout = ((LinearLayout) song.getChildAt(1));
            String name = ((TextView) layout.getChildAt(0)).getText().toString();
            if(name.equals(removedSong)) {
                tableLayout.removeView(song);
                break;
            }
        }
        tableLayout.invalidate();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "Unexpected behavior");
    }

    // gets the next song in the queue
    public QueuedSong getNextSong() {
        if (tableLayout.getChildCount() == 0) {
            return null;
        }
        return (QueuedSong) ((WidgetSongRow) tableLayout.getChildAt(0)).getSong();
    }
}
