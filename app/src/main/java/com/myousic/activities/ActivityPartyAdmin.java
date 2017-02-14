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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myousic.R;
import com.myousic.models.QueuedSong;
import com.myousic.util.CustomAudioController;
import com.myousic.util.CustomChildEventListener;
import java.util.Random;

public class ActivityPartyAdmin extends AppCompatActivity {
    private static final String TAG = "ActivityPartyAdmin";
    private FirebaseDatabase db;
    DatabaseReference currParty;
    private SharedPreferences loginPrefs;
    private String authToken;

    QueuedSong currentSong;
    QueuedSong nextSong;

    String id;

    TextView idField;

    Button play;
    Button pause;
    Button next;

    ImageView currImg;
    TextView currSong;
    TextView currArtistAlbum;

    CustomAudioController audioControllerInstance;

    // When a song is retrieved, do this action
    private interface SongReceivedAction {
        public void onSongReceived(QueuedSong song);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_admin);

        db = FirebaseDatabase.getInstance();
        loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        authToken = loginPrefs.getString("token", "");

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

    private void createPlayer() {
        Log.d(TAG, "Creating player with auth token: " + authToken);
        Log.d(TAG, "Client ID: " + getString(R.string.clientID));
        audioControllerInstance = CustomAudioController.getInstance(ActivityPartyAdmin.this,
                authToken, getString(R.string.clientID));
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

    protected void removeSongFromQueue(QueuedSong song) {
        // We should add this song to a "Currently playing" table in the database
        // so that other apps can see the current song
        currParty.child(Long.toString(song.getTimestamp())).removeValue();
    }

    // Puts the next song in the nextSong variable
    protected QueuedSong getNextSong(final SongReceivedAction songReceivedAction) {
        currParty.orderByChild("timestamp").limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            QueuedSong song = (QueuedSong) child.getValue(QueuedSong.class);
                            songReceivedAction.onSongReceived(song);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return nextSong;
    }


    protected void play(View v) {
        if(!audioControllerInstance.isPaused()) {
            // if there is no song to play, fetch the song
            getNextSong(new SongReceivedAction() {
                @Override
                public void onSongReceived(QueuedSong song) {
                    removeSongFromQueue(song);
                    currSong.setText(song.getName());
                    currArtistAlbum.setText(song.getArtist());
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                    audioControllerInstance.play(song.getUri());
                }
            });
        } else {
            // if the audio was previously paused, just resume play
            audioControllerInstance.resume();
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        }
    }

    protected void pause(View v) {
        boolean paused = audioControllerInstance.pause();
        if (paused) {
            play.setVisibility(View.VISIBLE);
            pause.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "Song was not able to be paused");
        }
    }

    protected void next(View v) {
        // skip to the next song, update the local song list,
        // then queue next song
        getNextSong(new SongReceivedAction() {
            @Override
            public void onSongReceived(QueuedSong song) {
                removeSongFromQueue(song);
                currSong.setText(song.getName());
                currArtistAlbum.setText(song.getArtist());
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                audioControllerInstance.play(song.getUri());
            }
        });
    }
}
