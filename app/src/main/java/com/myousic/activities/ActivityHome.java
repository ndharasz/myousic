package com.myousic.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.myousic.R;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;

public class ActivityHome extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //init shared preferences file/editor for login info
    }

    protected void join(View v) {
        Intent joinPartyIntent = new Intent(this, ActivityJoinParty.class);
        startActivity(joinPartyIntent);
    }

    protected void create(View v) {
        Intent partyAdminIntent = new Intent(this, ActivityPartyAdmin.class);
        startActivity(partyAdminIntent);
    }
}
