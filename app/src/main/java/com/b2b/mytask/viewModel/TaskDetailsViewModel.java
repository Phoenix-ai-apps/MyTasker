package com.b2b.mytask.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.b2b.mytask.DataRepository;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.db.entities.TaskDetailsEntity;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public class TaskDetailsViewModel extends AndroidViewModel {

    private final MediatorLiveData<List<TaskDetailsEntity>> liveData;
    private DataRepository dataRepository;

    public TaskDetailsViewModel(Application application) {
        super(application);
        liveData = new MediatorLiveData<>();
        dataRepository = ((MyTaskApp) application).getDataRepository();
    }

    public LiveData<List<TaskDetailsEntity>> getAllTasks() {
        // set by default null, until we get data from the database.
        liveData.setValue(null);
        LiveData<List<TaskDetailsEntity>> listLiveData = dataRepository.getAllTaskDetails();
        liveData.addSource(listLiveData, liveData::setValue);
        return listLiveData;//liveData;
    }

    public LiveData<List<TaskDetailsEntity>> getAllTaskDetailsbyWeek(String startDateOfWeek, String endDateOfWeek) {
        // set by default null, until we get data from the database.
        liveData.setValue(null);
        LiveData<List<TaskDetailsEntity>> listLiveDataByweek = dataRepository.getAllTaskDetailsbyWeek(startDateOfWeek, endDateOfWeek);
        liveData.addSource(listLiveDataByweek, liveData::setValue);
        return listLiveDataByweek;//liveData;
    }

    public LiveData<List<TaskDetailsEntity>> getAllTaskDetailsbyToday(String currentDateOfWeek) {
        // set by default null, until we get data from the database.
        liveData.setValue(null);
        LiveData<List<TaskDetailsEntity>> listLiveDataByToday = dataRepository.getAllTaskDetailsbyToday(currentDateOfWeek);
        liveData.addSource(listLiveDataByToday, liveData::setValue);
        return listLiveDataByToday;//liveData;
    }

    public LiveData<List<TaskDetailsEntity>> getAllTaskDetailsbyCompleted() {
        // set by default null, until we get data from the database.
        liveData.setValue(null);
        LiveData<List<TaskDetailsEntity>> listLiveDataByToday = dataRepository.getAllTaskDetailsbyCompleted();
        liveData.addSource(listLiveDataByToday, liveData::setValue);
        return listLiveDataByToday;//liveData;
    }
}
