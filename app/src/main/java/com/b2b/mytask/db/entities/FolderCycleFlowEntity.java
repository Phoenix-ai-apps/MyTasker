package com.b2b.mytask.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.b2b.mytask.db.converter.ObjectConverter;
import com.b2b.mytask.interfaces.FolderCycle;

import java.util.List;

/**
 * Created by Abhishek Singh on 7/6/18.
 */
@Entity(tableName = "FolderCycleFlow")
public class FolderCycleFlowEntity implements FolderCycle {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "FolderName")
    private String folderName;

    @TypeConverters(ObjectConverter.class)
    @ColumnInfo(name = "FolderCycleFlow")
    private List<FolderEntity> folderEntity;

    public void setId(int id) {
        this.id = id;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setFolderEntity(List<FolderEntity> folderEntity) {
        this.folderEntity = folderEntity;
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
    public List<FolderEntity> getFolderEntity() {
        return folderEntity;
    }
}
