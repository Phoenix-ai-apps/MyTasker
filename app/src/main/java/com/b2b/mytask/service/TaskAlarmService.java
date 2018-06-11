package com.b2b.mytask.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.b2b.mytask.ui.AddTaskActivity;
import com.b2b.mytask.R;

public class TaskAlarmService extends IntentService {
    private static int NOTIFICATION_ID = 1;
    Notification notification;
    Context context;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;

    public TaskAlarmService() {
        super("TaskAlarmService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        context = getApplicationContext();

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(context, AddTaskActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 123456, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        notification = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("ticker value")
                .setAutoCancel(true)
                .setPriority(8)
                .setContentTitle("Notify title")
                .setContentText("Text").build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        notification.ledARGB = 0xFFFFA500;
        notification.ledOnMS = 800;
        notification.ledOffMS = 1000;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
        Log.i("notif", "Notifications sent.");


    }
}
