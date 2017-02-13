package com.myousic;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class ActivityPartyAdmin extends AppCompatActivity {
    private FirebaseDatabase db;
    private SharedPreferences loginPrefs;
    private String authToken;

    TextView idField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_admin);

        db = FirebaseDatabase.getInstance();
        loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        authToken = loginPrefs.getString("token", "");
        idSetup();
    }

    protected void idSetup() {
        Random rnd = new Random(System.currentTimeMillis());
        int x = rnd.nextInt(65536); //Between 0-255
        String id = Integer.toHexString(x);
        db.getReference().child("parties").child(id).setValue("");
        idField = (TextView)findViewById(R.id.party_id_field);
        idField.setText("Party ID: "+id);
    }
}
