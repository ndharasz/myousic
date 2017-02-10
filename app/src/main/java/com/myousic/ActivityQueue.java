package com.myousic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myousic.R;

public class ActivityQueue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
    }

    protected void addSong(View v) {
        Intent searchIntent = new Intent(this, ActivitySearch.class);
        startActivity(searchIntent);
    }
}
