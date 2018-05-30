package com.b2b.sampleb2b.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.b2b.sampleb2b.db.entities.FolderEntity;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
@Dao
public interface FolderDao {
    @Query("Select * from AllFolder")
    LiveData<List<FolderEntity>> getAllFolder();

    @Query("Select * from AllFolder where id=:id")
    LiveData<List<FolderEntity>> getFolderById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllFolder(List<FolderEntity> folderEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFolder(FolderEntity folderEntities);

    @Update
    int updateFolderTask(FolderEntity folderEntity);

}
