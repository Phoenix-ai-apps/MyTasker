package com.b2b.mytask.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.b2b.mytask.interfaces.TaskDetails;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
@Entity(tableName = "TaskDetails")
public class TaskDetailsEntity implements TaskDetails{
    @PrimaryKey(autoGenerate = true)
    private int     taskId;
    private String  taskName;
    private String  taskDate;
    private String  taskTime;
    private String  taskRepeatMode;
    private String  taskNote;
    private String  taskPriority;
    private boolean isDateGone;
    private String  parentColumn;

    public void setDateGone(boolean dateGone) {
        isDateGone = dateGone;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public void setTaskRepeatMode(String taskRepeatMode) {
        this.taskRepeatMode = taskRepeatMode;
    }

    public void setTaskNote(String taskNote) {
        this.taskNote = taskNote;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
    }

    public void setParentColumn(String parentColumn) {
        this.parentColumn = parentColumn;
    }

    @Override
    public String getParentColumn() {
        return parentColumn;
    }

    @Override
    public int getTaskId() {
        return taskId;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    @Override
    public String getTaskDate() {
        return taskDate;
    }

    @Override
    public String getTaskTime() {
        return taskTime;
    }

    @Override
    public String getTaskRepeatMode() {
        return taskRepeatMode;
    }

    @Override
    public String getTaskNote() {
        return taskNote;
    }

    @Override
    public String getTaskPriority() {
        return taskPriority;
    }

    @Override
    public boolean getDateGone() {
        return isDateGone;
    }

    @Ignore
    public TaskDetailsEntity(){}

    public TaskDetailsEntity(int taskId,String taskName, String taskDate, String taskTime,
                             String taskRepeatMode, String taskNote, String taskPriority,
                             boolean isDateGone, String parentColumn){
        this.taskId         = taskId;
        this.taskName       = taskName;
        this.taskDate       = taskDate;
        this.taskTime       = taskTime;
        this.taskRepeatMode = taskRepeatMode;
        this.taskNote       = taskNote;
        this.taskPriority   = taskPriority;
        this.isDateGone     = isDateGone;
        this.parentColumn   = parentColumn;
    }

    public TaskDetailsEntity(TaskDetails taskDetails){
        this.taskId         = taskDetails.getTaskId();
        this.taskName       = taskDetails.getTaskName();
        this.taskDate       = taskDetails.getTaskDate();
        this.taskTime       = taskDetails.getTaskTime();
        this.taskRepeatMode = taskDetails.getTaskRepeatMode();
        this.taskNote       = taskDetails.getTaskNote();
        this.taskPriority   = taskDetails.getTaskPriority();
        this.isDateGone     = taskDetails.getDateGone();
        this.parentColumn   = taskDetails.getParentColumn();
    }
}
