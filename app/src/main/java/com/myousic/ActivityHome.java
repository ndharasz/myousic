package com.myousic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myousic.R;
import com.spotify.sdk.android.authentication.LoginActivity;

public class ActivityHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    protected void create(View v) {
        Intent createPartyIntent = new Intent(this, LoginActivity.class);
        startActivity(createPartyIntent);
    }

    protected void join(View v) {
        Intent joinPartyIntent = new Intent(this, ActivityJoinParty.class);
        startActivity(joinPartyIntent);
    }
}
