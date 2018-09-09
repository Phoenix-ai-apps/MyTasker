package com.b2b.mytask.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.b2b.mytask.db.converter.ObjectConverter;
import com.b2b.mytask.interfaces.Folder;
import com.b2b.mytask.models.AddTaskDetails;
import com.b2b.mytask.models.FolderTask;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
@Entity(tableName = "AllFolder")
public class FolderEntity implements Folder, Parcelable {

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
    private AddTaskDetails taskDetails;
    @TypeConverters(ObjectConverter.class)
    @ColumnInfo(name = "FolderTaskList")


    private List<FolderTask> folderTaskList;


    public FolderEntity() {
    }

    protected FolderEntity(Parcel in) {
        id = in.readInt();
        folderName = in.readString();
        color = in.readInt();
        insertedFrom = in.readString();

        taskDetails = in.readParcelable(AddTaskDetails.class.getClassLoader());
    }


    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String getInsertedFrom() {
        return insertedFrom;
    }

    public void setInsertedFrom(String insertedFrom) {
        this.insertedFrom = insertedFrom;
    }

    @Override
    public AddTaskDetails getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(AddTaskDetails taskDetails) {
        this.taskDetails = taskDetails;
    }

    @Override
    public List<FolderTask> getFolderTaskList() {
        return folderTaskList;
    }

    public void setFolderTaskList(List<FolderTask> folderTaskList) {
        this.folderTaskList = folderTaskList;
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
        dest.writeParcelable(taskDetails, flags);

    }

}
