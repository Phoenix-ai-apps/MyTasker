package com.b2b.sampleb2b;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.b2b.sampleb2b.db.MyTaskDatabase;
import com.b2b.sampleb2b.db.entities.FolderCycleFlowEntity;
import com.b2b.sampleb2b.db.entities.FolderEntity;
import com.b2b.sampleb2b.db.entities.SubFolderEntity;
import com.b2b.sampleb2b.db.entities.TaskDetailsEntity;
import com.b2b.sampleb2b.helper.ApplicationHelper;
import com.b2b.sampleb2b.helper.HelperInterface;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public class DataRepository implements HelperInterface{
    private static DataRepository dataRepository;
    private final MyTaskDatabase taskDatabase;
    private MediatorLiveData<List<FolderEntity>> obervableFolderEntity;

    private DataRepository(MyTaskDatabase myTaskDatabase){
        taskDatabase = myTaskDatabase;
        obervableFolderEntity = new MediatorLiveData<>();

        obervableFolderEntity.addSource(taskDatabase.getFolderDao().getAllFolder(),
                allFolderEntity ->{
                    if(taskDatabase.getDatabaseCreated().getValue() != null){
                        obervableFolderEntity.postValue(allFolderEntity);
                    }
                });
    }

    public static DataRepository getDataRepository(final MyTaskDatabase myTaskDatabase){
        if(dataRepository == null){
            synchronized (DataRepository.class){
                if(dataRepository == null){
                    dataRepository = new DataRepository(myTaskDatabase);
                }
            }
        }
        return dataRepository;
    }

    /**
     * Get the list of folders from the database and get notified when the data changes.
     */
    public LiveData<List<FolderEntity>> getAllFolder(){
        return obervableFolderEntity;
    }

    public LiveData<List<FolderEntity>> getAllHomeFolder(String from){
        return taskDatabase.getFolderDao().getFolderByName(from);
    }

    public LiveData<List<FolderEntity>> getAllFolderListByName(String name){
        return taskDatabase.getFolderDao().getFolderListByName(name);
    }

    public LiveData<List<TaskDetailsEntity>> getAllTaskDetails(){
        return taskDatabase.getTaskDetailsDao().loadAllTaskDetails();
    }

    public LiveData<List<SubFolderEntity>> getAllSubFolder(){
        return taskDatabase.getSubFolderDao().loadAllSubFolders();
    }

    public FolderEntity getFolderByName(String name, String parentFolder){
        return taskDatabase.getFolderDao().getFolderByFrom(name, parentFolder);
    }

    public TaskDetailsEntity getTaskByName(String name, String parentFolder){
        return taskDatabase.getTaskDetailsDao().getTaskByName(name, parentFolder);
    }

    public List<FolderEntity> getFolderEntFromFolderCycleByName(String name){
        FolderCycleFlowEntity cycleFlowEntity = taskDatabase.getFolderCycleFlowDao().getFolderCycleFlowByName(name);
        return cycleFlowEntity.getFolderEntity();
    }

    @Override
    public ApplicationHelper getHelper() {
        return ApplicationHelper.getInstance();
    }
}
