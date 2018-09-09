package com.b2b.mytask.interfaces;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public interface TaskDetails {
    int getTaskId();

    String getTaskName();

    String getTaskDate();

    String getTaskTime();

    String getTaskRepeatMode();

    String getTaskNote();

    String getTaskPriority();

    String getParentColumn();

    String getTaskFinishStatus();

    String getNotificationDate();

    int getAllfolderid();

    boolean getDateGone();
}
