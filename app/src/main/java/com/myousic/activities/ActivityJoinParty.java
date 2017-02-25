package com.myousic.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myousic.R;

public class ActivityJoinParty extends AppCompatActivity {
    private FirebaseDatabase db;
    private EditText partyIDField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_party);
        partyIDField = (EditText) findViewById(R.id.party_id);
        db = FirebaseDatabase.getInstance();
    }

    protected void join(View v) {
        //get input part id field and check db for it
        final String id = partyIDField.getText().toString();
        db.getReference().child("parties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(id)) {
                    //if id exists in db then save it to shared prefs
                    SharedPreferences.Editor editor = getSharedPreferences("Party", Context.MODE_PRIVATE).edit();
                    editor.putString("party_id", id);
                    editor.commit();
                    //start Activity Queue
                    Intent queueIntent = new Intent(ActivityJoinParty.this, ActivityQueue.class);
                    startActivity(queueIntent);
                } else {
                    Toast.makeText(ActivityJoinParty.this, "Party does not exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ActivityJoinParty.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
