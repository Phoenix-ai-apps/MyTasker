package com.b2b.sampleb2b.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.b2b.sampleb2b.db.entities.FolderEntity;
import com.b2b.sampleb2b.db.entities.SubFolderEntity;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
@Dao
public interface SubFolderDao {
    @Query("Select * from SubFolder")
    LiveData<List<SubFolderEntity>> loadAllSubFolders();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllSubFolder(List<SubFolderEntity> folderEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSubFolder(SubFolderEntity folderEntitie);
}
