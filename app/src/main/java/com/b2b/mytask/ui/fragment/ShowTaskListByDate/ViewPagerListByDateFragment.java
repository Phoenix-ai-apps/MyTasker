package com.b2b.mytask.ui.fragment.ShowTaskListByDate;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.mytask.AppExecutors;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.R;
import com.b2b.mytask.adapters.CalenderAllTaskListAdapter;
import com.b2b.mytask.databinding.FragmentAllTaskBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.TaskDetailsEntity;
import com.b2b.mytask.utils.ApplicationUtils;
import com.b2b.mytask.utils.Events;
import com.b2b.mytask.utils.TasksEventBus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nihar.s on 2/8/18.
 */

public class ViewPagerListByDateFragment extends Fragment {

    private FragmentAllTaskBinding binding;
    private String currentDate = "";
    private CalenderAllTaskListAdapter calenderAllTaskListAdapter_;
    private LinearLayoutManager mLinearLayoutManager;
    private List<TaskDetailsEntity> detailsEntityList;

    public ViewPagerListByDateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_task, container, false);
        binding.setLifecycleOwner(this);
        initialiseResource();
        return binding.getRoot();
    }

    private void initialiseResource() {
        detailsEntityList = new ArrayList<>();

        calenderAllTaskListAdapter_ = new CalenderAllTaskListAdapter();
        binding.recyclerTasks.setAdapter(calenderAllTaskListAdapter_);
        // initialize your RecyclerView and your LayoutManager
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerTasks.setLayoutManager(mLinearLayoutManager);
        currentDate = ApplicationUtils.getCurrentDate();
        getTaskListByCurrentDate(currentDate);
    }

    public void getTaskListByCurrentDate(String currentDate) {
        MyTaskDatabase database = ((MyTaskApp) getActivity().getApplicationContext()).getDatabase();
        AppExecutors appExecutors = new AppExecutors();
        appExecutors.getExeDiskIO().execute(() -> {
            detailsEntityList = database.getTaskDetailsDao().loadAllTaskDetailsByToday_V1(currentDate);
            if (detailsEntityList != null && detailsEntityList.size() > 0) {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        binding.setIsLoading(false);
                        calenderAllTaskListAdapter_.setFolderList(detailsEntityList);
                    }
                });
            } else {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        binding.setIsLoading(true);
                    }
                });

            }


        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!TasksEventBus.getBus().isRegistered(this))
            TasksEventBus.getBus().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TasksEventBus.getBus().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessage(Events.FragmentToFragment activityMessage) {
        currentDate = activityMessage.getMessage();
        getTaskListByCurrentDate(currentDate);
    }

}
