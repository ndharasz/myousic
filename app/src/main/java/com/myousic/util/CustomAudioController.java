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
import com.myousic.models.Song;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.Queue;

public class CustomAudioController {
    private static final String TAG = "CustomAudioController";
    private static CustomAudioController instance;

    private SpotifyPlayer player;

    private boolean isPaused = false;

    public interface SongPlayingListener {
        public void onPlaying(QueuedSong song);
    }

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

    //Constructor for setting up party Database and spotify player
    private CustomAudioController(Context context, String authToken, String clientID) {
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

    public static CustomAudioController getInstance(Context context, String authToken, String clientID) {
        if (instance == null) {
            return new CustomAudioController(context, authToken, clientID);
        } else {
            return instance;
        }
    }

    // Takes in a song and plays it
    public boolean play(final QueuedSong song, final SongPlayingListener onSongPlayingListener) {
        if (!player.getPlaybackState().isPlaying) {
            isPaused = false;
            player.playUri(new Player.OperationCallback() {
                @Override
                public void onSuccess() {
                    onSongPlayingListener.onPlaying(song);
                }

                @Override
                public void onError(Error error) {

                }
            }, song.getUri(), 0, 0);
            return true;
        } else {
            Log.d(TAG, "Player was already playing");
            return false;
        }
    }

    public boolean next(final QueuedSong song, final SongPlayingListener onSongPlayingListener) {
        player.playUri(new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                onSongPlayingListener.onPlaying(song);
            }

            @Override
            public void onError(Error error) {

            }
        }, song.getUri(), 0, 0);
        return true;
    }

    public boolean resume() {
        if (!player.getPlaybackState().isPlaying) {
            isPaused = false;
            player.resume(operationCallback);
            return true;
        } else {
            Log.d(TAG, "Player was already playing");
            return false;
        }
    }

    public boolean pause() {
        //if playing then pause, otherwise it was already paused
        if (player.getPlaybackState().isPlaying) {
            player.pause(operationCallback);
            isPaused = true;
            return true;
        } else {
            Log.d(TAG, "Player was already paused");
            return false;
        }
    }

    public boolean isPaused() {
        return isPaused;
    }
}
