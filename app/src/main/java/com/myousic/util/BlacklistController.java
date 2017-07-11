package com.myousic.util;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myousic.models.Song;

/**
 * Created by brian on 7/9/17.
 *
 * This class contains the logic for blacklisting.
 *
 * Basically, you make a call to the add method to blacklist a song.
 * Then, you make a call to the isBlacklisted method to see if a song
 *  can be added.  The checking is done client-side because it's really
 *  not a big deal if someone gets around this check.
 *
 */

public class BlacklistController {
    public interface BlacklistedCallback {
        public void onBlacklistedCallback(boolean isBlacklisted);
    }

    DatabaseReference dref;
    public BlacklistController(String partyid) {
        dref = FirebaseDatabase.getInstance().getReference().child("parties").child(partyid).child("blacklist");
    }

    public void add(Song song) {
        dref.child("" + song.hashCode()).setValue(song);
    }

    public void remove(Song song) {
        dref.child("" + song.hashCode()).removeValue();
    }

    public boolean isBlacklisted(final Song song, final BlacklistedCallback blacklistedCallback) {
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("" + song.hashCode()).exists()) {
                    blacklistedCallback.onBlacklistedCallback(true);
                } else {
                    blacklistedCallback.onBlacklistedCallback(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return false;
    }
}
