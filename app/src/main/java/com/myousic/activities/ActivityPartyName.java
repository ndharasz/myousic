package com.myousic.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.myousic.R;

public class ActivityPartyName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_name);
    }

    protected void changePartyID(View v) {
        String newName = ((EditText) findViewById(R.id.new_id_field)).getText().toString();
        getSharedPreferences("Party", Context.MODE_PRIVATE)
                .edit().putString("party_id", newName).commit();

        this.finish();
    }
}
