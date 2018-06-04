package com.b2b.sampleb2b.interfaces;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public interface TaskDetails {
    int     getTaskId();
    String  getTaskName();
    String  getTaskDate();
    String  getTaskTime();
    String  getTaskRepeatMode();
    String  getTaskNote();
    String  getTaskPriority();
    String  getParentColumn();
    boolean getDateGone();
}
