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
    }

    @Override
    protected void onStart() {
        super.onStart();

        setUnclickedColor(findViewById(R.id.party_name));
        setUnclickedColor(findViewById(R.id.playlist));
        setUnclickedColor(findViewById(R.id.blacklist));
    }

    protected void setClickedColor(View v) {
        v.setBackgroundColor(0xFF005177);
    }

    protected void setUnclickedColor(View v) {
        v.setBackgroundColor(0xFF007399);
    }

    protected void partyName(View v) {
        setClickedColor(v);
    }

    protected void playlist(View v) {
        setClickedColor(v);
        Intent playlistIntent =  new Intent(this, ActivityBackgroundPlaylist.class);
        startActivity(playlistIntent);
    }

    protected void blacklist(View v) {
        setClickedColor(v);
    }
}
