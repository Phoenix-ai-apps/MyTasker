package com.b2b.mytask.ui.fragment.AddTaskActivity;

/**
 * Created by Nihar.s on 27/6/18.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.mytask.AppExecutors;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.R;
import com.b2b.mytask.adapters.TimeTypeAdapterDay;
import com.b2b.mytask.adapters.TimeTypeAdapterMinutes;
import com.b2b.mytask.adapters.TimeTypeAdapterNight;
import com.b2b.mytask.databinding.FragmentTimeBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.TaskDetailsEntity;
import com.b2b.mytask.interfaces.iTimeSelected;
import com.b2b.mytask.utils.ApplicationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * TimeFragment :-
 * Class specifies two adapters for Day and Night time.
 * stringListTimeDay:- List of all time count for DAY.
 * stringListTimeNight:- List of all time count for NIGHT.
 * timeTypeAdapterDay:- adapter for DAY.
 * timeTypeAdapterDay:- adapter for NIGHT.
 * For Fresh Task, Simply setup adapters without selected element.
 * For Old Task, get Time from sharedPref,if sharedpref is empty then retrieve data from Db by using currentTaskID.
 * Further setups all adapters.
 */
public class TimeFragment extends Fragment implements iTimeSelected {

    List<Integer> stringListTimeDay;

    List<Integer> stringListTimeNight;

    List<Integer> stringListTimeMinutes;

    TimeTypeAdapterDay timeTypeAdapterDay;

    TimeTypeAdapterNight timeTypeAdapterNight;

    TimeTypeAdapterMinutes timeTypeAdapterMinutes;

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    FragmentTimeBinding timeBinding;

