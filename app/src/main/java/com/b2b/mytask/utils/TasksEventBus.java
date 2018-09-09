package com.b2b.mytask.utils;

public class TasksEventBus {
    private static org.greenrobot.eventbus.EventBus sBus;

    public static org.greenrobot.eventbus.EventBus getBus() {
        if (sBus == null)
            sBus = org.greenrobot.eventbus.EventBus.getDefault();
        return sBus;
    }

}
