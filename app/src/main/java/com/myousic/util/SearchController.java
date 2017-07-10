package com.myousic.util;

import com.myousic.models.Song;

/**
 * Created by brian on 7/10/17.
 *
 * This is a singleton that controls the action taken by a search activity
 *   when a song is chosen.  Activities which call a search activity are in charge
 *   of setting this action.
 */

public class SearchController {
    public interface SearchCallback {
        public void onSongChosen(Song song);
    }

    private class NoCallbackSpecifiedException extends RuntimeException {
        public NoCallbackSpecifiedException(String msg) {
            super(msg);
        }
    }

    public static final String TAG = "SearchController";
    public static SearchController instance;

    private SearchController() {
        resetCallback();
    }

    public static SearchController getInstance() {
        if (instance == null) {
            instance = new SearchController();
        }
        return instance;
    }

    private SearchCallback searchCallback;

    /*
     * This is called by the main activity
     */
    public void setSearchCallback(SearchCallback searchCallback) {
        this.searchCallback = searchCallback;
    }

    /*
     * Search activity calls this so that we can easily find problems
     */
    public void resetCallback() {
        searchCallback = new SearchCallback() {
            @Override
            public void onSongChosen(Song song) {
                throw new NoCallbackSpecifiedException("Callback must be specified before search activity is called.");
            }
        };
    }

    /*
     * This is called by the search activity
     */
    public void songChosen(Song song) {
        searchCallback.onSongChosen(song);
    }
}
