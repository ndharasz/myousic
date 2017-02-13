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
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myousic.R;
import com.myousic.models.QueuedSong;
import com.myousic.util.CustomAudioController;
import com.myousic.util.CustomChildEventListener;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.net.URI;
import java.util.Random;

public class ActivityPartyAdmin extends AppCompatActivity {
    private FirebaseDatabase db;
    DatabaseReference currParty;
    private SharedPreferences loginPrefs;
    private String authToken;
    String currentSongURI = null;
    int currentSongMs = 0;

    String id;

    TextView idField;

    SpotifyPlayer player;

    Button play;
    Button pause;
    Button next;

    ImageView currImg;
    TextView currSong;
    TextView currArtistAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_admin);

        db = FirebaseDatabase.getInstance();
        loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        authToken = loginPrefs.getString("token", "");
        idSetup();
        player = new SpotifyPlayer
                .Builder(new Config(this, authToken, getString(R.string.clientID)))
                .setAudioController(new CustomAudioController())
                .build();
        player.initialize(new Config(this, authToken, getString(R.string.clientID)));
        buttonSetup();
        currSongSetup();
    }

    protected void idSetup() {
        Random rnd = new Random(System.currentTimeMillis());
        int x = rnd.nextInt(65536); //Between 0-255
        id = Integer.toHexString(x);
        getSharedPreferences("Party", Context.MODE_PRIVATE)
                .edit().putString("party_id", id).commit();
        db.getReference().child("parties").child(id).setValue("");
        currParty = db.getReference().child("parties").child(id).getRef();
        currParty.addChildEventListener(new CustomChildEventListener(
                 (TableLayout)findViewById(R.id.queue_table), this));
        idField = (TextView)findViewById(R.id.party_id_field);
        idField.setText("Party ID: "+id);
    }

    protected void addSong(View v) {
        Intent searchIntent = new Intent(this, ActivitySearch.class);
        startActivity(searchIntent);
    }

    protected void buttonSetup() {
        play = (Button)findViewById(R.id.play_btn);
        pause = (Button)findViewById(R.id.pause_btn);
        next = (Button)findViewById(R.id.next_btn);
    }
    protected void currSongSetup() {
        currImg = (ImageView)findViewById(R.id.image);
        currSong = (TextView)findViewById(R.id.song);
        currArtistAlbum = (TextView)findViewById(R.id.artistAlbum);
    }

    protected void play(View v) {
        if(currentSongURI == null) {
            currParty.orderByChild("timestamp").limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            currentSongURI = child.child("uri").getValue().toString();
                            currSong.setText(child.child("name").getValue().toString());
                            currArtistAlbum.setText(child.child("artist").getValue().toString());
                            currParty.child(child.getKey().toString()).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        }
        player.playUri(new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(ActivityPartyAdmin.this, "Unable to play: " + error.name(), Toast.LENGTH_SHORT).show();
            }
        }, currentSongURI, 0, currentSongMs);
    }

    protected void pause(View v) {
        player.pause(new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                currentSongMs = (int)player.getPlaybackState().positionMs;
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(ActivityPartyAdmin.this, "Unable to pause", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void next(View v) {
        currentSongURI = null;
        play(v);
    }
}
