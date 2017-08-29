package com.myousic.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myousic.R;
import com.myousic.models.QueuedSong;
import com.myousic.models.Song;
import com.myousic.util.CustomAudioController;
import com.myousic.util.CustomQueueEventListener;
import com.myousic.util.LocalPlaylistController;
import com.myousic.util.NotificationController;
import com.myousic.util.NowPlayingEventListener;
import com.myousic.util.SearchController;
import com.myousic.widgets.WidgetInteractiveTable;
import com.spotify.sdk.android.player.Spotify;

import java.util.Random;

public class ActivityPartyAdmin extends AppCompatActivity {
    private static final String TAG = "ActivityPartyAdmin";
    private FirebaseDatabase db;
    DatabaseReference currParty;
    private String authToken;
    private String partyId;

    TextView idField;

    Button play;
    Button pause;
    Button next;

    RelativeLayout songWrapper;

    CustomAudioController audioControllerInstance;
    CustomQueueEventListener customQueueEventListener;
    NowPlayingEventListener nowPlayingEventListener;

    NotificationPlay notificationPlay;
    NotificationPause notificationPause;
    NotificationNext notificationNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_admin);

        db = FirebaseDatabase.getInstance();
        authToken = getSharedPreferences("loginPrefs", MODE_PRIVATE).getString("token", "");

        WidgetInteractiveTable table = (WidgetInteractiveTable) findViewById(R.id.queue_table);
        customQueueEventListener = new CustomQueueEventListener(this, table);
        table.disableDragAndDrop();
        table.setOnSongDeletedListener(new WidgetInteractiveTable.OnSongDeletedListener() {
            @Override
            public void onSongDeleted(Song song, int pos) {
                QueuedSong queuedSong = (QueuedSong) song;
                currParty.child(String.valueOf(queuedSong.getTimestamp())).removeValue();
            }
        });
        table.setTableEmptyListener(new WidgetInteractiveTable.TableEmptyListener() {
            @Override
            public void OnTableEmptied() {
                findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
            }
        });

        table.setTableOccupiedListener(new WidgetInteractiveTable.TableOccupiedListener() {
            @Override
            public void OnTableOccupied() {
                findViewById(R.id.empty_message).setVisibility(View.GONE);
            }
        });

        idSetup();
        buttonSetup();
        currSongSetup();

        // Init Notification Receivers and register them
        notificationPlay = new NotificationPlay();
        notificationPause = new NotificationPause();
        notificationNext = new NotificationNext();
        registerReceiver(notificationPlay, new IntentFilter(NotificationController.ACTION_PLAY));
        registerReceiver(notificationPause, new IntentFilter(NotificationController.ACTION_PAUSE));
        registerReceiver(notificationNext, new IntentFilter(NotificationController.ACTION_NEXT));
    }

    @Override
    protected void onStart() {
        super.onStart();
        createPlayer();
    }

    @Override
    public void onDestroy() {
        audioControllerInstance.destroy();
        String id = getSharedPreferences("Party", Context.MODE_PRIVATE).getString("party_id", "");
        FirebaseDatabase.getInstance().getReference().child("parties").child(id).removeValue();
        LocalPlaylistController.getInstance().destroy();
        NotificationController.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        final View dialogView = LayoutInflater.from(ActivityPartyAdmin.this)
                .inflate(R.layout.layout_confirmation, null);
        ((TextView) dialogView.findViewById(R.id.warning_message)).setText(
                "Exiting this screen will delete your party information."
        );
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                ActivityPartyAdmin.this, R.style.thinDialog);
        dialogBuilder.setTitle("Are you sure?");
        dialogBuilder.setView(dialogView);

        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityPartyAdmin.super.onBackPressed();
            }
        });
        dialogBuilder.create().show();
    }

    protected void idSetup() {
        //get from shared preferences
        String id = getSharedPreferences("Party", MODE_PRIVATE).getString("party_id", null);
        if (id == null) {
            onDestroy();
        }

        //store in db
        currParty = db.getReference().child("parties").child(id).child("queue").getRef();
        currParty.addChildEventListener(customQueueEventListener);
        idField = (TextView)findViewById(R.id.party_id_field);
        idField.setText(id);
        partyId = id;
    }

    private void createPlayer() {
        // This call gets an instance of the CustomAudioController ONLY
        audioControllerInstance = CustomAudioController.getInstance(this,
                authToken, getString(R.string.clientID));
        // This call initializes the player. Only when it's initialized can we set
        //      any listeners we might need for the player.
        audioControllerInstance.initialize(new CustomAudioController.OnInitializedListener() {
            @Override
            public void onInitialized() {
                audioControllerInstance.addOnSongFinished(
                        new CustomAudioController.SongFinishedListener() {
                            @Override
                            public void onSongFinished() {
                                next();
                            }
                        });
            }
        });
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
        songWrapper = (RelativeLayout) findViewById(R.id.now_playing);
        nowPlayingEventListener = new NowPlayingEventListener(this, songWrapper);
        currParty.addChildEventListener(nowPlayingEventListener);
    }

    public void addSong(View v) {
        SearchController searchController = SearchController.getInstance();
        searchController.setSearchCallback(new SearchController.SearchCallback() {
            @Override
            public void onSongChosen(Song song) {
                QueuedSong queuedSong = (QueuedSong) song;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference("parties").child(partyId).child("queue")
                        .child(Long.toString(queuedSong.getTimestamp()));
                databaseReference.setValue(queuedSong);
                Toast toast = Toast.makeText(ActivityPartyAdmin.this, "Song added: " + queuedSong.getName(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        searchController.setInstructions("Tap a song to add it to the queue");
        Intent searchIntent = new Intent(this, ActivitySearch.class);
        startActivity(searchIntent);
    }

    public void options(View v) {
        Intent optionsIntent = new Intent(this, ActivityOptions.class);
        startActivity(optionsIntent);
    }

    public void play() {
        if (!nowPlayingEventListener.songPlaying()) {
            QueuedSong nextSong = getNextSong();
            if (nextSong != null) {
                // first get the song out of the queue
                Log.d(TAG, "Song: " + nextSong.getName());
                playAndUpdateDatabase(nextSong);

            } else {
                Song song = LocalPlaylistController.getInstance().pop();
                if (song != null) {
                    nextSong = new QueuedSong(song);
                    // if there's no song in the queue, check the background queue
                    Log.d(TAG, "Song: " + nextSong.getName());
                    playAndUpdateDatabase(nextSong);
                } else {
                    play.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.GONE);
                    Intent searchIntent = new Intent(this, ActivitySearch.class);
                    startActivity(searchIntent);
                }
            }
        } else {
            audioControllerInstance.resume();
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        }
    }

    public void playAndUpdateDatabase(QueuedSong nextSong) {
        audioControllerInstance.play(nextSong, new CustomAudioController.SongPlayingListener() {
            @Override
            public void onPlaying(QueuedSong song) {
                currParty.child(String.valueOf(song.getTimestamp())).removeValue();
                song.setTimestamp(Long.MAX_VALUE);
                currParty.child("current").setValue(song);
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }
        });
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
        QueuedSong nextSong = getNextSong();
        if (nextSong != null) {
            playAndUpdateDatabase(nextSong);
        } else {
            Song song = LocalPlaylistController.getInstance().pop();
            if (song != null) {
                nextSong = new QueuedSong(song);
                // if there's no song in the queue, check the background queue
                Log.d(TAG, "Song: " + nextSong.getName());
                playAndUpdateDatabase(nextSong);
            } else {
                Intent searchIntent = new Intent(this, ActivitySearch.class);
                startActivity(searchIntent);
            }
        }
    }

    private QueuedSong getNextSong() {
        return customQueueEventListener.getNextSong();
    }

    // Broadcast Receivers for notification actions
    private class NotificationPlay extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationController.togglePlayButton(NotificationController.ACTION_PLAY);
            play();
        }
    }

    private class NotificationPause extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationController.togglePlayButton(NotificationController.ACTION_PAUSE);
            pause();
        }
    }

    private class NotificationNext extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent closeDialogs = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(closeDialogs);
            next();
        }
    }
}
