package com.b2b.mytask.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.b2b.mytask.AppExecutors;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.TaskDetailsEntity;
import com.b2b.mytask.utils.ApplicationUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import static org.greenrobot.eventbus.EventBus.TAG;


/**
 * Created by Nihar.s on 16/8/18.
 */

public class SetAlarmForAllTaskReceiver_V1 extends BroadcastReceiver implements AllConstants {
    int currentPosition = 0;
    private Context context;
    private List<TaskDetailsEntity> entityList;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String currentDate = ApplicationUtils.getCurrentDate();
        AppExecutors appExecutors = new AppExecutors();
        entityList = new ArrayList<>();
        appExecutors.getExeDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                entityList = database.getTaskDetailsDao().getAllTasksByDate(currentDate, "0", NEVER);

                if (entityList != null && entityList.size() > 0) {
                    for (int i = 0; i < entityList.size(); i++) {
                        if (ApplicationUtils.comparetimings(ApplicationUtils.getCurrentTime(), entityList.get(i).getTaskTime())) {
                            StringTokenizer tokens = new StringTokenizer(entityList.get(i).getTaskTime(), " =,;");
                            String hours = tokens.nextToken();
                            String am_pm = tokens.nextToken();
                            StringTokenizer tokens1 = new StringTokenizer(hours, " =,;:");
                            String first_hours = tokens1.nextToken();
                            String first_minutes = tokens1.nextToken();

                            setTaskAlarm(Integer.parseInt(first_hours), Integer.parseInt(first_minutes), am_pm, entityList.get(i).getTaskId(), entityList.get(i));
                        }

                    }
                }
            }
        });


    }

    /*AM:-  0, PM:-  1*/
    private void setTaskAlarm(int hour, int minutes, String AM_PM, int notificationid, TaskDetailsEntity taskDetailsEntity) {
        Calendar alarmStartTime = Calendar.getInstance();
        if (hour == 12) {
            hour = 0;
        }
        alarmStartTime.set(Calendar.HOUR, hour);
        alarmStartTime.set(Calendar.MINUTE, minutes);
        alarmStartTime.set(Calendar.SECOND, 0);
        alarmStartTime.set(Calendar.MILLISECOND, 0);
        if (AM_PM.equalsIgnoreCase("AM")) {
            alarmStartTime.set(Calendar.AM_PM, 0);
        } else {
            alarmStartTime.set(Calendar.AM_PM, 1);
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, TaskAlarmNotificationReceiver_V1.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", taskDetailsEntity);
        bundle.putInt("notificationid", notificationid);
        alarmIntent.putExtra("bundle", bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationid, alarmIntent, 0);
        if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), pendingIntent);
        }

        Log.d(TAG, "setTaskAlarm: " + hour + " :" + minutes + " " + notificationid + " " + AM_PM);
        //   Toast.makeText(context, "alarm set for " + hour + " :" + minutes + " " + AM_PM, Toast.LENGTH_SHORT).show();
    }


}