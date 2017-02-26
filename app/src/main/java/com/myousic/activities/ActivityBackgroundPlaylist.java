package com.myousic.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.myousic.R;
import com.myousic.models.Playlist;
import com.myousic.models.WebAPIWrapper;
import com.myousic.widgets.WidgetPlaylistRow;

import java.util.List;

public class ActivityBackgroundPlaylist extends AppCompatActivity {
    private static final String TAG = "ActivityBackgroundPlaylist";
    WebAPIWrapper instance;
    String spotifyUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_playlist);
        instance = WebAPIWrapper.getInstance(this);
        spotifyUID = getString(R.string.clientID);
    }

    protected void importPlaylist(View v) {
        final View playlistsView = LayoutInflater.from(this).inflate(R.layout.layout_playlist_table, null);
        final TableLayout tableLayout = (TableLayout) playlistsView.findViewById(R.id.playlist_table);
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null) {
            return;
        }
        instance.getPlaylists(token, spotifyUID, new WebAPIWrapper.GetPlaylistListener() {
            @Override
            public void onResponse(List<Playlist> playlists) {
                for (Playlist playlist : playlists) {
                    WidgetPlaylistRow widgetPlaylistRow = (WidgetPlaylistRow) LayoutInflater.from(playlistsView.getContext())
                            .inflate(R.layout.layout_playlist_row, null);
                    widgetPlaylistRow.setPlaylist(playlist);
                    ((TextView) widgetPlaylistRow.findViewById(R.id.playlist_name)).setText(playlist.getName());
                    tableLayout.addView(widgetPlaylistRow);
                }
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light);
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
}
