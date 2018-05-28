package com.b2b.sampleb2b.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.b2b.sampleb2b.R;
import com.b2b.sampleb2b.adapters.FilterBottomDialogAdapter;
import com.b2b.sampleb2b.constants.AllConstants;
import com.b2b.sampleb2b.utils.ApplicationUtils;
import com.b2b.sampleb2b.receiver.TaskAlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 20/4/18.
 */

public class AddTaskActivity extends AppCompatActivity implements AllConstants, View.OnClickListener {

    private static final String TAG = "AddTaskActivity";
    //@formatter:off

    @BindView(R.id.txt_meeting_date)         AppCompatTextView  txtMeetingDate;
    @BindView(R.id.txt_meeting_time)         AppCompatTextView  txtMeetingTime;
    @BindView(R.id.txt_task_name)            AppCompatTextView  txtTaskName;
    @BindView(R.id.txt_repeat_mode)          AppCompatTextView  txtRepeatMode;
    @BindView(R.id.priority_none)            AppCompatTextView  priorityNone;
    @BindView(R.id.priority_one)             AppCompatTextView  priorityOne;
    @BindView(R.id.priority_two)             AppCompatTextView  priorityTwo;
    @BindView(R.id.priority_three)           AppCompatTextView  priorityThree;
    @BindView(R.id.txt_repeatalarm_date)     AppCompatTextView  txtRepeatalarmDate;
    @BindView(R.id.tv_cancel)                AppCompatTextView  tvCancel;
    @BindView(R.id.tv_save)                  AppCompatTextView  tvSave;
    @BindView(R.id.title_dialog)             AppCompatTextView  titleBottomDialog;
    @BindView(R.id.img_clear_date)           AppCompatImageView imgCleaDate;
    @BindView(R.id.img_clear_time)           AppCompatImageView imgClearTime;
    @BindView(R.id.layout_repeat)            ConstraintLayout   layoutRepeat;
    @BindView(R.id.layout_moveto)            ConstraintLayout   layoutMoveto;
    @BindView(R.id.recyclerview)             RecyclerView       recyclerview;
    @BindView(R.id.edt_task_note)            AppCompatEditText  edtTaskNote;

    //@formatter:on

    private BottomSheetBehavior mBottomSheetBehavior;
    private List<String> listFilter;
    private LinearLayoutManager mLayoutManager;
    private int prorityType;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        ButterKnife.bind(this);

        initializeResources();
    }

    private void initializeResources() {

        Bundle bundle = this.getIntent().getExtras();
        String title = bundle.getString(TITLE);
        txtTaskName.setText(title);

        tvCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        txtMeetingDate.setOnClickListener(this);
        txtMeetingTime.setOnClickListener(this);
        imgClearTime.setOnClickListener(this);
        imgCleaDate.setOnClickListener(this);
        priorityNone.setOnClickListener(this);
        priorityOne.setOnClickListener(this);
        priorityTwo.setOnClickListener(this);
        priorityThree.setOnClickListener(this);
        layoutRepeat.setOnClickListener(this);
        layoutMoveto.setOnClickListener(this);


        //Find bottom Sheet ID
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);


//By default set BottomSheet Behavior as Collapsed and Height 0
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setPeekHeight(0);

