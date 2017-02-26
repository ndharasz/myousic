package com.myousic.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableRow;

import com.myousic.models.Playlist;

/**
 * Created by brian on 2/25/17.
 */

public class WidgetPlaylistRow extends TableRow {
    private Playlist playlist;

    public WidgetPlaylistRow(Context context) {
        super(context);
    }

    public WidgetPlaylistRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
}
