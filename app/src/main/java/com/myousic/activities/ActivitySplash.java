package com.myousic.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.myousic.R;
import com.myousic.models.WebAPIWrapper;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;

public class ActivitySplash extends AppCompatActivity {
    private SharedPreferences loginPrefs;
    private SharedPreferences.Editor loginEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSharedPreferences("loginPrefs", Context.MODE_PRIVATE).getString("loggedIn", "false");
        loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginEdit = loginPrefs.edit();
    }

    protected void create(View v) {
        //Create Spotify authentication request
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(
                getString(R.string.clientID), AuthenticationResponse.Type.TOKEN,
                "myousic://callback");
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        //open Spotify login activity
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //if successful
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                case TOKEN:
                    //if authentication token is in response, put it in the shared
                    //preference file and start the next activity
                    String token = response.getAccessToken();
                    loginEdit.putString("token", token);
                    loginEdit.commit();
                    WebAPIWrapper instance = WebAPIWrapper.getInstance(ActivitySplash.this);
                    instance.getUsername(token, new WebAPIWrapper.GetUsernameListener() {
                        @Override
                        public void onResponse(String username) {
                            loginEdit.putString("username", username).commit();
                        }
                    });
                    Intent activityHomeIntent = new Intent(this, ActivityHome.class);
                    startActivity(activityHomeIntent);
                    break;
                case ERROR:
                    //else let the user know it couldnt authenticate
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