//If you want to handle callback of Sheet Behavior you can use below code
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.d(TAG, "State Collapsed");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.d(TAG, "State Dragging");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.d(TAG, "State Expanded");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d(TAG, "State Hidden");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.d(TAG, "State Settling");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        listFilter = new ArrayList<>();
        listFilter.add("Filter 1");
        listFilter.add("Filter 2");
        listFilter.add("Filter 3");
        listFilter.add("Filter 4");
        listFilter.add("Filter 5");
        listFilter.add("Filter 6");
        listFilter.add("Filter 1");
        listFilter.add("Filter 2");
        listFilter.add("Filter 3");
        listFilter.add("Filter 4");
        listFilter.add("Filter 5");
        listFilter.add("Filter 6");


        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.txt_meeting_date:
                ApplicationUtils.showDatePicker(AddTaskActivity.this, txtMeetingDate);
                break;

            case R.id.txt_meeting_time:
                ApplicationUtils.showTimePicker(AddTaskActivity.this, txtMeetingTime, txtRepeatalarmDate);
                break;


            case R.id.img_clear_date:
                txtMeetingDate.setText(R.string.select_date);
                txtRepeatalarmDate.setVisibility(View.GONE);
                break;

            case R.id.img_clear_time:
                txtMeetingTime.setText(R.string.select_time);
                txtRepeatalarmDate.setVisibility(View.GONE);
                break;


            case R.id.priority_none:
                prorityType = 0;
                priorityNone.setTextColor(ContextCompat.getColor(this, R.color.white));
                priorityNone.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_left_rounded_corners_click));

                priorityOne.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
                priorityOne.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_square));
                priorityTwo.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
                priorityTwo.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_square));
                priorityThree.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
                priorityThree.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_right_rounded_corners));
                break;

            case R.id.priority_one:
                prorityType = 1;
                priorityOne.setTextColor(ContextCompat.getColor(this, R.color.white));
                priorityOne.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_square_click));

                priorityNone.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
                priorityNone.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_left_rounded_corners));
                priorityTwo.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
                priorityTwo.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_square));
                priorityThree.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
                priorityThree.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_right_rounded_corners));
                break;

            case R.id.priority_two:
                prorityType = 2;
                priorityTwo.setTextColor(ContextCompat.getColor(this, R.color.white));
                priorityTwo.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_square_click));

                priorityNone.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
                priorityNone.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_left_rounded_corners));
                priorityOne.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
                priorityOne.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_square));
                priorityThree.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
                priorityThree.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_right_rounded_corners));

                break;

            case R.id.priority_three:
                prorityType = 3;
                priorityThree.setTextColor(ContextCompat.getColor(this, R.color.white));
                priorityThree.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_right_rounded_corners_click));

                priorityNone.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
                priorityNone.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_left_rounded_corners));
                priorityOne.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
                priorityOne.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_square));
                priorityTwo.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
                priorityTwo.setBackground(ContextCompat.getDrawable(this, R.drawable.segment_square));
                break;


            case R.id.layout_repeat:
                titleBottomDialog.setText(R.string.select_repeat);
                titleBottomDialog.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_repeat, 0, 0, 0);

                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    FilterBottomDialogAdapter filterBottomDialogAdapter = new FilterBottomDialogAdapter(this, listFilter);
                    recyclerview.setAdapter(filterBottomDialogAdapter);
                    recyclerview.setNestedScrollingEnabled(false);
                    filterBottomDialogAdapter.notifyDataSetChanged();
                    //If state is in collapse mode expand it
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    //else if state is expanded collapse it
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }

                break;


            case R.id.layout_moveto:
                titleBottomDialog.setText(R.string.select_move_to);
                titleBottomDialog.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_folder_stroke, 0, 0, 0);

                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    FilterBottomDialogAdapter filterBottomDialogAdapter = new FilterBottomDialogAdapter(this, listFilter);
                    recyclerview.setAdapter(filterBottomDialogAdapter);
                    recyclerview.setNestedScrollingEnabled(false);
                    filterBottomDialogAdapter.notifyDataSetChanged();

                    //If state is in collapse mode expand it
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    //else if state is expanded collapse it
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
                break;

            case R.id.tv_save:

                if (txtMeetingDate.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.select_date))) {
                    Toast.makeText(this, "Enter Meeting Date", Toast.LENGTH_SHORT).show();
                } else if (txtMeetingTime.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.select_time))) {
                    Toast.makeText(this, "Enter Meeting Time", Toast.LENGTH_SHORT).show();
                } else if (txtRepeatMode.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.select_repeat_mode))) {
                    Toast.makeText(this, "Select task repeat mode", Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(edtTaskNote.getText().toString().trim())) {
                    Toast.makeText(this, "Enter task note", Toast.LENGTH_SHORT).show();
                } else if (prorityType == 0) {
                    Toast.makeText(this, "Select prority type", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tv_cancel:
                ApplicationUtils.showDialog(AddTaskActivity.this, getString(R.string.lbl_exit_task_message));
                break;
        }
    }

    private void setTaskAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(AddTaskActivity.this, TaskAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, 2);
        alarmStartTime.set(Calendar.MINUTE, 13);
        alarmStartTime.set(Calendar.SECOND, 00);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), 0, pendingIntent);

    }

    /**
     * Generate UUID for every alarmtask-->Save in DB --> get id from DB pass it while Notification generation-->
     * onClick notification get that ID and pass in RequestCode.
     */
    private void cancelTaskAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(AddTaskActivity.this, TaskAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

    }


}
