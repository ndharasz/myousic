package com.myousic.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myousic.R;
import com.myousic.util.CustomQueueEventListener;
import com.myousic.util.NowPlayingEventListener;
import com.myousic.widgets.WidgetInteractiveTable;
import com.myousic.widgets.WidgetSongRow;

public class ActivityQueue extends AppCompatActivity {
    private String TAG = "ActivityQueue";
    private CustomQueueEventListener customQueueEventListener;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        WidgetInteractiveTable table = (WidgetInteractiveTable) findViewById(R.id.queue_table);
        customQueueEventListener = new CustomQueueEventListener(this, table);
        table.disableDragAndDrop();
        table.disableDelete();
        table.setTableEmptyListener(new WidgetInteractiveTable.TableEmptyListener() {
            @Override
            public void OnTableEmptied() {
                findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
            }
        });

        table.setTableOccupiedListener(new WidgetInteractiveTable.TableOccupiedListener() {
            @Override
            public void OnTableOccupied() {
                findViewById(R.id.empty_message).setVisibility(View.GONE);
            }
        });

        //get party id from shared prefs
        String partyID = getSharedPreferences("Party", Context.MODE_PRIVATE).getString("party_id", null);
        if (partyID == null) {
            Log.d(TAG, "Party ID somehow not set");
            Intent joinPartyIntent = new Intent(this, ActivityJoinParty.class);
            startActivity(joinPartyIntent);
            return;
        }
        //get party db reference using id
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("parties").child(partyID).child("queue");

        //!! change this so that now playing only takes full song view and this
        RelativeLayout songWrapper = (RelativeLayout) findViewById(R.id.now_playing);
        databaseReference.addChildEventListener(new NowPlayingEventListener(this, songWrapper));
        databaseReference.addChildEventListener(customQueueEventListener);
    }

    protected void addSong(View v) {
        Intent searchIntent = new Intent(this, ActivitySearch.class);
        startActivity(searchIntent);
    }
}