    Integer currentTaskID;
    private String from = "", str_time = "";
    private String time = "";
    private String minutes = "";
    private String mEvent = "";
    private boolean isAM = false;
    private String str_hours = "", str_minutes = "", str_am_pm = "";
    private iTimeSelected itimeSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        timeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_time, container, false);

        View rootview = timeBinding.getRoot();

        itimeSelected = this;

        initialiseResource();

        return rootview;
    }

    /**
     * initialization of all used resources.
     * <p>
     * currentTaskID :- for fresh case it won't have any task id. If it's not fresh case, get taskId from sharedPreferences.
     * <p>
     * from :- Start date of task
     * <p>
     * str_time:- on basis of currentTaskID, get time from sharedPreferences. If sharedPreferences is empty get time from DB by currentTaskID.
     */
    private void initialiseResource() {

        sharedPreferences = getActivity().getSharedPreferences(ApplicationUtils.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        from = sharedPreferences.getString(ApplicationUtils.FROM, "");
        currentTaskID = sharedPreferences.getInt(ApplicationUtils.GETTASKID, 0);

    /*    timeBinding.txt0.setOnClickListener(this);
        timeBinding.txt15.setOnClickListener(this);
        timeBinding.txt30.setOnClickListener(this);
        timeBinding.txt45.setOnClickListener(this);*/
        stringListTimeDay = new ArrayList<>();
        stringListTimeNight = new ArrayList<>();
        stringListTimeMinutes = new ArrayList<>();

        timeBinding.recyclerTimeDay.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        timeBinding.recyclerTimeNight.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        timeBinding.recyclerTimeMinutes.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        timeBinding.recyclerTimeNight.setNestedScrollingEnabled(false);
        timeBinding.recyclerTimeDay.setNestedScrollingEnabled(false);
        timeBinding.recyclerTimeMinutes.setNestedScrollingEnabled(false);


        for (int i = 1; i < 13; i++) {
            stringListTimeDay.add(i);
        }

        for (int i = 1; i < 13; i++) {
            stringListTimeNight.add(i);
        }

        for (int i = 0; i < 60; i += 5) {
            //Because we are adding 5 and starting at 0 val must be divisible by 5... unless
            // you modify it inside the loop.
            stringListTimeMinutes.add(i);
        }

        if (currentTaskID != null) {

            if (sharedPreferences.getString(ApplicationUtils.START_TIME, "") != null && !sharedPreferences.getString(ApplicationUtils.START_TIME, "").equalsIgnoreCase("")) {
                str_time = sharedPreferences.getString(ApplicationUtils.START_TIME, "");

                StringTokenizer tokens = new StringTokenizer(str_time, ":");
                String first = tokens.nextToken();
                String second = tokens.nextToken();

                str_hours = first;

                StringTokenizer tokens1 = new StringTokenizer(second, " =,;");
                String first1 = tokens1.nextToken();
                String second1 = tokens1.nextToken();

                str_minutes = first1;
                str_am_pm = second1;

                if (str_am_pm.equalsIgnoreCase("AM")) {
                    storedTimeInPreference(str_hours, str_minutes, true);
                } else {
                    storedTimeInPreference(str_hours, str_minutes, false);
                }
            } else {
                AppExecutors appExecutors = new AppExecutors();
                appExecutors.getExeDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        MyTaskDatabase database = ((MyTaskApp) getActivity().getApplicationContext()).getDatabase();
                        if (database != null) {
                            TaskDetailsEntity taskDetailsEntity = database.getTaskDetailsDao().getTaskDetailsByTaskID(String.valueOf(currentTaskID));

                            if (taskDetailsEntity != null && taskDetailsEntity.getTaskTime() != null) {
                                str_time = taskDetailsEntity.getTaskTime();

                                StringTokenizer tokens = new StringTokenizer(str_time, ":");
                                String first = tokens.nextToken();
                                String second = tokens.nextToken();

                                str_hours = first;

                                StringTokenizer tokens1 = new StringTokenizer(second, " =,;");
                                String first1 = tokens1.nextToken();
                                String second1 = tokens1.nextToken();

                                str_minutes = first1;
                                str_am_pm = second1;

                                if (str_am_pm.equalsIgnoreCase("AM")) {
                                    storedTimeInPreference(str_hours, str_minutes, true);
                                } else {
                                    storedTimeInPreference(str_hours, str_minutes, false);
                                }
                            }
                        }
                    }
                });
            }
        }


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int position = getIndexByHours(str_hours, stringListTimeDay);
                timeTypeAdapterDay = new TimeTypeAdapterDay(getActivity(), stringListTimeDay, itimeSelected, str_hours, str_minutes, str_am_pm);
                timeBinding.recyclerTimeDay.setAdapter(timeTypeAdapterDay);

                timeTypeAdapterNight = new TimeTypeAdapterNight(getActivity(), stringListTimeNight, itimeSelected, str_hours, str_minutes, str_am_pm);
                timeBinding.recyclerTimeNight.setAdapter(timeTypeAdapterNight);

                timeTypeAdapterMinutes = new TimeTypeAdapterMinutes(getActivity(), stringListTimeMinutes, itimeSelected, str_hours, str_minutes, str_am_pm);
                timeBinding.recyclerTimeMinutes.setAdapter(timeTypeAdapterMinutes);

                if (!TextUtils.isEmpty(str_am_pm) && str_am_pm.equalsIgnoreCase("AM")) {
                    timeBinding.recyclerTimeDay.scrollToPosition(position);
                } else {
                    timeBinding.recyclerTimeNight.scrollToPosition(position);
                }


                int positionMinutes = getIndexByHours(str_minutes, stringListTimeMinutes);
                timeBinding.recyclerTimeMinutes.scrollToPosition(positionMinutes);


            }
        }, 500);


    }

    @Override
    public void getSelectedTime(String str_time_day, String isDayOrNight) {
        time = str_time_day;
        str_hours = "";
        str_am_pm = "";
        if (isDayOrNight.equalsIgnoreCase("1")) {
            isAM = true;
            timeTypeAdapterNight = new TimeTypeAdapterNight(getActivity(), stringListTimeNight, this, str_hours, str_minutes, str_am_pm);
            timeBinding.recyclerTimeNight.setAdapter(timeTypeAdapterNight);
        } else if (isDayOrNight.equalsIgnoreCase("0")) {
            isAM = false;
            timeTypeAdapterDay = new TimeTypeAdapterDay(getActivity(), stringListTimeDay, this, str_hours, str_minutes, str_am_pm);
            timeBinding.recyclerTimeDay.setAdapter(timeTypeAdapterDay);

        }

        if (TextUtils.isEmpty(str_minutes)) {
            str_minutes = "00";
            storedTimeInPreference(time, str_minutes, isAM);
        }else {
            storedTimeInPreference(time, str_minutes, isAM);
        }

    }

    /**
     * Stored Time In Preference
     * Ex:- 05:30 PM
     *
     * @param time    :05
     * @param minutes :30
     * @param isAM    :PM
     */
    private void storedTimeInPreference(String time, String minutes, boolean isAM) {
        if (isAM) {
            mEvent = "AM";
        } else {
            mEvent = "PM";
        }
        if (from.equalsIgnoreCase("start")) {
            if (minutes.equalsIgnoreCase("0") || minutes.equalsIgnoreCase("5")) {
                minutes = "0" + minutes;
            }
            editor.putString(ApplicationUtils.START_TIME, time + ":" + minutes + " " + mEvent);
        }

        editor.commit();
    }

    /**
     * Gets index by name.
     * This method gets position of hours in list. i.e 6:30 AM this scenario checks, 6 at what position present in integerList
     *
     * @param str_hours   : hours from DB
     * @param integerList the integer list
     * @return the index by hours
     */
    public int getIndexByHours(String str_hours, List<Integer> integerList) {
        for (Integer _item : integerList) {
            if (_item.toString().equals(str_hours.trim())) {
                return integerList.indexOf(_item);
            }
        }
        return -1;
    }

    @Override
    public void getSelectedMinutes(String minutes) {
        str_minutes = minutes;
        if (!TextUtils.isEmpty(str_hours)) {
            storedTimeInPreference(str_hours, str_minutes, isAM);
        } else if (!TextUtils.isEmpty(time)) {
            storedTimeInPreference(time, str_minutes, isAM);
        }
    }

}