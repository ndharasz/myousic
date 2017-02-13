package com.myousic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.myousic.R;

public class ActivityJoinParty extends AppCompatActivity {
    private EditText partyIDField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_party);
        partyIDField = (EditText) findViewById(R.id.party_id);
    }

    protected void join(View v) {
        SharedPreferences.Editor editor =  getSharedPreferences("Party", Context.MODE_PRIVATE).edit();
        editor.putString("party_id", partyIDField.getText().toString());
        editor.commit();
        Intent queueIntent = new Intent(this, ActivityQueue.class);
        startActivity(queueIntent);
    }
}
