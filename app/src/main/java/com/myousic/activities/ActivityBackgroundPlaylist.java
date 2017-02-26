package com.myousic.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myousic.R;
import com.myousic.models.Playlist;
import com.myousic.models.Song;
import com.myousic.models.WebAPIWrapper;
import com.myousic.widgets.WidgetPlaylistRow;
import com.myousic.widgets.WidgetSongRow;

import java.util.List;

public class ActivityBackgroundPlaylist extends AppCompatActivity {
    private static final String TAG = "ActivityBackgroundPlaylist";
    WebAPIWrapper instance;
    TableLayout backgroundSongTable;

    DatabaseReference ref;
    String partyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_playlist);
        instance = WebAPIWrapper.getInstance(this);
        backgroundSongTable = (TableLayout) findViewById(R.id.background_table);

        partyID = getSharedPreferences("Party", Context.MODE_PRIVATE).getString("party_id", "xxxx");
        if (partyID.equals("xxxx")) {
            throw new Error("Party ID lost");
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("parties");
    }

    protected void importPlaylist(View v) {
        final View playlistsView = LayoutInflater.from(this).inflate(R.layout.layout_playlist_table, null);
        final TableLayout tableLayout = (TableLayout) playlistsView.findViewById(R.id.playlist_table);
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        final String token = prefs.getString("token", null);
        if (token == null) {
            return;
        }
        instance.getPlaylists(token, new WebAPIWrapper.GetPlaylistListener() {
            @Override
            public void onResponse(List<Playlist> playlists) {
                for (Playlist playlist : playlists) {
                    final WidgetPlaylistRow widgetPlaylistRow = (WidgetPlaylistRow) LayoutInflater.from(playlistsView.getContext())
                            .inflate(R.layout.layout_playlist_row, null);
                    widgetPlaylistRow.setPlaylist(playlist);
                    ((TextView) widgetPlaylistRow.findViewById(R.id.playlist_name)).setText(playlist.getName());
                    widgetPlaylistRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addSongs(token, widgetPlaylistRow.getPlaylist());
                        }
                    });
                    tableLayout.addView(widgetPlaylistRow);
                }
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Dialog);
        alertDialogBuilder.setView(playlistsView);
        alertDialogBuilder.setTitle("Playlists");
        alertDialogBuilder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.show();
    }

    protected void addSong(View v) {

    }

    private void addSongs(String token, Playlist playlist) {
        instance.getSongsFromPlaylist(token, playlist, new WebAPIWrapper.SearchResultResponseListener() {
            @Override
            public void onResponse(List<Song> songs) {
                for (Song song : songs) {
                    WidgetSongRow songRow = (WidgetSongRow) LayoutInflater.from(backgroundSongTable.getContext())
                            .inflate(R.layout.layout_song_row, null);
                    ((TextView) songRow.findViewById(R.id.song_name)).setText(song.getName());
                    ((TextView) songRow.findViewById(R.id.song_artist)).setText(song.getArtist());
                    songRow.findViewById(R.id.song_image).setVisibility(View.GONE);
                    songRow.setSong(song);
                    backgroundSongTable.addView(songRow);

                    ref.child(partyID).child("backgound").child(song.getUri()).setValue(song);
                }
            }
        });
    }
}
