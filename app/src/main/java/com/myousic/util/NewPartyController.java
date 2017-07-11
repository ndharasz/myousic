package com.myousic.util;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myousic.R;

/**
 * Created by brian on 7/11/17.
 */

public class NewPartyController {
    private static final String TAG = "NewPartyController";
    private DatabaseReference dref;
    private String username;

    public interface CanCreatePartyCallback {
        public void canCreateCallback(boolean canCreate);
    }

    public NewPartyController(String username) {
        dref = FirebaseDatabase.getInstance().getReference().child("parties");
        this.username = username;
    }

    public void checkIfCanCreate(final String partyName, final CanCreatePartyCallback canCreatePartyCallback) {
        if (partyName == null) {
            canCreatePartyCallback.canCreateCallback(false);
            return;
        }
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(partyName).exists()) {
                    // TODO: check if it was previously owned by this user
                    boolean isOwned = true;
                    try {
                        isOwned = dataSnapshot.child(partyName).child("owner").getValue().equals(username);
                    } catch (Exception e) {
                        Log.d(TAG, "Exception thrown trying to get the owner of partyName: " + partyName);
                        Log.d(TAG, e.getMessage());
                    }
                    if (!isOwned) {
                        canCreatePartyCallback.canCreateCallback(false);
                        return;
                    }
                }
                canCreatePartyCallback.canCreateCallback(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void create(Context context, String id) {
        dref.child(id).child("owner").setValue(username);
        context.getSharedPreferences("Party", Context.MODE_PRIVATE)
                .edit().putString("party_id", id).commit();
    }
}
