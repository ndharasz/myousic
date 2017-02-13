package com.myousic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.LoginActivity;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;

public class ActivityHome extends AppCompatActivity {
    private SharedPreferences loginPrefs;
    private SharedPreferences.Editor loginEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginEdit = loginPrefs.edit();
    }

    protected void create(View v) {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(
                "dd2b9841d81d4b95938909baf24604f6", AuthenticationResponse.Type.TOKEN,
                "myousic://callback");
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    protected void join(View v) {
        Intent joinPartyIntent = new Intent(this, ActivityJoinParty.class);
        startActivity(joinPartyIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                case TOKEN:
                    String token = response.getAccessToken();
                    loginEdit.putString("token", token);
                    loginEdit.commit();
                    Intent partyAdminIntent = new Intent(this, ActivityPartyAdmin.class);
                    startActivity(partyAdminIntent);
                    break;
                case ERROR:
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
