package com.myousic.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myousic.R;
import com.myousic.models.Song;
import com.myousic.util.BlacklistController;
import com.myousic.util.CustomQueueEventListener;
import com.myousic.util.SearchController;
import com.myousic.widgets.WidgetInteractiveTable;

public class ActivityBlacklist extends AppCompatActivity {
    private BlacklistController blacklistController;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        String partyID = getSharedPreferences("Party", Context.MODE_PRIVATE).getString("party_id", null);
        blacklistController = new BlacklistController(partyID);

        databaseReference = FirebaseDatabase.getInstance().getReference("parties").child(partyID).child("blacklist");
        WidgetInteractiveTable table = (WidgetInteractiveTable) findViewById(R.id.blacklist_table);
        table.disableDragAndDrop();
        table.setOnSongDeletedListener(new WidgetInteractiveTable.OnSongDeletedListener() {
            @Override
            public void onSongDeleted(Song song, int pos) {
                blacklistController.remove(song);
            }
        });
        CustomQueueEventListener customQueueEventListener = new CustomQueueEventListener(this, table);
        databaseReference.addChildEventListener(customQueueEventListener);
    }

    protected void addSong(View v) {
        SearchController searchController = SearchController.getInstance();
        searchController.setSearchCallback(new SearchController.SearchCallback() {
            @Override
            public void onSongChosen(Song song) {
                blacklistController.add(song);
                Toast toast = Toast.makeText(ActivityBlacklist.this, "Song added: " + song.getName(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        searchController.setInstructions("Tap a song to restrict it from your party");
        Intent searchIntent = new Intent(this, ActivitySearch.class);
        startActivity(searchIntent);
    }
}
