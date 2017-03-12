package com.myousic.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myousic.R;
import com.myousic.models.QueuedSong;
import com.myousic.models.Song;
import com.myousic.models.WebAPIWrapper;
import com.myousic.util.LocalPlaylistController;
import com.myousic.widgets.WidgetSongRow;

import java.util.List;

public class ActivityBackgroundPlaylistSearch extends AppCompatActivity {
    private SearchView searchView;
    private TableLayout tableLayout;
    private final String ARTIST = "artist";
    private final String TRACK = "track";
    private final String ALBUM = "album";

    private static final String TAG = "ActivitySearch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tableLayout = (TableLayout) findViewById(R.id.result_table);

        searchView = (SearchView) findViewById(R.id.search);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });
    }

    protected void search(String query) {
        tableLayout.removeAllViews();
        WebAPIWrapper webAPIWrapper = WebAPIWrapper.getInstance(this);
        webAPIWrapper.search(query, TRACK, new WebAPIWrapper.SearchResultResponseListener() {
            @Override
            public void onResponse(List<Song> songs) {
                for (Song result : songs) {
                    WidgetSongRow songRow = (WidgetSongRow) LayoutInflater.from(ActivityBackgroundPlaylistSearch.this)
                            .inflate(R.layout.layout_song_row, null);
                    songRow.setSong(result);
                    songRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addSong(v);
                        }
                    });
                    ((TextView)songRow.findViewById(R.id.song_name)).setText(result.getName());
                    ((TextView)songRow.findViewById(R.id.song_artist)).setText(result.getArtist());
                    songRow.findViewById(R.id.song_image).setVisibility(View.GONE);
                    tableLayout.addView(songRow);
                }
            }
        });
    }

    protected void addSong(View v) {
        String partyID = getSharedPreferences("Party", Context.MODE_PRIVATE).getString("party_id", null);
        if (partyID == null) {
            Log.d(TAG, "Preferences not set");
            return;
        } try {
            WidgetSongRow widgetSongRow = (WidgetSongRow) v;
            Song song = widgetSongRow.getSong();
            LocalPlaylistController localPlaylistController = LocalPlaylistController.getInstance();
            localPlaylistController.push(song);
            Toast toast = Toast.makeText(this, "Song added: " + song.getName(), Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            Log.d(TAG, "Database error. Song not queued: " + e.toString());
        }
    }
}
