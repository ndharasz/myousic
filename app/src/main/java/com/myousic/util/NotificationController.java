package com.myousic.util;

import android.R.drawable;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.myousic.R;
import com.myousic.activities.ActivityPartyAdmin;
import com.myousic.models.Song;

/**
 * Created by ndharasz on 7/13/2017.
 */

public class NotificationController {
    private static int NotificationID = 42069;
    private static NotificationCompat.Builder builder;
    private static RemoteViews notificitionView;
    private static NotificationManager mNotificationManager;
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    private static boolean isInitialized = false;

    public static void notify(Context context, Song song) {

        if(!isInitialized) {
            init(context);
        }
        notificitionView.setTextViewText(R.id.notification_title, song.getName());
        notificitionView.setTextViewText(R.id.notification_artist, song.getArtist());

        mNotificationManager.notify(NotificationID, builder.build());
    }

    public static void togglePlayButton(String action) {
        if(isInitialized) {
            if (action.equals(ACTION_PLAY)) {
                builder.setSmallIcon(drawable.ic_media_play);
                notificitionView.setViewVisibility(R.id.notification_play, View.GONE);
                notificitionView.setViewVisibility(R.id.notification_pause, View.VISIBLE);
            } else {
                builder.setSmallIcon(drawable.ic_media_pause);
                notificitionView.setViewVisibility(R.id.notification_play, View.VISIBLE);
                notificitionView.setViewVisibility(R.id.notification_pause, View.GONE);
            }
            mNotificationManager.notify(NotificationID, builder.build());
        }
    }

    public static void init(Context context) {
        Intent goToInent = new Intent(context, ActivityPartyAdmin.class);
        Intent playIntent = new Intent(ACTION_PLAY);
        Intent pauseIntent = new Intent(ACTION_PAUSE);
        Intent nextIntent = new Intent(ACTION_NEXT);

        PendingIntent goToPendingIntent = PendingIntent.getActivity(context, 1, goToInent, 0);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 1, playIntent, 0);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 1, pauseIntent, 0);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 1, nextIntent, 0);

        notificitionView = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
        notificitionView.setOnClickPendingIntent(R.id.notification_text, goToPendingIntent);
        notificitionView.setOnClickPendingIntent(R.id.notification_play, playPendingIntent);
        notificitionView.setOnClickPendingIntent(R.id.notification_pause, pausePendingIntent);
        notificitionView.setOnClickPendingIntent(R.id.notification_next, nextPendingIntent);

        builder = new NotificationCompat.Builder(context)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(drawable.ic_media_play)
                .setCustomBigContentView(notificitionView)
                .setOngoing(true);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        isInitialized = true;
    }
}
