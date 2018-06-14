package com.b2b.mytask.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.b2b.mytask.db.entities.FolderEntity;

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

    @Query("Select * from AllFolder where insertedFrom=:insertedFrom")
    LiveData<List<FolderEntity>> getFolderByParentFolder(String insertedFrom);

    @Query("Select * from AllFolder where Folder=:name")
    LiveData<List<FolderEntity>> getFolderListByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllFolder(List<FolderEntity> folderEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFolder(FolderEntity folderEntities);

    @Update
    int updateFolderTask(FolderEntity folderEntity);

    @Query("Select * from AllFolder where Folder=:name and InsertedFrom=:insertedFrom")
    FolderEntity getFolderByFrom(String name, String insertedFrom);

    @Query("Select * from AllFolder where id=:insertionID")
    FolderEntity getFolderByID(int insertionID);

    @Query("Select * from AllFolder where Folder=:folderName")
    FolderEntity getByFolderName(String folderName);

    @Query("Delete from AllFolder where id=:id or InsertedFrom=:insertedBy")
    int deleteFolderByIdAndInsertedBy(int id, String insertedBy);

}
