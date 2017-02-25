package com.myousic.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableRow;

import com.myousic.models.Song;

/**
 * Created by brian on 2/12/17.
 *
 * I extended this class so that it could hold a Song object.
 * This way when it's clicked on we can easily get the information from the view
 */

public class WidgetSongRow extends TableRow {
    private Song song;

    public WidgetSongRow(Context context) {
        super(context);
    }

    public WidgetSongRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Song getSong() {
        return song;
    }
}