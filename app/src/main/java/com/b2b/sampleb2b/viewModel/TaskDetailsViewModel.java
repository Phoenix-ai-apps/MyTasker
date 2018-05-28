package com.b2b.sampleb2b.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.b2b.sampleb2b.MyTaskApp;
import com.b2b.sampleb2b.db.entities.FolderEntity;
import com.b2b.sampleb2b.db.entities.TaskDetailsEntity;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public class TaskDetailsViewModel extends AndroidViewModel {

  private final MediatorLiveData<List<TaskDetailsEntity>> liveData;

  public TaskDetailsViewModel(Application application){
      super(application);
      liveData = new MediatorLiveData<>();
      // set by default null, until we get data from the database.
      liveData.setValue(null);
      LiveData<List<TaskDetailsEntity>> listLiveData = ((MyTaskApp) application).getDataRepository().getAllTaskDetails();
      liveData.addSource(listLiveData, liveData::setValue);
  }

  public LiveData<List<TaskDetailsEntity>> getAllFolders(){
      return liveData;
  }
}
