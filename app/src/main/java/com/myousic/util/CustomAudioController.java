package com.myousic.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myousic.R;
import com.myousic.models.QueuedSong;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.Queue;

public class CustomAudioController {
    private static final String TAG = "CustomAudioController";

    private SpotifyPlayer player;
    private DatabaseReference partyDB;
    private QueuedSong currentSong;
    private int currSongMs;
    private QueuedSong nextSong;

    private boolean isPaused;

    //Basic callback for logging events
    private final Player.OperationCallback operationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "Task completed");
        }

        @Override
        public void onError(Error error) {
            Log.d(TAG, "Error completing task");
        }
    };

    //Cunstructor for setting up party Database and spotify player
    public CustomAudioController(Context context, String authToken, String clientID,
                                 DatabaseReference partyDB) {
        this.partyDB = partyDB;
        Config playerConfig = new Config(context, authToken, clientID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                player = spotifyPlayer;
                Log.d(TAG, "Player initialized");
            }

            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error in initialization " + error);
            }
        });
    }

    public boolean play() {
        //Edge-case: First song on queue playing
        if(currentSong == null) {
            //Get first two songs based on timestamp
            partyDB.orderByChild("timestamp").limitToFirst(2)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //loop through two songs
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            Log.d(TAG, child.toString());
                            //first song should set to current
                            //Second song set to next
                            if(currentSong == null) {
                                currentSong = child.getValue(QueuedSong.class);
                            } else {
                                nextSong = child.getValue(QueuedSong.class);
                            }
                            //if queue not empty
                            if(currentSong != null) {
                                //remove current song from queue
                                partyDB.child(Long.toString(currentSong.getTimestamp())).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //When removal is complete, play current song
                                                player.playUri(operationCallback, currentSong.getUri(), 0, 0);
                                                //Set current song
                                                partyDB.child("current").setValue(currentSong);
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        } else if(!isPaused) {
            //if it's already playing, do nothing
            return false;
        } else {
            //if a current osng is pause, resum
            player.resume(operationCallback);
        }
        //switch boolean and return positive
        isPaused = false;
        return true;
    }

    public boolean pause() {
        //if playing then pause, otherwise it was already paused
        if (player.getPlaybackState().isPlaying) {
            isPaused = true;
            player.pause(operationCallback);
            return true;
        } else {
            Log.d(TAG, "Player was already paused");
            return false;
        }
    }

    public boolean next() {
        //Edge-case: last song on list
        if(nextSong == null) {
            return false;
        }
        //Move to next song
        currentSong = nextSong;
        Log.d("Current Song", currentSong.getName());
        //remove current song from DB
        partyDB.child(Long.toString(currentSong.getTimestamp())).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //When removal complete, play current song
                        player.playUri(operationCallback, currentSong.getUri(), 0, 0);
                        //Set current song in DB
                        partyDB.child("current").setValue(currentSong);
                            //set next song variable to next song in queue
                        partyDB.orderByChild("timestamp").limitToFirst(1)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            nextSong = child.getValue(QueuedSong.class);
                                            Log.d("Next Song", child.toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                    }
                });
        return true;
    }
}
