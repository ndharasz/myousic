package com.myousic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myousic.R;

public class ActivityJoinParty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_party);
    }

    protected void join(View v) {
        Intent queueIntent = new Intent(this, ActivityQueue.class);
        startActivity(queueIntent);
    }
}
