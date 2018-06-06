package com.b2b.sampleb2b.interfaces;

import com.b2b.sampleb2b.db.entities.FolderEntity;

import java.util.List;

/**
 * Created by Abhishek Singh on 7/6/18.
 */
public interface FolderCycle {
    int getId();
    String getFolderName();
    List<FolderEntity> getFolderEntity();
}
