package com.myousic.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myousic.R;
import com.myousic.util.CustomAudioController;
import com.myousic.util.LocalPlaylistController;

public class ActivityManageParty extends AppCompatActivity {
    public static final String TAG = "ActivityManageParty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_party);
    }

    public void deleteBackgroundPlaylist(View v) {
        // Show dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.thinDialog);
        alertDialogBuilder.setTitle("Delete background playlist?");
        alertDialogBuilder.setMessage("Cannot be undone");
        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Deleting background playlist");
                LocalPlaylistController localPlaylistController = LocalPlaylistController.getInstance();
                localPlaylistController.destroy();
            }
        });
        alertDialogBuilder.show();
    }

    public void deleteQueue(View v) {
        // Show dialog
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
            }
        });
        alertDialogBuilder.show();
    }
}
