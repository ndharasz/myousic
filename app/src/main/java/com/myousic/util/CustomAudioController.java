package com.myousic.util;

import android.content.Context;
import android.util.Log;

import com.myousic.models.QueuedSong;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class CustomAudioController {
    private static final String TAG = "CustomAudioController";
    private static CustomAudioController instance;

    private SpotifyPlayer player;
    private Config playerConfig;

    private boolean isPaused = false;

    public interface SongPlayingListener {
        public void onPlaying(QueuedSong song);
    }

    public interface SongFinishedListener {
        public void onSongFinished();
    }

    public interface OnInitializedListener {
        public void onInitialized();
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
        playerConfig = new Config(context, authToken, clientID);
    }

    public static CustomAudioController getInstance(Context context, String authToken, String clientID) {
        if (instance == null) {
            return new CustomAudioController(context, authToken, clientID);
        } else {
            return instance;
        }
    }

    public void initialize(final OnInitializedListener onInitializedListener) {
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                player = spotifyPlayer;
                Log.d(TAG, "Player initialized");
                onInitializedListener.onInitialized();
            }

            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error in initialization " + error);
            }
        });
    }

    // Takes in a song and plays it
    public boolean play(final QueuedSong song, final SongPlayingListener onSongPlayingListener) {
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
        NotificationController.togglePlayButton(NotificationController.ACTION_PLAY);
        return true;
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
        NotificationController.togglePlayButton(NotificationController.ACTION_PAUSE);
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

    public void addOnSongFinished(final SongFinishedListener songFinishedListener) {
        player.addNotificationCallback(new Player.NotificationCallback() {
            @Override
            public void onPlaybackEvent(PlayerEvent playerEvent) {
                if (playerEvent == PlayerEvent.kSpPlaybackNotifyAudioDeliveryDone) {
                    isPaused = false;
                    songFinishedListener.onSongFinished();
                }
            }

            @Override
            public void onPlaybackError(Error error) {

            }
        });
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void destroy() {
        pause();
        Spotify.destroyPlayer(this);
        instance = null;
    }
}
