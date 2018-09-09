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
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.mytask.AppExecutors;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.R;
import com.b2b.mytask.adapters.DateTypeAdapter;
import com.b2b.mytask.databinding.FragmentDateBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.TaskDetailsEntity;
import com.b2b.mytask.interfaces.iDateSelected;
import com.b2b.mytask.models.TaskByDate;
import com.b2b.mytask.utils.ApplicationUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * This Fragment adds all dates to bottomsheetview.
 */
public class DateFragment extends Fragment implements View.OnClickListener, iDateSelected {

    /**
     * The Date binding.
     */
    FragmentDateBinding dateBinding;

    /**
     * The Task by date list after.
     */
    List<TaskByDate> taskByDateList_after;

    /**
     * The Date type adapter.
     */
    DateTypeAdapter dateTypeAdapter;

    /**
     * The Shared preferences.
     */
    SharedPreferences sharedPreferences;
    /**
     * The Editor.
     */
    SharedPreferences.Editor editor;

    /**
     * The From.
     */
    String from = "", /**
     * The Str date.
     */
    str_date = "";
    /**
     * The Current task id.
     */
    int currentTaskID;
    private Context context;
    private iDateSelected idateSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        dateBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_date, container, false);

        View rootview = dateBinding.getRoot();

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
     * str_date:- on basis of currentTaskID, get date from sharedPreferences. If sharedPreferences is empty get date from DB by currentTaskID.
     */
    public void initialiseResource() {
        taskByDateList_after = new ArrayList<>();
        idateSelected = this;

        sharedPreferences = getActivity().getSharedPreferences(ApplicationUtils.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        from = sharedPreferences.getString(ApplicationUtils.FROM, "");

        currentTaskID = sharedPreferences.getInt(ApplicationUtils.GETTASKID, 0);

        dateBinding.recyclerDate.setLayoutManager(new GridLayoutManager(getActivity(), 8));

        dateBinding.imgMore.setOnClickListener(this);

        dateBinding.txtTitleDate.setOnClickListener(this);


        if (currentTaskID != 0) {
            if (!sharedPreferences.getString(ApplicationUtils.START_DATE, "").equalsIgnoreCase("")) {
                str_date = sharedPreferences.getString(ApplicationUtils.START_DATE, "");
            } else {
                AppExecutors appExecutors = new AppExecutors();
                appExecutors.getExeDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        MyTaskDatabase database = ((MyTaskApp) getActivity().getApplicationContext()).getDatabase();
                        if (database != null) {
                            TaskDetailsEntity taskDetailsEntity = database.getTaskDetailsDao().getTaskDetailsByTaskID(String.valueOf(currentTaskID));
                            if (taskDetailsEntity != null && taskDetailsEntity.getTaskDate() != null) {
                                str_date = taskDetailsEntity.getTaskDate();
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
                if (TextUtils.isEmpty(str_date)) {

                    List<TaskByDate> taskByDateslst = dateSetup(null, null, null, null, false);
                    if (taskByDateslst.size() > 0) {
                        dateTypeAdapter = new DateTypeAdapter(getActivity(), taskByDateslst, idateSelected, str_date);
                        dateBinding.recyclerDate.setAdapter(dateTypeAdapter);
                    }
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("EEEE dd MMM, yyyy");
                    String day = null, dayOfMonth = null, month = null, year = null;
                    try {
                        Date date = format.parse(str_date);
                        day = (String) DateFormat.format("EE", date);
                        dayOfMonth = (String) DateFormat.format("dd", date);
                        month = (String) DateFormat.format("MM", date);
                        year = (String) DateFormat.format("yyyy", date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    List<TaskByDate> taskByDateslst = dateSetup(year, month, dayOfMonth, day, true);
                    if (taskByDateslst.size() > 0) {
                        dateTypeAdapter = new DateTypeAdapter(getActivity(), taskByDateslst, idateSelected, str_date);
                        dateBinding.recyclerDate.setAdapter(dateTypeAdapter);
                    }
                }

            }
        }, 500);

    }

    /**
     * This method calculates dates
     * <p>
     * ex:- Wed 28-07-2018
     *
     * @param year         :2018
     * @param month        :       07
     * @param dayOfmonth   : 28
     * @param day          : Wed
     * @param fromCalender : App has provision to select date from calender, returns boolean
     * @return : List of all dates, that are calculated either by 1. For fresh Task:- caculates by current date. 2. For task from DB:- calcutes by date present in db
     */
    public List<TaskByDate> dateSetup(String year, String month, String dayOfmonth, String day, boolean fromCalender) {
        taskByDateList_after.clear();
        Calendar cal = Calendar.getInstance();
        if (fromCalender) {
            cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(dayOfmonth));
        }
        Date date = cal.getTime();
        int i = 0;
        TaskByDate taskByDate1 = new TaskByDate();
        taskByDate1.dayOfMonth = "This \nweek";
        taskByDateList_after.add(taskByDate1);
        while (i <= 13) {
            if (i == 7) {
                TaskByDate taskByDate2 = new TaskByDate();
                taskByDate2.dayOfMonth = "Next \nweek";
                taskByDateList_after.add(taskByDate2);
            }

            Date newDate = addDays(date, i);
            TaskByDate taskByDate = new TaskByDate();
            taskByDate.dayOfMonth = (String) DateFormat.format("dd", newDate);
            taskByDate.month = (String) DateFormat.format("MM", newDate);
            taskByDate.year = (String) DateFormat.format("yyyy", newDate);
            taskByDate.day = (String) DateFormat.format("EE", newDate);
            taskByDateList_after.add(taskByDate);
            i++;
        }

        if (from.equalsIgnoreCase("start")) {
            editor.putString(ApplicationUtils.START_DATE, DateFormat.format("EE", date) + " " + DateFormat.format("dd", date) + " " + DateFormat.format("MMM", date) + ", " + DateFormat.format("yyyy", date));
        }
        editor.commit();

        if (str_date != null && !str_date.equalsIgnoreCase("")) {
            dateBinding.txtTitleDate.setText(str_date);
        } else {
            dateBinding.txtTitleDate.setText(DateFormat.format("EE", date) + " " + DateFormat.format("dd", date) + " " + DateFormat.format("MMM", date) + ", " + DateFormat.format("yyyy", date));
        }
        return taskByDateList_after;
    }

    /**
     * add days to date in java
     *
     * @param date :selected date either current date or date from DB
     * @param days : itterator of for loop calulates next day with this number.
     * @return : Next date
     */
    public Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_title_date:
                ApplicationUtils.showDatePicker(getActivity(), this);
                break;
        }
    }


    /**
     * An interface to get selected date from DateTypeAdapter
     * <p>
     * ex:- Wed 28-07-2018
     *
     * @param str_dayofmonth : 28
     * @param str_month      : 07
     * @param str_year       : 2018
     * @param str_day        : Wed
     * @param isFromCalender : App has provision to select date from calender, returns boolean
     */
    @Override
    public void getSelectedDate(String str_dayofmonth, String str_month, String str_year, String str_day, boolean isFromCalender) {
        if (!str_dayofmonth.equalsIgnoreCase("")) {
            if (isFromCalender) {
                String selected_dt = str_dayofmonth + "-" + str_month + "-" + str_year;
                str_date = str_day + " " + ApplicationUtils.formatDate(selected_dt);
                List<TaskByDate> taskByDateslst = dateSetup(str_year, str_month, str_dayofmonth, str_day, true);
                if (taskByDateslst.size() > 0) {
                    dateTypeAdapter = new DateTypeAdapter(getActivity(), taskByDateslst, this, str_date);
                    dateBinding.recyclerDate.setAdapter(dateTypeAdapter);
                }
            } else {
                String selected_dt = str_dayofmonth + "-" + str_month + "-" + str_year;

                if (from.equalsIgnoreCase("start")) {
                    editor.putString(ApplicationUtils.START_DATE, str_day + " " + ApplicationUtils.formatDate(selected_dt));
                } else {
                    editor.putString(ApplicationUtils.END_DATE, str_day + " " + ApplicationUtils.formatDate(selected_dt));
                }
                editor.commit();
                dateBinding.txtTitleDate.setText(str_day + " " + ApplicationUtils.formatDate(selected_dt));

            }
        }
    }

}