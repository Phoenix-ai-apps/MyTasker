package com.b2b.mytask.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.b2b.mytask.interfaces.TaskDetails;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
@Entity(tableName = "TaskDetails")
public class TaskDetailsEntity implements TaskDetails, Parcelable {

    public static final Creator<TaskDetailsEntity> CREATOR = new Creator<TaskDetailsEntity>() {
        @Override
        public TaskDetailsEntity createFromParcel(Parcel source) {
            return new TaskDetailsEntity(source);
        }

        @Override
        public TaskDetailsEntity[] newArray(int size) {
            return new TaskDetailsEntity[size];
        }
    };
    @PrimaryKey(autoGenerate = true)
    private int taskId;
    private String taskName;
    private String taskDate;
    private String taskTime;
    private String taskRepeatMode;
    private String taskNote;
    private String taskPriority;
    private boolean isDateGone;
    private String parentColumn;
    private String taskFinishStatus;
    private int allfolderid;
    private String notificationDate;

    @Ignore
    public TaskDetailsEntity() {
    }

    public TaskDetailsEntity(int taskId, String taskName, String taskDate, String taskTime,
                             String taskRepeatMode, String taskNote, String taskPriority,
                             boolean isDateGone, String parentColumn, String taskFinishStatus, int allfolderid, String notificationDate) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.taskRepeatMode = taskRepeatMode;
        this.taskNote = taskNote;
        this.taskPriority = taskPriority;
        this.isDateGone = isDateGone;
        this.parentColumn = parentColumn;
        this.taskFinishStatus = taskFinishStatus;
        this.allfolderid = allfolderid;
        this.notificationDate = notificationDate;
    }

    public TaskDetailsEntity(TaskDetails taskDetails) {
        this.taskId = taskDetails.getTaskId();
        this.taskName = taskDetails.getTaskName();
        this.taskDate = taskDetails.getTaskDate();
        this.taskTime = taskDetails.getTaskTime();
        this.taskRepeatMode = taskDetails.getTaskRepeatMode();
        this.taskNote = taskDetails.getTaskNote();
        this.taskPriority = taskDetails.getTaskPriority();
        this.isDateGone = taskDetails.getDateGone();
        this.parentColumn = taskDetails.getParentColumn();
        this.taskFinishStatus = taskDetails.getTaskFinishStatus();
        this.allfolderid = taskDetails.getAllfolderid();
        this.notificationDate = taskDetails.getNotificationDate();
    }

    protected TaskDetailsEntity(Parcel in) {
        this.taskId = in.readInt();
        this.taskName = in.readString();
        this.taskDate = in.readString();
        this.taskTime = in.readString();
        this.taskRepeatMode = in.readString();
        this.taskNote = in.readString();
        this.taskPriority = in.readString();
        this.isDateGone = in.readByte() != 0;
        this.parentColumn = in.readString();
        this.taskFinishStatus = in.readString();
        this.allfolderid = in.readInt();
        this.notificationDate = in.readString();
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    @Override
    public int getAllfolderid() {
        return allfolderid;
    }

    public void setAllfolderid(int allfolderid) {
        this.allfolderid = allfolderid;
    }

    @Override
    public String getTaskFinishStatus() {
        return taskFinishStatus;
    }

    public void setTaskFinishStatus(String taskFinishStatus) {
        this.taskFinishStatus = taskFinishStatus;
    }

    @Override
    public String getParentColumn() {
        return parentColumn;
    }

    public void setParentColumn(String parentColumn) {
        this.parentColumn = parentColumn;
    }

    @Override
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    @Override
    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    @Override
    public String getTaskRepeatMode() {
        return taskRepeatMode;
    }

    public void setTaskRepeatMode(String taskRepeatMode) {
        this.taskRepeatMode = taskRepeatMode;
    }

    @Override
    public String getTaskNote() {
        return taskNote;
    }

    public void setTaskNote(String taskNote) {
        this.taskNote = taskNote;
    }

    @Override
    public String getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
    }

    @Override
    public boolean getDateGone() {
        return isDateGone;
    }

    public void setDateGone(boolean dateGone) {
        isDateGone = dateGone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.taskId);
        dest.writeString(this.taskName);
        dest.writeString(this.taskDate);
        dest.writeString(this.taskTime);
        dest.writeString(this.taskRepeatMode);
        dest.writeString(this.taskNote);
        dest.writeString(this.taskPriority);
        dest.writeByte(this.isDateGone ? (byte) 1 : (byte) 0);
        dest.writeString(this.parentColumn);
        dest.writeString(this.taskFinishStatus);
        dest.writeInt(this.allfolderid);
        dest.writeString(this.notificationDate);
    }
}
