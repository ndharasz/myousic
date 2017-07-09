package com.myousic.widgets;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.myousic.R;
import com.myousic.activities.ActivityBackgroundPlaylist;
import com.myousic.models.Song;
import com.myousic.models.WebAPIWrapper;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by brian on 3/11/17.
 */

public class WidgetInteractiveTable extends TableLayout {
    private static final String TAG = "WidgetInteractiveTable";
    private boolean empty = true;

    public interface OnOrderAlteredListener {
        public void onOrderAltered(Song song, int oldPos, int newPos);
    }

    public interface OnSongDeletedListener {
        public void onSongDeleted(Song song, int pos);
    }

    public interface TableEmptyListener {
        public void OnTableEmptied();
    }

    public interface TableOccupiedListener {
        public void OnTableOccupied();
    }

    private OnOrderAlteredListener onOrderAlteredListener = new OnOrderAlteredListener() {
        @Override
        public void onOrderAltered(Song song, int oldPos, int newPos) {
            return;
        }
    };

    private OnSongDeletedListener onSongDeletedListener = new OnSongDeletedListener() {
        @Override
        public void onSongDeleted(Song song, int pos) {
            return;
        }
    };

    private TableEmptyListener tableEmptyListener = new TableEmptyListener() {
        @Override
        public void OnTableEmptied() {
            return;
        }
    };

    private TableOccupiedListener tableOccupiedListener = new TableOccupiedListener() {
        @Override
        public void OnTableOccupied() {
            return;
        }
    };

    private View.OnClickListener songClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final WidgetSongRow widgetSongRow = (WidgetSongRow) v;
            Song song = widgetSongRow.getSong();

            final View dialogView = LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_song_info, null);
            ((TextView) dialogView.findViewById(R.id.song_name)).setText(song.getName());
            ((TextView) dialogView.findViewById(R.id.song_artist)).setText(song.getArtist());
            WebAPIWrapper instance = WebAPIWrapper.getInstance(getContext());
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                    getContext(), android.R.style.Theme_Holo_Dialog);
            dialogBuilder.setTitle("Song Information");
            dialogBuilder.setView(dialogView);

            dialogBuilder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onSongDeletedListener.onSongDeleted(widgetSongRow.getSong(), widgetSongRow.getIndex());
                    removeView(widgetSongRow);
                }
            });
            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            final AlertDialog alertDialog = dialogBuilder.create();

            Context context = getContext();

            SharedPreferences prefs = context.getSharedPreferences("loginPrefs", MODE_PRIVATE);
            final String token = prefs.getString("token", null);

            instance.getLargeAlbumCover(token, song.getUri(), new WebAPIWrapper.AlbumCoverListener() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    ((ImageView) dialogView.findViewById(R.id.album_art)).setImageBitmap(bitmap);
                    alertDialog.show();
                }
            });
        }
    };

    private View.OnLongClickListener songLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Log.d(TAG, "Start drag");
            ClipData data = ClipData.newPlainText("", "");
            DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            return false;
        }
    };

    private View.OnDragListener songDragListener = new OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                return true;
            } else if (event.getAction() == DragEvent.ACTION_DROP) {
                WidgetSongRow original = (WidgetSongRow) event.getLocalState();
                WidgetSongRow cur = (WidgetSongRow) v;
                onOrderAlteredListener.onOrderAltered(original.getSong(), original.getIndex(), cur.getIndex());
                removeView(original);
                int curIndex = cur.getIndex();
                addView(original, curIndex);
                return true;
            }
            return false;
        }
    };

    public WidgetInteractiveTable(Context context) {
        super(context);
        displayEmptyMessage();
    }

    public WidgetInteractiveTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        displayEmptyMessage();
    }

    public void setOnOrderAlteredListener(OnOrderAlteredListener onOrderAlteredListener) {
        this.onOrderAlteredListener = onOrderAlteredListener;
    }

    public void setOnSongDeletedListener(OnSongDeletedListener onSongDeletedListener) {
        this.onSongDeletedListener = onSongDeletedListener;
    }

    public void setTableEmptyListener(TableEmptyListener tableEmptyListener) {
        this.tableEmptyListener = tableEmptyListener;
    }

    public void setTableOccupiedListener(TableOccupiedListener tableOccupiedListener) {
        this.tableOccupiedListener = tableOccupiedListener;
    }

    public void disableDragAndDrop() {
        songLongClickListener = null;
    }

    @Override
    public void addView(View v, int index) {
        if (v instanceof TextView) {
            super.addView(v, index);
            return;
        }
        if (!(v instanceof WidgetSongRow)) {
            Log.d(TAG, "Attempt was made to add something other than a WidgetSongRow to the table");
            return;
        }
        if (empty) {
            removeEmptyMessage();
            empty = false;
        }
        WidgetSongRow widgetSongRow = (WidgetSongRow) v;
        widgetSongRow.setIndex(index);
        super.addView(v, index);
        for (int i = 0; i < getChildCount(); i++) {
            WidgetSongRow child = (WidgetSongRow) getChildAt(i);
            if (child != null)
                child.setIndex(i);
        }
    }

    @Override
    public void addView(View v) {
        if (v instanceof WidgetSongRow) {
            if (empty) {
                removeEmptyMessage();
                empty = false;
            }
            WidgetSongRow widgetSongRow = (WidgetSongRow) v;
            widgetSongRow.setIndex(getChildCount());
            super.addView(v);
            v.setOnClickListener(songClickListener);
            v.setOnLongClickListener(songLongClickListener);
            v.setOnDragListener(songDragListener);
        } else if (v instanceof TextView) {
            super.addView(v);
        } else {
            Log.d(TAG, "Attempt was made to add something other than a WidgetSongRow to the table");
            return;
        }
    }

    @Override
    public void removeView(View v) {
        if (!(v instanceof WidgetSongRow)) {
            Log.d(TAG, "Attempt was made to remove something other than a WidgetSongRow to the table");
            return;
        }
        super.removeView(v);
        if (getChildCount() == 0) {
            displayEmptyMessage();
        } else {
            for (int i = 0; i < getChildCount(); i++) {
                WidgetSongRow child = (WidgetSongRow) getChildAt(i);
                if (child != null)
                    child.setIndex(i);
            }
        }
    }

    private void displayEmptyMessage() {
        tableEmptyListener.OnTableEmptied();
    }

    private void removeEmptyMessage() {
        tableOccupiedListener.OnTableOccupied();
    }
}
