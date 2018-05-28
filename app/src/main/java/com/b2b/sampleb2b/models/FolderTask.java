package com.b2b.sampleb2b.models;

import java.util.List;

public class FolderTask {

    private String               folderName;
    private int                  color;
    private String               from ;
    private List<AddTaskDetails> taskDetails;
    private List<FolderTask>     folderTaskList;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<AddTaskDetails> getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(List<AddTaskDetails> taskDetails) {
        this.taskDetails = taskDetails;
    }

    public List<FolderTask> getFolderTaskList() {
        return folderTaskList;
    }

    public void setFolderTaskList(List<FolderTask> folderTaskList) {
        this.folderTaskList = folderTaskList;
    }
}
