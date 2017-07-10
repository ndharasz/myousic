package com.myousic.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myousic.R;
import com.myousic.util.BlacklistController;

public class ActivityBlacklist extends AppCompatActivity {
    BlacklistController blacklistController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        String partyID = getSharedPreferences("Party", Context.MODE_PRIVATE).getString("party_id", null);
        blacklistController = new BlacklistController(partyID);
    }

    protected void addSong(View v) {
        // Pop up dialog with search or new page.  Either or.
    }
}
