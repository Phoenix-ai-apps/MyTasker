package com.b2b.mytask.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.b2b.mytask.AppExecutors;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.R;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.db.entities.TaskDetailsEntity;
import com.b2b.mytask.models.AddTaskDetails;
import com.b2b.mytask.ui.AddTaskActivity;
import com.b2b.mytask.utils.ApplicationUtils;

public class TaskAlarmNotificationReceiver_V1 extends BroadcastReceiver implements AllConstants {
    Context context;
    private NotificationManager notifManager;
    private PendingIntent pendingIntent;
    private FolderEntity folderEntity;
    private String nextDate = "";
    private int notificationid;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle != null) {

            notificationid = bundle.getInt("notificationid");

            TaskDetailsEntity taskDetailsEntity = (TaskDetailsEntity) bundle.getParcelable("data");
            if (taskDetailsEntity != null) {
                createNotification(notificationid, taskDetailsEntity);
            }
        }
    }

    public void createNotification(int notificationid, TaskDetailsEntity taskDetailsEntity) {

        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        folderEntity = new FolderEntity();
        notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (notifManager == null) {
            notifManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, AddTaskActivity.class);
            intent.setAction(String.valueOf(taskDetailsEntity.getAllfolderid()));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, notificationid, intent, 0);

            RemoteViews mContentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
            if (Integer.parseInt(taskDetailsEntity.getTaskPriority()) == PRIORITY_TYPE_LOW) {
                mContentView.setImageViewResource(R.id.img_noti_icon, R.drawable.ic_notifications_yellow);
            } else if (Integer.parseInt(taskDetailsEntity.getTaskPriority()) == PRIORITY_TYPE_MEDIUM) {
                mContentView.setImageViewResource(R.id.img_noti_icon, R.drawable.ic_notifications_blue);
            } else if (Integer.parseInt(taskDetailsEntity.getTaskPriority()) == PRIORITY_TYPE_HIGH) {
                mContentView.setImageViewResource(R.id.img_noti_icon, R.drawable.ic_notifications_green);
            } else if (Integer.parseInt(taskDetailsEntity.getTaskPriority()) == PRIORITY_TYPE_VERY_HIGH) {
                mContentView.setImageViewResource(R.id.img_noti_icon, R.drawable.ic_notifications_red);
            }
            mContentView.setTextViewText(R.id.task_name, taskDetailsEntity.getTaskName());
            mContentView.setTextViewText(R.id.task_note, taskDetailsEntity.getTaskNote());
            mContentView.setTextViewText(R.id.task_time, taskDetailsEntity.getTaskTime());
            builder.setContent(mContentView);

            builder.setContentTitle(taskDetailsEntity.getTaskName())  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(taskDetailsEntity.getTaskNote())  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(taskDetailsEntity.getTaskName())
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            AppExecutors appExecutors = new AppExecutors();
            appExecutors.getExeDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                    if (database != null) {
                        String repeatMode = taskDetailsEntity.getTaskRepeatMode();
                        if (repeatMode.equalsIgnoreCase(EVERY_DAY)) {
                            nextDate = ApplicationUtils.getNextDateFromCurrentDate(taskDetailsEntity.getTaskDate());
                        } else if (repeatMode.equalsIgnoreCase(EVERY_WEEK)) {
                            nextDate = ApplicationUtils.getNextDateOfWeekFromCurrentDate(taskDetailsEntity.getTaskDate());
                        } else if (repeatMode.equalsIgnoreCase(EVERY_MONTH)) {
                            nextDate = ApplicationUtils.getNextDateOfMonthFromCurrentDate(taskDetailsEntity.getTaskDate());
                        } else if (repeatMode.equalsIgnoreCase(EVERY_YEAR)) {
                            nextDate = ApplicationUtils.getNextDateOfYearFromCurrentDate(taskDetailsEntity.getTaskDate());
                        }

                        if (!TextUtils.isEmpty(nextDate)) {
                        }
                        /*UPDATE COMPLETE TASKDETAILS JSON IN ALLFOLDER*/
                        taskDetailsEntity.setNotificationDate(nextDate);
                        AddTaskDetails addTaskDetails = updateNotiDateInAllFolder(taskDetailsEntity, nextDate);
                        database.getFolderDao().updateTakDetailsforTaskStatusV1(addTaskDetails, taskDetailsEntity.getAllfolderid());

                        /*UPDATE ONLY TASK DATE IN TASKDETAILS*/
                        database.getTaskDetailsDao().updateNotificationDate(taskDetailsEntity.getAllfolderid(), nextDate);

                    }

                }
            });

        } else {

            builder = new NotificationCompat.Builder(context);

            intent = new Intent(context, AddTaskActivity.class);

            intent.setAction(String.valueOf(taskDetailsEntity.getAllfolderid()));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, notificationid, intent, 0);

            RemoteViews mContentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
            if (Integer.parseInt(taskDetailsEntity.getTaskPriority()) == PRIORITY_TYPE_LOW) {
                mContentView.setImageViewResource(R.id.img_noti_icon, R.drawable.ic_notifications_yellow);
            } else if (Integer.parseInt(taskDetailsEntity.getTaskPriority()) == PRIORITY_TYPE_MEDIUM) {
                mContentView.setImageViewResource(R.id.img_noti_icon, R.drawable.ic_notifications_blue);
            } else if (Integer.parseInt(taskDetailsEntity.getTaskPriority()) == PRIORITY_TYPE_HIGH) {
                mContentView.setImageViewResource(R.id.img_noti_icon, R.drawable.ic_notifications_green);
            } else if (Integer.parseInt(taskDetailsEntity.getTaskPriority()) == PRIORITY_TYPE_VERY_HIGH) {
                mContentView.setImageViewResource(R.id.img_noti_icon, R.drawable.ic_notifications_red);
            }
            mContentView.setTextViewText(R.id.task_name, taskDetailsEntity.getTaskName());
            mContentView.setTextViewText(R.id.task_note, taskDetailsEntity.getTaskNote());
            mContentView.setTextViewText(R.id.task_time, taskDetailsEntity.getTaskTime());
            builder.setContent(mContentView);

            builder.setContentTitle(taskDetailsEntity.getTaskName())                           // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)// required
                    .setContentText(taskDetailsEntity.getTaskNote())  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(taskDetailsEntity.getTaskName())
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);

             /*Once Notification is fired, Update date in table according to alert flag*/
            AppExecutors appExecutors1 = new AppExecutors();
            appExecutors1.getExeDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                    if (database != null) {
                        String repeatMode = taskDetailsEntity.getTaskRepeatMode();
                        if (repeatMode.equalsIgnoreCase(EVERY_DAY)) {
                            nextDate = ApplicationUtils.getNextDateFromCurrentDate(taskDetailsEntity.getTaskDate());
                        } else if (repeatMode.equalsIgnoreCase(EVERY_WEEK)) {
                            nextDate = ApplicationUtils.getNextDateOfWeekFromCurrentDate(taskDetailsEntity.getTaskDate());
                        } else if (repeatMode.equalsIgnoreCase(EVERY_MONTH)) {
                            nextDate = ApplicationUtils.getNextDateOfMonthFromCurrentDate(taskDetailsEntity.getTaskDate());
                        } else if (repeatMode.equalsIgnoreCase(EVERY_YEAR)) {
                            nextDate = ApplicationUtils.getNextDateOfYearFromCurrentDate(taskDetailsEntity.getTaskDate());
                        }

                        if (!TextUtils.isEmpty(nextDate)) {
                        }
                        /*UPDATE COMPLETE TASKDETAILS JSON IN ALLFOLDER*/
                        taskDetailsEntity.setNotificationDate(nextDate);
                        AddTaskDetails addTaskDetails = updateNotiDateInAllFolder(taskDetailsEntity, nextDate);
                        database.getFolderDao().updateTakDetailsforTaskStatusV1(addTaskDetails, taskDetailsEntity.getAllfolderid());

                        /*UPDATE ONLY TASK DATE IN TASKDETAILS*/
                        database.getTaskDetailsDao().updateNotificationDate(taskDetailsEntity.getAllfolderid(), nextDate);

                    }

                }
            });
        }

        Notification notification = builder.build();
        notifManager.notify(notificationid, notification);
    }

    private AddTaskDetails updateNotiDateInAllFolder(TaskDetailsEntity taskDetailsEntity, String date) {
        AddTaskDetails addTaskDetails = new AddTaskDetails();
        addTaskDetails.setTaskId(taskDetailsEntity.getTaskId());
        addTaskDetails.setTaskName(taskDetailsEntity.getTaskName());
        addTaskDetails.setTaskPriority(taskDetailsEntity.getTaskPriority());
        addTaskDetails.setTaskRepeatMode(taskDetailsEntity.getTaskRepeatMode());
        addTaskDetails.setTaskNote(taskDetailsEntity.getTaskNote());
        addTaskDetails.setTaskDate(taskDetailsEntity.getTaskDate());
        addTaskDetails.setNotificationDate(date);
        addTaskDetails.setTaskTime(taskDetailsEntity.getTaskTime());
        addTaskDetails.setTaskFinishStatus(taskDetailsEntity.getTaskFinishStatus());
        addTaskDetails.setParentColumn(taskDetailsEntity.getParentColumn());
        addTaskDetails.setAllfolderid(taskDetailsEntity.getAllfolderid());
        return addTaskDetails;
    }


}
