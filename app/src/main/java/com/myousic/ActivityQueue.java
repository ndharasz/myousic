package com.myousic;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myousic.R;
import com.myousic.models.QueuedSong;

import static com.myousic.R.layout.layout_queue_row;

public class ActivityQueue extends AppCompatActivity {
    private String TAG = "ActivityQueue";
    protected TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        tableLayout = (TableLayout) findViewById(R.id.queue_table);

        String partyID = getSharedPreferences("Party", Context.MODE_PRIVATE).getString("party_id", null);
        if (partyID == null) {
            Log.d(TAG, "Party ID somehow not set");
            Intent joinPartyIntent = new Intent(this, ActivityJoinParty.class);
            startActivity(joinPartyIntent);
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("parties").child(partyID);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                QueuedSong result = (QueuedSong) dataSnapshot.getValue(QueuedSong.class);
                Log.d(TAG, "Song added: " + result.getName());
                TableRow row = (TableRow) LayoutInflater.from(ActivityQueue.this)
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
        });
    }

    protected void addSong(View v) {
        Intent searchIntent = new Intent(this, ActivitySearch.class);
        startActivity(searchIntent);
    }
}
