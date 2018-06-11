package com.b2b.mytask.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.b2b.mytask.DataRepository;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.db.entities.FolderCycleFlowEntity;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.models.FolderCycleFlow;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public class FolderCycleFlowViewModel extends AndroidViewModel {

  private final MediatorLiveData<List<FolderEntity>> liveData;
  private final MediatorLiveData<FolderCycleFlowEntity> liveDataByName;
  private DataRepository dataRepository;

  public FolderCycleFlowViewModel(Application application){
      super(application);
      liveData       = new MediatorLiveData<>();
      liveDataByName = new MediatorLiveData<>();
      // set by default null, until we get data from the database.
      liveData.setValue(null);
      dataRepository =((MyTaskApp) application).getDataRepository();
      LiveData<List<FolderEntity>> listLiveData = dataRepository.getAllFolder();
      liveData.addSource(listLiveData, liveData::setValue);
  }

  public LiveData<List<FolderEntity>> getAllFolderCycleFlow(){ return liveData; }

  public LiveData<FolderCycleFlowEntity> getFolderCycleFlowByName(String name){
      // set by default null, until we get data from the database.
      liveDataByName.setValue(null);
      LiveData<FolderCycleFlowEntity> listLiveData = dataRepository.getFolderEntFromFolderCycleByName(name);
      liveDataByName.addSource(listLiveData, liveDataByName::setValue);
      return liveDataByName;
  }

}
