package com.b2b.sampleb2b.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.b2b.sampleb2b.MyTaskApp;
import com.b2b.sampleb2b.db.entities.FolderEntity;
import com.b2b.sampleb2b.db.entities.SubFolderEntity;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public class SubFolderViewModel extends AndroidViewModel {

  private final MediatorLiveData<List<SubFolderEntity>> liveData;

  public SubFolderViewModel(Application application){
      super(application);
      liveData = new MediatorLiveData<>();
      // set by default null, until we get data from the database.
      liveData.setValue(null);
      LiveData<List<SubFolderEntity>> listLiveData =
              ((MyTaskApp) application).getDataRepository().getAllSubFolder();
      liveData.addSource(listLiveData, liveData::setValue);
  }

  public LiveData<List<SubFolderEntity>> getAllFolders(){
      return liveData;
  }
}
