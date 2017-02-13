package com.myousic.activities;

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
import com.myousic.util.CustomChildEventListener;

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
        databaseReference.addChildEventListener(new CustomChildEventListener(tableLayout, this));
    }

    protected void addSong(View v) {
        Intent searchIntent = new Intent(this, ActivitySearch.class);
        startActivity(searchIntent);
    }
}
