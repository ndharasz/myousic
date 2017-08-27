package com.myousic.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.myousic.R;
import com.myousic.util.WebAPIWrapper;
import com.myousic.util.NewPartyController;

public class ActivityHome extends AppCompatActivity {
    NewPartyController newPartyController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // init controller
        String username = getSharedPreferences("loginPrefs", MODE_PRIVATE).getString("username", "");
        newPartyController = new NewPartyController(username);
    }

    public void join(View v) {
        Intent joinPartyIntent = new Intent(this, ActivityJoinParty.class);
        startActivity(joinPartyIntent);
    }

    /*
     * This method is called by the big "Create" button on the home activity
     */
    public void create(View v) {
        SharedPreferences loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if(!loginPrefs.getBoolean("premium", false)) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.thinDialog);
            alertDialogBuilder.setTitle("ERROR");
            alertDialogBuilder.setMessage("You need a premium account in order to create parties.");
            alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                // When you click create...
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.show();
        } else {
            // This seems complicated but it's really just a lot of functional programming..

            // Inflate the layout
            final View partyNameLayout = LayoutInflater.from(this).inflate(R.layout.layout_party_name, null);
            // partyName is the text box which the user types the name into
            final EditText partyName = (EditText) partyNameLayout.findViewById(R.id.party_name);

            // Build the dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.thinDialog);
            alertDialogBuilder.setView(partyNameLayout);
            alertDialogBuilder.setTitle("Enter a party name");
            alertDialogBuilder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                // When you click create...
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Check if this is a valid party name (See if the name is already taken.  If it does and you don't own the party, you are denied creation access.)
                    newPartyController.checkIfCanCreate(partyName.getText().toString(), new NewPartyController.CanCreatePartyCallback() {
                        @Override
                        public void canCreateCallback(boolean canCreate) {
                            if (canCreate) {
                                // You are given access
                                // Save the String as lowercase
                                newPartyController.create(ActivityHome.this, partyName.getText().toString().toLowerCase());
                                Intent partyAdminIntent = new Intent(ActivityHome.this, ActivityPartyAdmin.class);
                                startActivity(partyAdminIntent);
                            } else {
                                // You are denied access
                                Toast.makeText(ActivityHome.this, "This party is name is taken by someone else", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            alertDialogBuilder.show();
        }
    }
}
