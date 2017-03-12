package com.myousic.widgets;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
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

/**
 * Created by brian on 3/11/17.
 */

public class WidgetInteractiveTable extends TableLayout {
    private static final String TAG = "WidgetInteractiveTable";

    public interface OnOrderAlteredListener {
        public void onOrderAltered(Song song, int oldPos, int newPos);
    }

    public interface OnSongDeletedListener {
        public void onSongDeleted(Song song, int pos);
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

            instance.getLargeAlbumCover(song.getUri(), new WebAPIWrapper.AlbumCoverListener() {
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
    }

    public WidgetInteractiveTable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnOrderAlteredListener(OnOrderAlteredListener onOrderAlteredListener) {
        this.onOrderAlteredListener = onOrderAlteredListener;
    }

    public void setOnSongDeletedListener(OnSongDeletedListener onSongDeletedListener) {
        this.onSongDeletedListener = onSongDeletedListener;
    }

    public void disableDragAndDrop() {
        songLongClickListener = null;
    }

    @Override
    public void addView(View v, int index) {
        if (!(v instanceof WidgetSongRow)) {
            Log.d(TAG, "Attempt was made to add something other than a WidgetSongRow to the table");
            return;
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
        if (!(v instanceof WidgetSongRow)) {
            Log.d(TAG, "Attempt was made to add something other than a WidgetSongRow to the table");
            return;
        }
        WidgetSongRow widgetSongRow = (WidgetSongRow) v;
        widgetSongRow.setIndex(getChildCount());
        super.addView(v);
        v.setOnClickListener(songClickListener);
        v.setOnLongClickListener(songLongClickListener);
        v.setOnDragListener(songDragListener);
    }

    @Override
    public void removeView(View v) {
        if (!(v instanceof WidgetSongRow)) {
            Log.d(TAG, "Attempt was made to add something other than a WidgetSongRow to the table");
            return;
        }
        super.removeView(v);
        for (int i = 0; i < getChildCount(); i++) {
            WidgetSongRow child = (WidgetSongRow) getChildAt(i);
            if (child != null)
                child.setIndex(i);
        }
    }
}
