package com.myousic.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;

import com.myousic.R;

public class ActivityOptions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        findViewById(R.id.party_name).setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setUnclickedColor(findViewById(R.id.party_name));
        setUnclickedColor(findViewById(R.id.playlist));
        setUnclickedColor(findViewById(R.id.blacklist));
        setUnclickedColor(findViewById(R.id.manage));
    }

    protected void setClickedColor(View v) {
        v.setBackgroundColor(0xFF005177);
    }

    protected void setUnclickedColor(View v) {
        v.setBackgroundColor(0xFF007399);
    }

    public void partyName(View v) {
        setClickedColor(v);
        Intent partyNameIntent = new Intent(this, ActivityPartyName.class);
        startActivity(partyNameIntent);
    }

    public void playlist(View v) {
        setClickedColor(v);
        Intent playlistIntent =  new Intent(this, ActivityBackgroundPlaylist.class);
        startActivity(playlistIntent);
    }

    public void blacklist(View v) {
        setClickedColor(v);
        Intent blacklistIntent =  new Intent(this, ActivityBlacklist.class);
        startActivity(blacklistIntent);
    }

    public void manage(View v) {
        setClickedColor(v);
        Intent managePartyIntent = new Intent(this, ActivityManageParty.class);
        startActivity(managePartyIntent);
    }
}
