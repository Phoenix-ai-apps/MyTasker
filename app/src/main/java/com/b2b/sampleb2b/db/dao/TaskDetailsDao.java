package com.b2b.sampleb2b.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.b2b.sampleb2b.db.entities.SubFolderEntity;
import com.b2b.sampleb2b.db.entities.TaskDetailsEntity;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
@Dao
public interface TaskDetailsDao {
    @Query("Select * from TaskDetails")
    LiveData<List<TaskDetailsEntity>> loadAllTaskDetails();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTaskDetails(List<TaskDetailsEntity> folderEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTaskDetails(TaskDetailsEntity folderEntitie);

    @Query("Select * from TaskDetails where taskName=:name and parentColumn=:parentFolder")
    TaskDetailsEntity getTaskByName(String name, String parentFolder);

    @Update
    int updateTaskDetails(TaskDetailsEntity folderEntitie);


}
