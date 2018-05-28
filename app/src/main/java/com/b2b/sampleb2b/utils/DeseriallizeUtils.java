package com.b2b.sampleb2b.utils;

import com.b2b.sampleb2b.models.AddTaskDetails;
import com.b2b.sampleb2b.models.FolderTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class DeseriallizeUtils {

    public static Gson getGsonObject() {
        return new GsonBuilder().create();
    }

    public static <T> T deserializeResponse(String response, Class<T> myClass){
        T responseClass = null;
        try{
            responseClass = getGsonObject().fromJson(response, myClass);
        }catch (Exception e){
         e.printStackTrace();
        }
        return responseClass;
    }

    public static AddTaskDetails deserializeAddTaskDetails(String response){
        AddTaskDetails responseClass = null;
        try{
            responseClass = getGsonObject().fromJson(response,AddTaskDetails.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseClass;
    }

    public static FolderTask deserializeFolderTask(String response){
        FolderTask responseClass = null;
        try{
            responseClass = getGsonObject().fromJson(response,FolderTask.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseClass;
    }

    //
    public static List<AddTaskDetails> deserializeAddTaskDetailsList(String response){
        List<AddTaskDetails> responseClass = null;
        try{
            responseClass = getGsonObject().fromJson(response,
                    new TypeToken<List<AddTaskDetails>>() {
            }.getType());
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseClass;
    }

    public static List<FolderTask> deserializeFolderTaskList(String response){
        List<FolderTask> responseClass = null;
        try{
            responseClass = getGsonObject().fromJson(response,
                    new TypeToken<List<FolderTask>>() {
                    }.getType());
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseClass;
    }

}
