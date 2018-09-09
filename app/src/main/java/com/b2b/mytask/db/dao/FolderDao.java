package com.b2b.mytask.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.models.AddTaskDetails;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
@Dao
public interface FolderDao {
    @Query("Select * from AllFolder")
    LiveData<List<FolderEntity>> getAllFolder();

    @Query("Select * from AllFolder where id=:id")
    FolderEntity getFolderDataById(int id);

    @Query("Select * from AllFolder where insertedFrom=:insertedFrom")
    LiveData<List<FolderEntity>> getFolderByParentFolder(String insertedFrom);

    @Query("Select * from AllFolder where Folder=:name")
    LiveData<List<FolderEntity>> getFolderListByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFolder(FolderEntity folderEntities);

    @Update
    int updateFolderTask(FolderEntity folderEntity);

    @Query("Select * from AllFolder where Folder=:name and InsertedFrom=:insertedFrom and id=:id")
    FolderEntity getFolderByFrom(String name, String insertedFrom, String id);

    @Query("Select * from AllFolder where Folder=:name and InsertedFrom=:insertedFrom")
    FolderEntity getFolderByName(String name, String insertedFrom);

    @Query("Update AllFolder set Folder=:folderName,Color=:color where id=:id")
    int UpdateFolderDetails(String folderName, int color, int id);

    @Query("Select * from AllFolder where id=:id")
    FolderEntity getFolderByID(int id);

    @Query("Select * from AllFolder where Folder=:folderName")
    FolderEntity getByFolderName(String folderName);

    @Query("Select * from AllFolder where InsertedFrom=:parentColumn")
    List<FolderEntity> getListFolderByParent(int parentColumn);

    @Query("Delete from AllFolder where id=:id or InsertedFrom=:insertedBy")
    int deleteFolderByIdORInsertedBy(int id, String insertedBy);

    @Query("select * from AllFolder where id=:allFolderid")
    FolderEntity getDetailsOfTaskFromAllFolder(int allFolderid);

    @Query("Update AllFolder set TaskDetails=:addTaskDetails where id=:id and InsertedFrom=:insertedBy")
    int updateTakDetailsforTaskStatus(AddTaskDetails addTaskDetails, int id, String insertedBy);

    @Query("Update AllFolder set TaskDetails=:addTaskDetails where id=:id")
    int updateTakDetailsforTaskStatusV1(AddTaskDetails addTaskDetails, int id);

    @Query("Delete from AllFolder where id=:id")
    int deleteFolderByFolderID(int id);

    @Query("Delete from AllFolder where id=:id and InsertedFrom=:insertedBy")
    int deleteFolderByIdANDInsertedBy(int id, String insertedBy);


    @Query("SELECT id FROM AllFolder ORDER BY id DESC LIMIT 1")
    int getLastRecordInsertedInDB();

    @Query("select count(*) from AllFolder where InsertedFrom=:insertedBy and TaskDetails IS NOT NULL")
    int getTotalTasksInFolderByInsertedId(int insertedBy);


}
