package com.b2b.sampleb2b.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.b2b.sampleb2b.db.converter.ObjectConverter;
import com.b2b.sampleb2b.interfaces.Folder;
import com.b2b.sampleb2b.models.AddTaskDetails;
import com.b2b.sampleb2b.models.FolderTask;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
@Entity(tableName = "AllFolder")
public class FolderEntity implements Folder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "Folder")
    private String folderName;
    @ColumnInfo(name = "Color")
    private int color;
    @ColumnInfo(name = "From")
    private String from ;
    @TypeConverters(ObjectConverter.class)
    @ColumnInfo(name = "TaskDetails")
    private List<AddTaskDetails> taskDetails;
    @TypeConverters(ObjectConverter.class)
    @ColumnInfo(name = "FolderTaskList")
    private List<FolderTask>     folderTaskList;

    public void setId(int id) {
        this.id = id;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTaskDetails(List<AddTaskDetails> taskDetails) {
        this.taskDetails = taskDetails;
    }

    public void setFolderTaskList(List<FolderTask> folderTaskList) {
        this.folderTaskList = folderTaskList;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getFolderName() {
        return folderName;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public List<AddTaskDetails> getTaskDetails() {
        return taskDetails;
    }

    @Override
    public List<FolderTask> getFolderTaskList() {
        return folderTaskList;
    }
}
