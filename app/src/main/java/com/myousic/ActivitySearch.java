package com.myousic;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.SearchView;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myousic.R;
import com.myousic.models.QueuedSong;
import com.myousic.models.SearchResult;
import com.myousic.models.WebAPIWrapper;
import com.myousic.widgets.ResultRow;

import java.util.List;

public class ActivitySearch extends AppCompatActivity {
    private SearchView searchView;
    private TableLayout tableLayout;
    private final String ARTIST = "artist";
    private final String TRACK = "track";
    private final String ALBUM = "album";

    private static String TAG = "ActivitySearch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = (SearchView) findViewById(R.id.search);
        tableLayout = (TableLayout) findViewById(R.id.result_table);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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
            public void onResponse(List<SearchResult> searchResults) {
                for (SearchResult result : searchResults) {
                    ResultRow row = (ResultRow) LayoutInflater.from(ActivitySearch.this)
                            .inflate(R.layout.layout_search_row, null);
                    row.setSearchResult(result);
                    ((TextView)row.findViewById(R.id.result_value)).setText(result.getSong());
                    ((TextView)row.findViewById(R.id.result_type)).setText(result.getArtist());
                    tableLayout.addView(row);
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
            ResultRow resultRow = (ResultRow) v;
            QueuedSong queuedSong = new QueuedSong(resultRow.getSearchResult());
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference("parties/").child(partyID)
                    .child(Long.toString(queuedSong.getTimestamp()));
            databaseReference.setValue(queuedSong);
            Toast toast = Toast.makeText(this, "Song added: " + queuedSong.getName(), Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            Log.d(TAG, "Database error. Song not queued: " + e.toString());
        }
    }
}
