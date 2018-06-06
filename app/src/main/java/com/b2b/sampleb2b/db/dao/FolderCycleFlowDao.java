package com.b2b.sampleb2b.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.b2b.sampleb2b.db.entities.FolderCycleFlowEntity;
import com.b2b.sampleb2b.db.entities.FolderEntity;

import java.util.List;

/**
 * Created by Abhishek Singh on 7/6/18.
 */
@Dao
public interface FolderCycleFlowDao {

    @Query("Select * from FolderCycleFlow")
    List<FolderCycleFlowEntity> getAllFolderCycleFlowList();

    @Query("Select * from FolderCycleFlow where FolderName=:name")
    FolderCycleFlowEntity getFolderCycleFlowByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFolderCycleFlow(FolderCycleFlowEntity folderCycleFlowEntity);

    @Update
    int updateFolderCycleFlow(FolderCycleFlowEntity folderCycleFlowEntity);

}
