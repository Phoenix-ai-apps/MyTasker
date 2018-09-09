package com.b2b.mytask.models;

import android.os.Parcel;
import android.os.Parcelable;

public class AddTaskDetails implements Parcelable {
    public static final Creator<AddTaskDetails> CREATOR =
            new Creator<AddTaskDetails>() {
                @Override
                public AddTaskDetails createFromParcel(Parcel source) {
                    return new AddTaskDetails(source);
                }

                @Override
                public AddTaskDetails[] newArray(int size) {
                    return new AddTaskDetails[size];
                }
            };
    private int taskId;
    private String taskName;
    private String taskDate;
    private String taskTime;
    private String taskRepeatMode;
    private String taskNote;
    private String taskPriority;
    private String taskFinishStatus;
    private String parentColumn;
    private int allfolderid;
    private String notificationDate;
    private boolean isDateGone;
    public AddTaskDetails() {
    }

    protected AddTaskDetails(Parcel in) {
        this.taskId = in.readInt();
        this.taskName = in.readString();
        this.taskDate = in.readString();
        this.taskTime = in.readString();
        this.taskRepeatMode = in.readString();
        this.taskNote = in.readString();
        this.taskPriority = in.readString();
        this.taskFinishStatus = in.readString();
        this.allfolderid = in.readInt();
        this.isDateGone = in.readByte() != 0;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public int getAllfolderid() {
        return allfolderid;
    }

    public void setAllfolderid(int allfolderid) {
        this.allfolderid = allfolderid;
    }

    public String getParentColumn() {
        return parentColumn;
    }

    public void setParentColumn(String parentColumn) {
        this.parentColumn = parentColumn;
    }

    public boolean isDateGone() {
        return isDateGone;
    }

    public void setDateGone(boolean dateGone) {
        isDateGone = dateGone;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getTaskRepeatMode() {
        return taskRepeatMode;
    }

    public void setTaskRepeatMode(String taskRepeatMode) {
        this.taskRepeatMode = taskRepeatMode;
    }

    public String getTaskNote() {
        return taskNote;
    }

    public void setTaskNote(String taskNote) {
        this.taskNote = taskNote;
    }

    public String getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
    }

    public String getTaskFinishStatus() {
        return taskFinishStatus;
    }

    public void setTaskFinishStatus(String taskFinishStatus) {
        this.taskFinishStatus = taskFinishStatus;
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
        dest.writeString(this.taskFinishStatus);
        dest.writeInt(this.allfolderid);
        dest.writeByte(this.isDateGone ? (byte) 1 : (byte) 0);
    }
}
