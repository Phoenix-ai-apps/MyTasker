package com.b2b.sampleb2b.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.b2b.sampleb2b.interfaces.SubFolder;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
@Entity(tableName = "SubFolder")
public class SubFolderEntity implements SubFolder {
    @PrimaryKey(autoGenerate = true)
    private int    id;
    private String parentFolder;
    private String childFolder ;

    public void setId(int id) {
        this.id = id;
    }

    public void setParentFolder(String parentFolder) {
        this.parentFolder = parentFolder;
    }

    public void setChildFolder(String childFolder) {
        this.childFolder = childFolder;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getParentFolder() {
        return parentFolder;
    }

    @Override
    public String getChildFolder() {
        return childFolder;
    }

    public SubFolderEntity( int id,String parentFolder, String childFolder){
        this.id = id;
        this.parentFolder = parentFolder;
        this.childFolder  = childFolder;
    }

    public SubFolderEntity(SubFolder subFolder){
        this.id           = subFolder.getId();
        this.parentFolder = subFolder.getParentFolder();
        this.childFolder  = subFolder.getChildFolder();
    }
}
