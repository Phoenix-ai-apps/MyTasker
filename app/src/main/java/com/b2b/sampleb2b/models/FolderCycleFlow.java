package com.b2b.sampleb2b.models;

import com.b2b.sampleb2b.db.entities.FolderEntity;
import com.b2b.sampleb2b.interfaces.FolderCycle;

import java.util.List;

/**
 * Created by Abhishek Singh on 7/6/18.
 */
public class FolderCycleFlow implements FolderCycle {
    private int                id;
    private String             folderName;
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
