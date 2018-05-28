package com.b2b.sampleb2b.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.b2b.sampleb2b.service.TaskAlarmService;

public class TaskAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service1 = new Intent(context, TaskAlarmService.class);
        context.startService(service1);
    }
}
