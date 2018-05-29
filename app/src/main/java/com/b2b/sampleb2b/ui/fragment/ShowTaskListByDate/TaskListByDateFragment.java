package com.b2b.sampleb2b.ui.fragment.ShowTaskListByDate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.sampleb2b.R;
import com.b2b.sampleb2b.adapters.ShowTaskListAdapter;
import com.b2b.sampleb2b.utils.ApplicationUtils;
import com.b2b.sampleb2b.models.TaskByDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nihar.s on 15/5/18.
 */

public class TaskListByDateFragment extends Fragment {

    List<TaskByDate> taskByDateList_after;
    List<TaskByDate> taskByDateList_before;
    List<TaskByDate> taskByDateList_final;

    // @formatter:off
      @BindView(R.id.recyclerview)           RecyclerView recyclerview;
  // @formatter:on

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        ButterKnife.bind(this, rootView);

        initialiseResource();

        return rootView;


    }

    public void initialiseResource() {

        taskByDateList_after = new ArrayList<>();
        taskByDateList_before = new ArrayList<>();
        taskByDateList_final = new ArrayList<>();

        List<TaskByDate> allTaskList = dateSetup();

        if (allTaskList != null) {

            if (allTaskList.size() > 0) {
                ShowTaskListAdapter adapter = new ShowTaskListAdapter(getActivity(), allTaskList);

                recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

                recyclerview.setAdapter(adapter);

                recyclerview.scrollToPosition(29);

            }
        }


    }

    public List<TaskByDate> dateSetup() {
        Date date = new Date();

        int i = 0;
        while (i <= 30) {
            Date newDate = ApplicationUtils.subtractDays(date, i);
            TaskByDate taskByDate = new TaskByDate();
            taskByDate.month = (String) DateFormat.format("MMM", newDate);
            taskByDate.day = (String) DateFormat.format("dd", newDate);
            taskByDateList_before.add(taskByDate);
            i++;
        }
        i = 1;
        while (i <= 30) {
            Date newDate = ApplicationUtils.addDays(date, i);
            TaskByDate taskByDate = new TaskByDate();
            taskByDate.month = (String) DateFormat.format("MMM", newDate);
            taskByDate.day = (String) DateFormat.format("dd", newDate);
            taskByDateList_after.add(taskByDate);
            i++;
        }

        Collections.reverse(taskByDateList_before);
        taskByDateList_final.addAll(taskByDateList_before);
        taskByDateList_final.addAll(taskByDateList_after);
        return taskByDateList_final;

    }

}
