package com.b2b.mytask.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.b2b.mytask.db.entities.TaskDetailsEntity;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
@Dao
public interface TaskDetailsDao {

    /*  @Query("Select * from TaskDetails")
      LiveData<List<TaskDetailsEntity>> loadAllTaskDetails();
  */
    @Query("select * from TaskDetails ORDER BY date(taskDate) ASC")
    LiveData<List<TaskDetailsEntity>> loadAllTaskDetails();

    @Query("SELECT * FROM TaskDetails WHERE taskDate between :startDateOfWeek and :endDateOfWeek")
    LiveData<List<TaskDetailsEntity>> loadAllTaskDetailsByWeek(String startDateOfWeek, String endDateOfWeek);

    @Query("SELECT * FROM TaskDetails WHERE taskDate=:currentDateOfWeek")
    LiveData<List<TaskDetailsEntity>> loadAllTaskDetailsByToday(String currentDateOfWeek);

    @Query("SELECT * FROM TaskDetails WHERE taskDate=:currentDateOfWeek")
    List<TaskDetailsEntity> loadAllTaskDetailsByToday_V1(String currentDateOfWeek);

    @Query("SELECT * FROM TaskDetails WHERE taskFinishStatus=:status")
    LiveData<List<TaskDetailsEntity>> loadAllTaskDetailsByCompleted(String status);

    @Query("Select * from TaskDetails")
    List<TaskDetailsEntity> getAllTaskDetails();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTaskDetails(List<TaskDetailsEntity> folderEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTaskDetails(TaskDetailsEntity folderEntitie);

    @Query("Select * from TaskDetails where taskName=:name and parentColumn=:parentFolder")
    TaskDetailsEntity getTaskByName(String name, String parentFolder);

    @Query("Select * from TaskDetails where taskId=:taskId and parentColumn=:parentFolder")
    TaskDetailsEntity getTaskById(int taskId, String parentFolder);

    @Update
    int updateTaskDetails(TaskDetailsEntity folderEntitie);

    @Query("Update TaskDetails set parentColumn=:parentColumn where taskId=:taskId")
    int updateParentColumn(int taskId, int parentColumn);

    @Query("Update TaskDetails set taskFinishStatus=:status where taskId=:taskId")
    int updateTaskFinishStatus(int taskId, String status);

    @Query("Update TaskDetails set notificationDate=:date where taskId=:taskId")
    int updateNotificationDate(int taskId, String date);

    @Query("Delete from TaskDetails where taskId=:taskid")
    int deleteTaskByTaskId(int taskid);

    @Query("Delete from TaskDetails where parentColumn=:insertedId")
    int deleteTaskByInsertedId(int insertedId);

    @Query("Select * from TaskDetails where taskName=:name and parentColumn=:parentColumn")
    TaskDetailsEntity getTaskDetailsByTaskNameAndParentColumn(String name, String parentColumn);

    @Query("Select * from TaskDetails where taskId=:id")
    TaskDetailsEntity getTaskDetailsByTaskID(String id);

    @Query("Update TaskDetails set allfolderid=:allfolderid where taskId=:taskId")
    int updateAllFoldersIdInTaskDetails(int taskId, int allfolderid);

    @Query("Select * from TaskDetails where notificationDate=:date and taskFinishStatus=:taskFinishStatus and taskRepeatMode !=:never ")
    List<TaskDetailsEntity> getAllTasksByDate(String date, String taskFinishStatus, String never);

}
