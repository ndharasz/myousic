package com.myousic.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.SearchView;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.myousic.R;
import com.myousic.models.QueuedSong;
import com.myousic.models.Song;
import com.myousic.util.WebAPIWrapper;
import com.myousic.util.SearchController;
import com.myousic.widgets.WidgetSongRow;

import java.util.List;

public class ActivitySearch extends AppCompatActivity {
    private static final String TAG = "ActivitySearch";

    private SearchView searchView;
    private TableLayout tableLayout;
    private final String ARTIST = "artist";
    private final String TRACK = "track";
    private final String ALBUM = "album";

    private SearchController searchControllerInstance;

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
                searchView.clearFocus();
                search(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });

        searchControllerInstance = SearchController.getInstance();
        ((TextView) findViewById(R.id.instructions)).setText(searchControllerInstance.getInstuctions());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchControllerInstance.resetCallback();
    }

    protected void search(String query) {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        final String token = prefs.getString("token", null);
        tableLayout.removeAllViews();
        WebAPIWrapper webAPIWrapper = WebAPIWrapper.getInstance(this);
        webAPIWrapper.search(token, query, TRACK, new WebAPIWrapper.SearchResultResponseListener() {
            @Override
            public void onResponse(List<Song> songs) {
                for (Song result : songs) {
                    WidgetSongRow songRow = (WidgetSongRow) LayoutInflater.from(ActivitySearch.this)
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

    public void addSong(View v) {
        String partyID = getSharedPreferences("Party", Context.MODE_PRIVATE).getString("party_id", null);
        if (partyID == null) {
            Log.d(TAG, "Preferences not set");
            return;
        } try {
            WidgetSongRow widgetSongRow = (WidgetSongRow) v;
            QueuedSong queuedSong = new QueuedSong(widgetSongRow.getSong());
            searchControllerInstance.songChosen(queuedSong);

            /* copy this to the main activity
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference("parties").child(partyID).child("queue")
                    .child(Long.toString(queuedSong.getTimestamp()));
            databaseReference.setValue(queuedSong);
            Toast toast = Toast.makeText(this, "Song added: " + queuedSong.getName(), Toast.LENGTH_SHORT);
            toast.show();
            */
        } catch (Exception e) {
            Log.d(TAG, "Database error. Song not queued: " + e.toString());
        }
    }
}
