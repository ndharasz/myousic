package com.myousic.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.myousic.R;
import com.myousic.activities.ActivityQueue;
import com.myousic.models.QueuedSong;

import static com.myousic.R.layout.layout_queue_row;

/**
 * Created by ndharasz on 2/13/2017.
 */

public class CustomChildEventListener implements ChildEventListener {
    private String TAG = "ChildEventListener";
    TableLayout tableLayout;
    Context context;

    public CustomChildEventListener(TableLayout tableLayout, Context context) {
        this.tableLayout = tableLayout;
        this.context = context;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        QueuedSong result = (QueuedSong) dataSnapshot.getValue(QueuedSong.class);
        Log.d(TAG, "Song added: " + result.getName());
        TableRow row = (TableRow) LayoutInflater.from(context)
                .inflate(layout_queue_row, null);
        ((TextView)row.findViewById(R.id.song_name)).setText(result.getName());
        ((TextView)row.findViewById(R.id.artist_name)).setText(result.getArtist());
        tableLayout.addView(row);
        tableLayout.invalidate();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String removedSong = (String) dataSnapshot.child("name").getValue();
        for(int x = 0; x < tableLayout.getChildCount(); x++) {
            TableRow song = (TableRow) tableLayout.getChildAt(x);
            String name = ((TextView) song.getChildAt(0)).getText().toString();
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
}
