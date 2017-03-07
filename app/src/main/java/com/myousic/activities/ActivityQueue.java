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
import com.myousic.widgets.WidgetSongRow;

public class ActivityQueue extends AppCompatActivity {
    private String TAG = "ActivityQueue";
    protected TableLayout queueLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        queueLayout = (TableLayout) findViewById(R.id.queue_table);
        RelativeLayout songWrapper = (RelativeLayout) findViewById(R.id.now_playing);

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
        DatabaseReference databaseReference = database.getReference("parties").child(partyID).child("queue");
        //add a listener for when the db queue changes
        //handles adding/removing songs from UI
        //!!change this so that "this" is first arg
        databaseReference.addChildEventListener(new CustomQueueEventListener(this, queueLayout));

        //!! change this so that now playing only takes full song view and this
        databaseReference.addChildEventListener(new NowPlayingEventListener(this, songWrapper));
    }

    protected void addSong(View v) {
        Intent searchIntent = new Intent(this, ActivitySearch.class);
        startActivity(searchIntent);
    }
}
