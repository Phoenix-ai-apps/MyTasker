package com.b2b.sampleb2b.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.b2b.sampleb2b.db.converter.ObjectConverter;
import com.b2b.sampleb2b.interfaces.Folder;
import com.b2b.sampleb2b.models.AddTaskDetails;
import com.b2b.sampleb2b.models.FolderTask;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
@Entity(tableName = "AllFolder")
public class FolderEntity implements Folder, Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "Folder")
    private String folderName;
    @ColumnInfo(name = "Color")
    private int color;
    @ColumnInfo(name = "InsertedFrom")
    private String insertedFrom;
    @TypeConverters(ObjectConverter.class)
    @ColumnInfo(name = "TaskDetails")
    private List<AddTaskDetails> taskDetails;
    @TypeConverters(ObjectConverter.class)
    @ColumnInfo(name = "FolderTaskList")
    private List<FolderTask>     folderTaskList;

    public FolderEntity(){}

    public FolderEntity(Parcel in) {
        id = in.readInt();
        folderName = in.readString();
        color = in.readInt();
        insertedFrom = in.readString();
        taskDetails = in.createTypedArrayList(AddTaskDetails.CREATOR);
    }

    public static final Creator<FolderEntity> CREATOR = new Creator<FolderEntity>() {
        @Override
        public FolderEntity createFromParcel(Parcel in) {
            return new FolderEntity(in);
        }

        @Override
        public FolderEntity[] newArray(int size) {
            return new FolderEntity[size];
        }
    };

    public void setId(int id) {
        this.id = id;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setInsertedFrom(String insertedFrom) {
        this.insertedFrom = insertedFrom;
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
    public String getInsertedFrom() {
        return insertedFrom;
    }

    @Override
    public List<AddTaskDetails> getTaskDetails() {
        return taskDetails;
    }

    @Override
    public List<FolderTask> getFolderTaskList() {
        return folderTaskList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(folderName);
        dest.writeInt(color);
        dest.writeString(insertedFrom);
        dest.writeTypedList(taskDetails);
    }
}
