package com.b2b.mytask.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.b2b.mytask.DataRepository;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.db.entities.FolderEntity;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public class FolderViewModel extends AndroidViewModel {

    private final MediatorLiveData<List<FolderEntity>> liveData;
    private final MediatorLiveData<List<FolderEntity>> liveDataByName;
    private DataRepository dataRepository;

    public FolderViewModel(Application application) {
        super(application);
        liveData = new MediatorLiveData<>();
        liveDataByName = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        liveData.setValue(null);
        dataRepository = ((MyTaskApp) application).getDataRepository();
        LiveData<List<FolderEntity>> listLiveData = dataRepository.getAllFolder();
        liveData.addSource(listLiveData, liveData::setValue);
    }

    public LiveData<List<FolderEntity>> getAllFolders() {
        return liveData;
    }

    public LiveData<List<FolderEntity>> getAllFoldersByInsertedFolder(String from) {
        // set by default null, until we get data from the database.
        liveDataByName.setValue(null);
        LiveData<List<FolderEntity>> listLiveDataByName = dataRepository.getAllFolderByParent(from);
        liveDataByName.addSource(listLiveDataByName, liveDataByName::setValue);
        return listLiveDataByName;//liveDataByName;
    }


}
