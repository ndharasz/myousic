package com.myousic.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myousic.R;
import com.myousic.models.QueuedSong;
import com.myousic.util.CustomAudioController;
import com.myousic.util.CustomQueueEventListener;
import com.myousic.util.NowPlayingEventListener;

import java.util.Random;

public class ActivityPartyAdmin extends AppCompatActivity {
    private static final String TAG = "ActivityPartyAdmin";
    private FirebaseDatabase db;
    DatabaseReference currParty;
    private String authToken;

    TextView idField;

    Button play;
    Button pause;
    Button next;

    RelativeLayout currSongWrapper;

    CustomAudioController audioControllerInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_admin);

        db = FirebaseDatabase.getInstance();
        authToken = getSharedPreferences("loginPrefs", MODE_PRIVATE).getString("token", "");

        idSetup();
        buttonSetup();
        currSongSetup();
    }

    @Override
    protected void onStart() {
        super.onStart();
        createPlayer();
    }

    protected void idSetup() {
        //generate random party id hex string
        Random rnd = new Random(System.currentTimeMillis());
        int x = rnd.nextInt(65536); //Between 0-255
        String id = Integer.toHexString(x);
        //store in shared preferences
        getSharedPreferences("Party", Context.MODE_PRIVATE)
                .edit().putString("party_id", id).commit();
        //store in db
        db.getReference().child("parties").child(id).setValue("");
        currParty = db.getReference().child("parties").child(id).getRef();
        currParty.addChildEventListener(new CustomQueueEventListener(this,
                (TableLayout)findViewById(R.id.queue_table)));
        idField = (TextView)findViewById(R.id.party_id_field);
        idField.setText(id);
    }

    private void createPlayer() {
        audioControllerInstance = new CustomAudioController(this,
                authToken, getString(R.string.clientID), currParty);
    }

    protected void buttonSetup() {
        play = (Button)findViewById(R.id.play_btn);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        pause = (Button)findViewById(R.id.pause_btn);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });
        next = (Button)findViewById(R.id.next_btn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    protected void currSongSetup() {
        currSongWrapper = (RelativeLayout)findViewById(R.id.curr_song_wrapper);
        currParty.addChildEventListener(new NowPlayingEventListener(this, currSongWrapper));
    }

    public void addSong(View v) {
        Log.d(TAG, "should not hit this");
        Intent searchIntent = new Intent(this, ActivitySearch.class);
        startActivity(searchIntent);
    }


    public void play() {
        Log.d(TAG, "should hit this");
        boolean playing = audioControllerInstance.play();
        if(playing) {
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Song was not able to be played");
        }
    }

    public void pause() {
        boolean paused = audioControllerInstance.pause();
        if (paused) {
            play.setVisibility(View.VISIBLE);
            pause.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "Song was not able to be paused");
        }
    }

    public void next() {
        audioControllerInstance.next();
    }
}
