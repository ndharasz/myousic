package com.myousic.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableRow;

import com.google.firebase.database.FirebaseDatabase;
import com.myousic.R;
import com.myousic.util.NotificationController;

public class ActivityOptions extends AppCompatActivity {
    private static final String TAG = "ActivityOptions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        findViewById(R.id.party_name).setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setUnclickedColor(findViewById(R.id.party_name));
        setUnclickedColor(findViewById(R.id.playlist));
        setUnclickedColor(findViewById(R.id.blacklist));
        setUnclickedColor(findViewById(R.id.manage));
    }

    protected void setClickedColor(View v) {
        v.setBackgroundColor(0xFF005177);
    }

    protected void setUnclickedColor(View v) {
        v.setBackgroundColor(0xFF007399);
    }

    public void partyName(View v) {
        setClickedColor(v);
        Intent partyNameIntent = new Intent(this, ActivityPartyName.class);
        startActivity(partyNameIntent);
    }

    public void playlist(View v) {
        setClickedColor(v);
        Intent playlistIntent =  new Intent(this, ActivityBackgroundPlaylist.class);
        startActivity(playlistIntent);
    }

    public void blacklist(View v) {
        setClickedColor(v);
        Intent blacklistIntent =  new Intent(this, ActivityBlacklist.class);
        startActivity(blacklistIntent);
    }

    public void deleteQueue(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.thinDialog);
        alertDialogBuilder.setTitle("Delete party queue?");
        alertDialogBuilder.setMessage("Cannot be undone");
        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Deleting queue");
                String partyId = getSharedPreferences("Party", MODE_PRIVATE).getString("party_id", "");
                FirebaseDatabase.getInstance().getReference().child("parties").child(partyId).child("queue").removeValue();
                NotificationController.destroy();
            }
        });
        alertDialogBuilder.show();
    }
}
