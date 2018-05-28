package com.b2b.sampleb2b.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.b2b.sampleb2b.models.AddTaskDetails;
import com.b2b.sampleb2b.models.FolderTask;
import com.b2b.sampleb2b.utils.ApplicationUtils;
import com.b2b.sampleb2b.utils.DeseriallizeUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public class ObjectConverter {
  @TypeConverter
    public static AddTaskDetails toAddTaskDetails(String json){
      AddTaskDetails objClass = null;
      return objClass = DeseriallizeUtils.deserializeAddTaskDetails(json);
  }

  @TypeConverter
    public static String fromAddTaskDetailsToJson(AddTaskDetails myclass){
    String json = null;
    return json = ApplicationUtils.toJson(myclass);
  }

  @TypeConverter
  public static List<AddTaskDetails> toAddTaskDetailsList(String json){
    List<AddTaskDetails> objClass = null;
    return objClass = DeseriallizeUtils.deserializeAddTaskDetailsList(json);
  }

  @TypeConverter
  public static String fromAddTaskDetailsListToJson(List<AddTaskDetails> myclass){
    String json = null;
    return json = ApplicationUtils.toJson(myclass);
  }

  @TypeConverter
  public static FolderTask toFolderTask(String json){
    FolderTask objClass = null;
    return objClass = DeseriallizeUtils.deserializeFolderTask(json);
  }

  @TypeConverter
  public static String fromFolderTaskToJson(FolderTask myclass){
    String json = null;
    return json = ApplicationUtils.toJson(myclass);
  }

  @TypeConverter
  public static List<FolderTask> toFolderTaskList(String json){
    List<FolderTask> objClass = null;
    return objClass = DeseriallizeUtils.deserializeFolderTaskList(json);
  }

  @TypeConverter
  public static String fromFolderTaskListToJson(List<FolderTask> myclass){
    String json = null;
    return json = ApplicationUtils.toJson(myclass);
  }

  @TypeConverter
  public static Date toDate(Long timeStamp) {
    return timeStamp == null ? null : new Date(timeStamp);
  }

  @TypeConverter
  public static Long toTimeStamp(Date date) {
    return date == null ? null : date.getTime();
  }


}
