package com.myousic.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.myousic.R;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

/*
 * Brian Woodbury
 *
 * Singleton containing all the play methods for spotify
 */
public class CustomAudioController {
    private static CustomAudioController instance;
    private static final String TAG = "CustomAudioController";

    private SpotifyPlayer player;
    private boolean isPaused = false;

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

    public CustomAudioController(Context context, String authToken, String clientID) {
        Config playerConfig = new Config(context, authToken, clientID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                player = spotifyPlayer;
                Log.d(TAG, "Player initialized");
            }

            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error in initilzation" + error.getMessage());
            }
        });
        if (!player.isInitialized()) {
            Log.d(TAG, "Initialization has not yet occured");
        }
    }

    public static CustomAudioController getInstance(Context context, String authToken, String clientID) {
        if (instance == null) {
            return new CustomAudioController(context, authToken, clientID);
        } else {
            instance.player.login(authToken);
            return instance;
        }
    }

    public boolean play(String uri) {
        if (!player.getPlaybackState().isPlaying) {
            isPaused = false;
            player.playUri(operationCallback, uri, 0, 0);
            return true;
        } else {
            Log.d(TAG, "Player was already playing");
            return false;
        }
    }

    public void queueNext(String uri) {
        player.queue(operationCallback, uri);
    }

    public void skip() {
        player.skipToNext(operationCallback);
    }

    public boolean pause() {
        if (player.getPlaybackState().isPlaying) {
            isPaused = true;
            player.pause(operationCallback);
            return true;
        } else {
            Log.d(TAG, "Player was already paused");
            return false;
        }
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

    public boolean isPaused() {
        return isPaused;
    }
}
