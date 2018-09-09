package com.b2b.mytask.ui;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.b2b.mytask.AppExecutors;
import com.b2b.mytask.DataRepository;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.R;
import com.b2b.mytask.adapters.FilterBottomDialogAdapter;
import com.b2b.mytask.adapters.ViewPagerAdapter;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.customviews.FlowLayout;
import com.b2b.mytask.databinding.ActivityAddTaskBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.db.entities.TaskDetailsEntity;
import com.b2b.mytask.interfaces.iBottomSheetMoveTo;
import com.b2b.mytask.models.AddTaskDetails;
import com.b2b.mytask.models.MeetingPeopleDO;
import com.b2b.mytask.ui.fragment.AddTaskActivity.DateFragment;
import com.b2b.mytask.ui.fragment.AddTaskActivity.MoveFolderFragment;
import com.b2b.mytask.ui.fragment.AddTaskActivity.TimeFragment;
import com.b2b.mytask.utils.ApplicationUtils;
import com.b2b.mytask.utils.ObjectUtils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener, AllConstants, iBottomSheetMoveTo {

    private static final String TAG = "AddTaskActivity";
    public int CURR_FOLDER_ID = 0;
    public int CURR_FOLDER_ID_MOVETO = 0;
    ActivityAddTaskBinding binding;
    String[] alertBeforeArray = new String[]{EVERY_DAY, EVERY_WEEK, EVERY_MONTH, EVERY_YEAR, NEVER};
    List<String> alertBeforeList = new ArrayList<>();

    int fillColor;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<MeetingPeopleDO> selectedPeople;
    TextView txt_chips_location, txt_chips_alert_before;
    List<TextView> textViewList_location, textViewList_alert_before;
    FlowLayout.LayoutParams flowLayout;
    private float[] yData = {50, 50, 50, 50};
    private String[] xData = {"Yellow", "Blue", "Green", "Red"};
    private String from = "", priorityName = "";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BottomSheetBehavior mBottomSheetDateTime;
    private BottomSheetBehavior mBottomSheetMoveTo;
    private int lastClicked_location = -1, lastClicked_alert = -1;
    private int expand_collapse_flag_location = 1, expand_collapse_flag_alert = 1;
    private FolderEntity folderEntity = null;
    private FolderEntity folderEntity_moveto = null;
    private AddTaskDetails addTaskDetails = null;
    private int prorityType = 0;
    private MyTaskDatabase database;
    private boolean isFromDB;
    private List<String> listFilter;
    private AppExecutors appExecutors;
    private FilterBottomDialogAdapter filterBottomDialogAdapter;
    private int moveToFolderId = -1;
    private boolean isFreshCase = false;
    private List<TaskDetailsEntity> cancelAlarmList;
    private boolean isFromNotification = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_task);

        initialization();
    }

    /*
    *initialization of all used resources.
    *From Bundle , taskName and folderEntity objects are retrieved.
    * */
    private void initialization() {
        Bundle bundle = this.getIntent().getExtras();
        String taskName = "";
        if (bundle != null) {
            if (bundle.containsKey(TITLE)) {
                taskName = bundle.getString(TITLE);
                binding.setTaskName(taskName);
            }
            if (bundle.containsKey(FOLDER_OBJ)) {
                folderEntity = bundle.getParcelable(FOLDER_OBJ);
                getDataForTask(folderEntity, bundle);
                setUpChartonThread();
                isFromNotification = false;
            }

        }

        /*
        * Below code used When notification arrived for specific task,
        * on click of notification app opens n lands on this page.
        * onCLick of notification in setAction taskId is sent, on that basis data is retrived from Db.
        * */
        String taskId = this.getIntent().getAction();
        if (!TextUtils.isEmpty(taskId)) {
            AppExecutors appExecutors = new AppExecutors();
            appExecutors.getExeDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    isFromNotification = true;
                    MyTaskDatabase database = ((MyTaskApp) getApplicationContext()).getDatabase();
                    folderEntity = database.getFolderDao().getFolderDataById(Integer.parseInt(taskId));
                    getDataForTask(folderEntity, null);
                    setUpChartonThread();
                }
            });
        }


        appExecutors = new AppExecutors();
        textViewList_location = new ArrayList<>();
        cancelAlarmList = new ArrayList<>();
        textViewList_alert_before = new ArrayList<>();

        sharedPreferences = getSharedPreferences(ApplicationUtils.MY_PREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        binding.linearPriority.setOnClickListener(AddTaskActivity.this);
        binding.layoutMoveTo.setOnClickListener(AddTaskActivity.this);
        binding.tvSave.setOnClickListener(AddTaskActivity.this);
        binding.dateTimePicker.tvDone.setOnClickListener(AddTaskActivity.this);
        binding.bckView.setOnClickListener(AddTaskActivity.this);
        binding.linearSelectDt.setOnClickListener(AddTaskActivity.this);
        binding.selectAlertBefore.setOnClickListener(AddTaskActivity.this);
        binding.layoutMovetoHeader.setOnClickListener(AddTaskActivity.this);
        binding.imgBackArrow.setOnClickListener(AddTaskActivity.this);
        binding.btnMoveTo.setOnClickListener(AddTaskActivity.this);

        /*BottomSheetView For Date and Time Picker*/
        mBottomSheetDateTime = BottomSheetBehavior.from(binding.bottomSheetDatetimepicker);
        mBottomSheetDateTime.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetDateTime.setPeekHeight(0);

        /*BottomSheetView For MoveTo , Move task from one folder to other folder*/
        mBottomSheetMoveTo = BottomSheetBehavior.from(binding.bottomSheetMoveto);
        mBottomSheetMoveTo.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetMoveTo.setPeekHeight(0);

        flowLayout = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        flowLayout.setMargins(5, 5, 5, 5);

        for (int i = 0; i < alertBeforeArray.length; i++) {
            alertBeforeList.add(alertBeforeArray[i]);
        }


        if (addTaskDetails != null && (addTaskDetails.getTaskRepeatMode() != null || !addTaskDetails.getTaskRepeatMode().equalsIgnoreCase(""))) {
            addAlertBefore(addTaskDetails.getTaskRepeatMode());
        } else {
            addAlertBefore("");
        }

        expand_collapse_flag_alert = ApplicationUtils.collapse(binding.chipsBoxAlertBefore, expand_collapse_flag_alert);

        //If you want to handle callback of Sheet Behavior you can use below code
        mBottomSheetMoveTo.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        // dynamic for MOVE TO
        listFilter = new ArrayList<>();
        filterBottomDialogAdapter = new FilterBottomDialogAdapter(this, listFilter);
        binding.incFilter.cmnRecView.recyclerview.setAdapter(filterBottomDialogAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        binding.incFilter.cmnRecView.recyclerview.setLayoutManager(layoutManager);

    }


    /**
     * This method setups Activity, once data received from Bundle.
     * <p>
     * isFreshCase:- boolean param. decided task is fresh or old task from DB.
     *
     * @param folderEntity : Object received from bundle.
     * @param bundle       :- Complete bundle object.
     */
    private void getDataForTask(FolderEntity folderEntity, Bundle bundle) {
        if (folderEntity.getTaskDetails() != null) {
            CURR_FOLDER_ID = folderEntity.getId();
            addTaskDetails = folderEntity.getTaskDetails();
            String getFormatedDate = ApplicationUtils.formatDateAllTask(addTaskDetails.getTaskDate(), 1);
            addTaskDetails.setTaskDate(getFormatedDate);
            binding.setTaskDetails(addTaskDetails);
            isFreshCase = false;
            if (bundle != null) {
                if (bundle.containsKey(getString(R.string.parent_column))) {
                    String parentCol = bundle.getString(getString(R.string.parent_column));
                    if (TextUtils.isEmpty(addTaskDetails.getParentColumn())) {
                        addTaskDetails.setParentColumn(parentCol);
                    }
                }
            }
            prorityType = Integer.parseInt(addTaskDetails.getTaskPriority());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Stuff that updates the UI
                    binding.edtTaskHeading.setEnabled(false);
                }
            });

        } else {
            isFreshCase = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Stuff that updates the UI
                    binding.edtTaskHeading.setEnabled(true);
                }
            });

        }
    }

    /**
     * This method setup pie chart depending on fresh or old task.
     */
    public void setUpChartonThread() {
        // Get a handler that can be used to post to the main thread
        Handler mainHandler = new Handler(this.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                if (addTaskDetails != null) {
                    binding.setTaskDetails(addTaskDetails);
                    binding.pieChart.spin(600, -180, 180f, Easing.EasingOption.EaseInBack);
                    binding.pieChart.setVisibility(View.GONE);
                    binding.priorityIndicator.setVisibility(View.GONE);
                    binding.linearPriority.setVisibility(View.VISIBLE);
                    binding.txtPriorityName.setVisibility(View.VISIBLE);
                    String color_id = xData[Integer.parseInt(addTaskDetails.getTaskPriority()) - 1];
                    if (color_id.equalsIgnoreCase(getString(R.string.priority_yellow))) {
                        priorityName = getString(R.string.priority_level_low);
                        fillColor = getResources().getColor(R.color.yellow_500);
                    } else if (color_id.equalsIgnoreCase(getString(R.string.priority_blue))) {
                        priorityName = getString(R.string.priority_level_medium);
                        fillColor = getResources().getColor(R.color.blue_500);
                    } else if (color_id.equalsIgnoreCase(getString(R.string.priority_green))) {
                        priorityName = getString(R.string.priority_level_high);
                        fillColor = getResources().getColor(R.color.green_500);
                    } else if (color_id.equalsIgnoreCase(getString(R.string.priority_red))) {
                        priorityName = getString(R.string.priority_level_very_high);
                        fillColor = getResources().getColor(R.color.red_500);
                    }

                    GradientDrawable gD = new GradientDrawable();
                    gD.setColor(fillColor);
                    gD.setShape(GradientDrawable.OVAL);
                    binding.linearPriority.setBackground(gD);
                    binding.txtPriorityName.setText(priorityName);
                    isFromDB = true;

                } else {
                    binding.linearPriority.setVisibility(View.GONE);
                    binding.txtPriorityName.setVisibility(View.GONE);
                    setUpPiechart(addTaskDetails);
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    private void addAlertBefore(String str_db_alert) {
        for (int i = 0; i < alertBeforeArray.length; i++) {
            txt_chips_alert_before = new TextView(this);
            txt_chips_alert_before.setLayoutParams(flowLayout);
            txt_chips_alert_before.setPadding(Math.round(getResources().getDimension(R.dimen._15sdp)), Math.round(getResources().getDimension(R.dimen._10sdp)), Math.round(getResources().getDimension(R.dimen._15sdp)), Math.round(getResources().getDimension(R.dimen._10sdp)));
            txt_chips_alert_before.setText(alertBeforeArray[i]);
            if (str_db_alert != null && !str_db_alert.equalsIgnoreCase("") && str_db_alert.equalsIgnoreCase(alertBeforeArray[i])) {
                txt_chips_alert_before.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + getString(R.string.regular_font)));
                txt_chips_alert_before.setTextColor(getResources().getColor(R.color.black));
                txt_chips_alert_before.setBackground(getResources().getDrawable(R.drawable.outline_rounded_purple));
                lastClicked_alert = i;
            } else {
                txt_chips_alert_before.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + getString(R.string.regular_font)));
                txt_chips_alert_before.setTextColor(getResources().getColor(R.color.black));
                txt_chips_alert_before.setBackground(getResources().getDrawable(R.drawable.outline_rounded_purple));
            }
            binding.chipsBoxAlertBefore.addView(txt_chips_alert_before);

            textViewList_alert_before.add(txt_chips_alert_before);
            final int finalI = i;
            txt_chips_alert_before.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastClicked_alert != finalI) {
                        textViewList_alert_before.get(finalI).setBackground(getResources().getDrawable(R.drawable.outline_rounded_purple));
                        binding.selectAlertBefore.setText(textViewList_alert_before.get(finalI).getText().toString().trim());
                        binding.selectAlertBefore.setBackground(getResources().getDrawable(R.drawable.outline_rounded_purple));
                        binding.selectAlertBefore.setTextColor(getResources().getColor(R.color.txt_color));
                        ApplicationUtils.setTextViewDrawableColor(binding.selectAlertBefore, getResources().getColor(R.color.purple_500));
                        textViewList_alert_before.get(finalI).setTextColor(getResources().getColor(R.color.txt_color));

                        if (lastClicked_alert != -1) {
                            textViewList_alert_before.get(lastClicked_alert).setTextColor(getResources().getColor(R.color.txt_color));
                            textViewList_alert_before.get(lastClicked_alert).setBackground(getResources().getDrawable(R.drawable.outline_rounded_purple));
                        }

                        lastClicked_alert = finalI;
                        expand_collapse_flag_alert = ApplicationUtils.collapse(binding.chipsBoxAlertBefore, expand_collapse_flag_alert);
                    } else {
                        expand_collapse_flag_alert = ApplicationUtils.collapse(binding.chipsBoxAlertBefore, expand_collapse_flag_alert);

                    }
                }
            });

        }

    }

    public void closeScreen(View view) {
        AddTaskActivity.this.finish();
        sharedPreferences.edit().remove(ApplicationUtils.START_TIME).apply();
        sharedPreferences.edit().remove(ApplicationUtils.START_DATE).apply();
        sharedPreferences.edit().remove(ApplicationUtils.GETTASKID).apply();

        //overridePendingTransition(R.anim.slide_out_up, R.anim.slide_in_up);
    }

    private void setUpPiechart(AddTaskDetails addTaskDetails) {
        binding.pieChart.setVisibility(View.VISIBLE);
        binding.priorityIndicator.setVisibility(View.VISIBLE);
        binding.pieChart.setDescription(null);
        binding.pieChart.setRotationEnabled(true);
        binding.pieChart.setHoleRadius(35f);
        binding.pieChart.setTransparentCircleAlpha(0);
        binding.pieChart.setCenterText("X");
        binding.pieChart.setCenterTextSize(15);
        binding.pieChart.setCenterTextColor(getResources().getColor(R.color.txt_color));
        binding.pieChart.getLegend().setEnabled(false);   // Hide the legend
        binding.pieChart.spin(600, 180, -180f, Easing.EasingOption.EaseInOutQuad);

        addPriorityPieChart();

        if (isFromDB) {
            if (addTaskDetails != null && addTaskDetails.getTaskPriority() != null) {
                prorityType = Integer.parseInt(addTaskDetails.getTaskPriority()) - 1;
                binding.pieChart.highlightValue(prorityType, 0, false);
                addTaskDetails.setTaskPriority(String.valueOf(prorityType));
            }
        }

        binding.pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                final PieEntry pe = (PieEntry) e;
                fillColor = getResources().getColor(R.color.green_500);
                isFromDB = false;
                binding.pieChart.spin(600, -180, 180f, Easing.EasingOption.EaseInBack);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.pieChart.setVisibility(View.GONE);
                        binding.priorityIndicator.setVisibility(View.GONE);
                        binding.linearPriority.setVisibility(View.VISIBLE);
                        binding.txtPriorityName.setVisibility(View.VISIBLE);
                        if (pe.getLabel().equalsIgnoreCase(getString(R.string.priority_yellow))) {
                            prorityType = PRIORITY_TYPE_LOW;
                            priorityName = getString(R.string.priority_level_low);
                            fillColor = getResources().getColor(R.color.yellow_500);
                        } else if (pe.getLabel().equalsIgnoreCase(getString(R.string.priority_blue))) {
                            prorityType = PRIORITY_TYPE_MEDIUM;
                            priorityName = getString(R.string.priority_level_medium);
                            fillColor = getResources().getColor(R.color.blue_500);
                        } else if (pe.getLabel().equalsIgnoreCase(getString(R.string.priority_green))) {
                            prorityType = PRIORITY_TYPE_HIGH;
                            priorityName = getString(R.string.priority_level_high);
                            fillColor = getResources().getColor(R.color.green_500);
                        } else if (pe.getLabel().equalsIgnoreCase(getString(R.string.priority_red))) {
                            prorityType = PRIORITY_TYPE_VERY_HIGH;
                            priorityName = getString(R.string.priority_level_very_high);
                            fillColor = getResources().getColor(R.color.red_500);
                        }
                        GradientDrawable gD = new GradientDrawable();
                        gD.setColor(fillColor);
                        gD.setShape(GradientDrawable.OVAL);
                        binding.linearPriority.setBackground(gD);
                        binding.txtPriorityName.setText(priorityName);

                    }
                }, 650);

            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void addPriorityPieChart() {
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for (int i = 0; i < yData.length; i++) {
            yEntrys.add(new PieEntry(yData[i], i));
        }

        for (int i = 1; i < xData.length; i++) {
            xEntrys.add(xData[i]);
        }

        ArrayList<PieEntry> values = new ArrayList<>();

        values.add(new PieEntry(50f, xData[0]));
        values.add(new PieEntry(50f, xData[1]));
        values.add(new PieEntry(50f, xData[2]));
        values.add(new PieEntry(50f, xData[3]));


        //create the data set
        PieDataSet pieDataSet = new PieDataSet(values, "");
        pieDataSet.setSliceSpace(1);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setAutomaticallyDisableSliceSpacing(true);
        pieDataSet.setDrawValues(false);


        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.yellow_500));
        colors.add(getResources().getColor(R.color.blue_500));
        colors.add(getResources().getColor(R.color.green_500));
        colors.add(getResources().getColor(R.color.red_500));

        pieDataSet.setColors(colors);
        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        binding.pieChart.setData(pieData);
        binding.pieChart.setEntryLabelColor(getResources().getColor(R.color.colorTransparent));

        binding.pieChart.invalidate();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.linear_priority:
                ApplicationUtils.hideKeyboard(this);
                mBottomSheetDateTime.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetMoveTo.setState(BottomSheetBehavior.STATE_COLLAPSED);
                binding.linearPriority.setVisibility(View.GONE);
                binding.txtPriorityName.setVisibility(View.GONE);
                setUpPiechart(addTaskDetails);

                break;


            case R.id.tv_done:
                from = sharedPreferences.getString(ApplicationUtils.FROM, "");

                if (from.equalsIgnoreCase(getResources().getString(R.string.start))) {
                    if (!TextUtils.isEmpty(sharedPreferences.getString(ApplicationUtils.START_TIME, ""))) {
                        StringTokenizer tokens = new StringTokenizer(sharedPreferences.getString(ApplicationUtils.START_TIME, ""), " =,;");
                        String hours = tokens.nextToken();
                        String minutes = tokens.nextToken();
                        StringTokenizer tokens1 = new StringTokenizer(hours, " =,;:");
                        String first_hours = tokens1.nextToken();
                        if (first_hours.equalsIgnoreCase("00") || first_hours.equalsIgnoreCase("15")
                                || first_hours.equalsIgnoreCase("30")
                                || first_hours.equalsIgnoreCase("45")
                                || minutes.equalsIgnoreCase(":00")
                                || minutes.equalsIgnoreCase(":15")
                                || minutes.equalsIgnoreCase(":30") || minutes.equalsIgnoreCase(":45")) {
                            ApplicationUtils.customToast(this, getString(R.string.please_select_time));
                        } else {
                            binding.edtSelectedDate.setHint(sharedPreferences.getString(ApplicationUtils.START_DATE, ""));
                            binding.edtSelectedTime.setText(sharedPreferences.getString(ApplicationUtils.START_TIME, ""));
                            if (mBottomSheetDateTime.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                mBottomSheetDateTime.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                binding.bckView.setVisibility(View.GONE);
                                binding.tvSave.setText(R.string.save);
                            }
                            sharedPreferences.edit().remove(ApplicationUtils.START_DATE).apply();
                            sharedPreferences.edit().remove(ApplicationUtils.START_TIME).apply();
                        }

                    } else {
                        ApplicationUtils.customToast(this, getString(R.string.please_select_time));
                    }
                }


                break;

            case R.id.bck_view:
                ApplicationUtils.hideKeyboard(this);
                mBottomSheetDateTime.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetMoveTo.setState(BottomSheetBehavior.STATE_COLLAPSED);
                binding.bckView.setVisibility(View.GONE);
                mBottomSheetDateTime.setState(BottomSheetBehavior.STATE_COLLAPSED);

                break;

            case R.id.select_alert_before:
                ApplicationUtils.hideKeyboard(this);
                mBottomSheetDateTime.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetMoveTo.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (expand_collapse_flag_alert == 1) {
                    expand_collapse_flag_alert = ApplicationUtils.expand(binding.chipsBoxAlertBefore, expand_collapse_flag_alert);

                } else {
                    expand_collapse_flag_alert = ApplicationUtils.collapse(binding.chipsBoxAlertBefore, expand_collapse_flag_alert);
                }

                break;

            case R.id.linear_select_dt:
                sharedPreferences.edit().remove(ApplicationUtils.START_DATE).apply();
                sharedPreferences.edit().remove(ApplicationUtils.START_TIME).apply();
                ApplicationUtils.hideKeyboard(this);
                mBottomSheetDateTime.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetMoveTo.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (addTaskDetails != null && addTaskDetails.getTaskId() != 0) {
                    editor.putInt(ApplicationUtils.GETTASKID, addTaskDetails.getTaskId());
                } else {
                    editor.putInt(ApplicationUtils.GETTASKID, 0);
                }
                editor.putString(ApplicationUtils.FROM, getResources().getString(R.string.start));
                if (!binding.edtSelectedTime.getText().toString().trim().equalsIgnoreCase("") && !binding.edtSelectedDate.getHint().toString().equalsIgnoreCase("")) {
                    editor.putString(ApplicationUtils.START_DATE, binding.edtSelectedDate.getHint().toString());
                    editor.putString(ApplicationUtils.START_TIME, binding.edtSelectedTime.getText().toString().trim());
                }

                editor.commit();

                binding.dateTimePicker.tvTitle.setText(getString(R.string.select_date_time));

                openDateNTimePicker();
                break;

            case R.id.layout_move_to:
                ApplicationUtils.hideKeyboard(this);
                binding.layoutMovetoHeader.setVisibility(View.VISIBLE);
                setUpBottomSheetAdapter(MOVE_TO);
                binding.bckView.setVisibility(View.VISIBLE);
                if (mBottomSheetMoveTo.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    filterBottomDialogAdapter.notifyDataSetChanged();
                    //If state is in collapse mode expand it
                    mBottomSheetMoveTo.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    //else if state is expanded collapse it
                    binding.bckView.setVisibility(View.GONE);
                    mBottomSheetMoveTo.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;


            case R.id.layout_moveto_header:
                break;

            case R.id.img_back_arrow:
                checkCurrentFolder();

                break;

            case R.id.btn_move_to:
                moveTaskToFolder();
                break;


            case R.id.tv_save:

                ApplicationUtils.hideKeyboard(this);
                mBottomSheetDateTime.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetMoveTo.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (binding.edtTaskHeading.getText().toString().trim().equalsIgnoreCase("")) {
                    ApplicationUtils.customToast(AddTaskActivity.this, getResources().getString(R.string.enter_task_name));
                    binding.edtTaskHeading.requestFocus();
                    binding.nestedscroll.scrollTo(0, 0);
                } else if (binding.edtSelectedTime.getText().toString().trim().equalsIgnoreCase("")) {
                    ApplicationUtils.customToast(AddTaskActivity.this, getString(R.string.select_task_date));
                } else if (prorityType == 0) {
                    ApplicationUtils.customToast(AddTaskActivity.this, getString(R.string.please_select_priority));
                } else if (binding.selectAlertBefore.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.select))) {
                    ApplicationUtils.customToast(AddTaskActivity.this, getString(R.string.please_select_alert_reminder));
                } else if (binding.edtTaskDescription.getText().toString().trim().equalsIgnoreCase("")) {
                    ApplicationUtils.customToast(AddTaskActivity.this, getString(R.string.enter_task_description));
                    binding.edtTaskDescription.requestFocus();
                } else {

                    if (addTaskDetails == null) {
                        addTaskDetails = new AddTaskDetails();
                    }
                    if (folderEntity == null) {
                        folderEntity = new FolderEntity();
                    }

                    addTaskDetails.setTaskName(binding.edtTaskHeading.getText().toString().trim());
                    addTaskDetails.setTaskDate(ApplicationUtils.formatDateAllTask(binding.edtSelectedDate.getHint().toString().trim(), 0));
                    addTaskDetails.setNotificationDate(ApplicationUtils.formatDateAllTask(binding.edtSelectedDate.getHint().toString().trim(), 0));
                    addTaskDetails.setTaskTime(binding.edtSelectedTime.getText().toString().trim());
                    addTaskDetails.setTaskRepeatMode(binding.selectAlertBefore.getText().toString().trim());
                    addTaskDetails.setTaskNote(binding.edtTaskDescription.getText().toString().trim());
                    addTaskDetails.setTaskPriority(String.valueOf(prorityType));
                    addTaskDetails.setTaskFinishStatus(DEFAULT_TASK_STATUS);


                    TaskDetailsEntity taskDetailsEntity = new TaskDetailsEntity();
                    taskDetailsEntity.setTaskName(binding.edtTaskHeading.getText().toString().trim());
                    taskDetailsEntity.setTaskDate(ApplicationUtils.formatDateAllTask(binding.edtSelectedDate.getHint().toString().trim(), 0));
                    taskDetailsEntity.setNotificationDate(ApplicationUtils.formatDateAllTask(binding.edtSelectedDate.getHint().toString().trim(), 0));
                    taskDetailsEntity.setTaskTime(binding.edtSelectedTime.getText().toString().trim());
                    taskDetailsEntity.setTaskRepeatMode(binding.selectAlertBefore.getText().toString().trim());
                    taskDetailsEntity.setTaskNote(binding.edtTaskDescription.getText().toString().trim());
                    taskDetailsEntity.setTaskPriority(String.valueOf(prorityType));
                    taskDetailsEntity.setTaskFinishStatus(DEFAULT_TASK_STATUS);


                    MyTaskDatabase database = ((MyTaskApp) getApplicationContext()).getDatabase();
                    AppExecutors appExecutors = new AppExecutors();
                    appExecutors.getExeDiskIO().execute(() -> {
                        TaskDetailsEntity entityFromDB = null;
                        entityFromDB =
                                DataRepository.getDataRepository(database).getTaskByName(binding.edtTaskHeading.getText().toString().trim(), addTaskDetails.getParentColumn());

                        if (isFreshCase) {
                            entityFromDB =
                                    DataRepository.getDataRepository(database).getTaskByName(binding.edtTaskHeading.getText().toString().trim(), String.valueOf(folderEntity.getId()));

                            if (entityFromDB == null) {
                                insertUpdateData(database, entityFromDB, addTaskDetails, taskDetailsEntity);
                                finish();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ApplicationUtils.customToast(AddTaskActivity.this, getString(R.string.task_already_exists));

                                    }
                                });
                            }
                        } else {
                            insertUpdateData(database, entityFromDB, addTaskDetails, taskDetailsEntity);
                            finish();
                        }
                    });
                }
                break;
        }
    }


    public void insertUpdateData(MyTaskDatabase database, TaskDetailsEntity entityFromDB, AddTaskDetails addTaskDetails, TaskDetailsEntity
            taskDetailsEntity) {
        if (entityFromDB == null) {
            /*INSERT DATA*/
            CURR_FOLDER_ID = folderEntity.getId();

            if (moveToFolderId != -1) {
                addTaskDetails.setParentColumn(String.valueOf(moveToFolderId));
                taskDetailsEntity.setParentColumn(String.valueOf(moveToFolderId));
            } else {
                addTaskDetails.setParentColumn(String.valueOf(CURR_FOLDER_ID));
                taskDetailsEntity.setParentColumn(String.valueOf(CURR_FOLDER_ID));
            }

            /*Insert in TaskDetails*/
            database.getTaskDetailsDao().insertTaskDetails(taskDetailsEntity);

            TaskDetailsEntity taskDetailsfromDb = database.getTaskDetailsDao().getTaskDetailsByTaskNameAndParentColumn(taskDetailsEntity.getTaskName(), taskDetailsEntity.getParentColumn());
            if (taskDetailsfromDb != null) {
                addTaskDetails.setTaskId(taskDetailsfromDb.getTaskId());
                addTaskDetails.setTaskName(taskDetailsfromDb.getTaskName());
                addTaskDetails.setTaskDate(taskDetailsfromDb.getTaskDate());
                addTaskDetails.setNotificationDate(taskDetailsfromDb.getNotificationDate());
                addTaskDetails.setTaskTime(taskDetailsfromDb.getTaskTime());
                addTaskDetails.setTaskRepeatMode(taskDetailsfromDb.getTaskRepeatMode());
                addTaskDetails.setTaskNote(taskDetailsfromDb.getTaskNote());
                addTaskDetails.setTaskPriority(taskDetailsfromDb.getTaskPriority());
                addTaskDetails.setTaskFinishStatus(taskDetailsfromDb.getTaskFinishStatus());

                FolderEntity folderEntityLocal = new FolderEntity();
                folderEntityLocal.setFolderName(null);
                if (moveToFolderId != -1) {
                    folderEntityLocal.setInsertedFrom(String.valueOf(moveToFolderId));
                } else {
                    folderEntityLocal.setInsertedFrom(String.valueOf(CURR_FOLDER_ID));
                }
                folderEntityLocal.setTaskDetails(addTaskDetails);

                /*Insert in AllFolder*/
                database.getFolderDao().insertFolder(folderEntityLocal);

                /*Get latest inserted record from DB*/

                int latest_id = database.getFolderDao().getLastRecordInsertedInDB();

                /*Update Allfolders id in TaskDetails Table */

                database.getTaskDetailsDao().updateAllFoldersIdInTaskDetails(taskDetailsfromDb.getTaskId(), latest_id);


                //   cancelAlarmForAllTask();
                ApplicationUtils.cancelTaskAlarm(AddTaskActivity.this, MAIN_NOTIFICATION_CODE);
                ApplicationUtils.fireAlarmDaily12AM(AddTaskActivity.this, MAIN_NOTIFICATION_CODE);

                Log.e(TAG, "Data Inserted in Folder Table-- Folder Column");
            }
        } else {
            /*UPDATE DATA*/
            if (entityFromDB != null && entityFromDB.getTaskName().
                    equalsIgnoreCase(binding.edtTaskHeading.getText().toString().trim())) {

                folderEntity.setFolderName(null);
                if (!entityFromDB.getParentColumn().equalsIgnoreCase(String.valueOf(moveToFolderId))) {
                    if (moveToFolderId != -1) {
                        folderEntity.setInsertedFrom(String.valueOf(moveToFolderId));
                    }
                }
                if (!addTaskDetails.getParentColumn().equalsIgnoreCase(String.valueOf(moveToFolderId))) {
                    if (moveToFolderId != -1) {
                        addTaskDetails.setParentColumn(String.valueOf(moveToFolderId));
                    }
                }

                folderEntity.setTaskDetails(addTaskDetails);

                database.getFolderDao().updateFolderTask(folderEntity);

                entityFromDB.setTaskName(addTaskDetails.getTaskName());
                entityFromDB.setTaskDate(addTaskDetails.getTaskDate());
                entityFromDB.setNotificationDate(addTaskDetails.getTaskDate());
                entityFromDB.setTaskTime(addTaskDetails.getTaskTime());
                entityFromDB.setTaskRepeatMode(addTaskDetails.getTaskRepeatMode());
                entityFromDB.setTaskNote(addTaskDetails.getTaskNote());
                entityFromDB.setTaskPriority(addTaskDetails.getTaskPriority());
                entityFromDB.setTaskFinishStatus(addTaskDetails.getTaskFinishStatus());

                if (!entityFromDB.getParentColumn().equalsIgnoreCase(String.valueOf(moveToFolderId))) {
                    if (moveToFolderId != -1) {
                        entityFromDB.setParentColumn(String.valueOf(moveToFolderId));
                    }
                }
                database.getTaskDetailsDao().updateTaskDetails(entityFromDB);

                //   cancelAlarmForAllTask();
                ApplicationUtils.cancelTaskAlarm(AddTaskActivity.this, MAIN_NOTIFICATION_CODE);
                ApplicationUtils.fireAlarmDaily12AM(AddTaskActivity.this, MAIN_NOTIFICATION_CODE);

                Log.e(TAG, "Data Inserted in Folder Table-- Folder Column");
            }

        }
    }

    private void openDateNTimePicker() {

        if (mBottomSheetDateTime.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            binding.bckView.setVisibility(View.VISIBLE);
            setupViewPager(binding.dateTimePicker.viewpager);
            binding.dateTimePicker.tabs.setupWithViewPager(binding.dateTimePicker.viewpager);
            mBottomSheetDateTime.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            mBottomSheetDateTime.setState(BottomSheetBehavior.STATE_COLLAPSED);
            binding.bckView.setVisibility(View.GONE);
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DateFragment(), " Date ");
        adapter.addFragment(new TimeFragment(), " Time ");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetDateTime.getState() == BottomSheetBehavior.STATE_EXPANDED || mBottomSheetMoveTo.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetDateTime.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mBottomSheetMoveTo.setState(BottomSheetBehavior.STATE_COLLAPSED);
            binding.bckView.setVisibility(View.GONE);
        } else {
            if (isFromNotification) {
                ApplicationUtils.simpleIntentFinish(AddTaskActivity.this, HomeActivity.class, Bundle.EMPTY);
            } else {
                AddTaskActivity.this.finish();
                //   overridePendingTransition(R.anim.slide_out_up, R.anim.slide_in_up);
            }
        }

    }

    public void setCustomFont() {

        ViewGroup vg = (ViewGroup) binding.dateTimePicker.tabs.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.bold_font)));
                }
            }
        }
    }

    private void setUpBottomSheetAdapter(String mode) {
        if (mode.equalsIgnoreCase(MOVE_TO)) {
            listFilter.clear();
            binding.contraintMoveTo.setVisibility(View.VISIBLE);
            binding.frameLayoutMain.setVisibility(View.VISIBLE);
            // add Move FolderFragment
            MoveFolderFragment fragment = new MoveFolderFragment();
            Bundle bundle = new Bundle();
            bundle.putString(MOVE_FROM, "0");
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(binding.frameLayoutMain.getId(), fragment, "MoveFolderFragment").commitAllowingStateLoss();
        }
    }


    @Override
    public void getSelectedFolderDetails(String from, FolderEntity folderEntity) {
        folderEntity_moveto = folderEntity;
        if (folderEntity != null) {
            CURR_FOLDER_ID_MOVETO = folderEntity.getId();
            binding
                    .txtHeader.setText(folderEntity.getFolderName());
        } else {
            binding.txtHeader.setText(getResources().getString(R.string.home));
        }
        if (!from.equalsIgnoreCase(FROM_HOME_FRAGMENT)) {
            binding.imgBackArrow.setVisibility(View.VISIBLE);
            //  binding.btnMoveTo.setVisibility(View.VISIBLE);
        } else {
            binding.imgBackArrow.setVisibility(View.GONE);
            //    binding.btnMoveTo.setVisibility(View.GONE);
        }
    }

    private void checkCurrentFolder() {
        appExecutors.getExeDiskIO().execute(() -> {
            int currFolderId = CURR_FOLDER_ID_MOVETO;
            String currentFolder = folderEntity_moveto.getFolderName();
            database = ((MyTaskApp) getApplicationContext()).getDatabase();
            if (!TextUtils.isEmpty(currentFolder)) {
                FolderEntity folderEntity = database.getFolderDao().getFolderByID(currFolderId);
                if (folderEntity != null) {
                    if (!TextUtils.isEmpty(folderEntity.getInsertedFrom())
                            && !TextUtils.isEmpty(folderEntity.getFolderName())) {
                        FolderEntity entity = database.getFolderDao().getFolderByID(ObjectUtils.getIntFromString(folderEntity.getInsertedFrom()));

                        if (entity != null) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(FOLDER_OBJ, entity);
                            bundle.putString(MOVE_FROM, String.valueOf(entity.getId()));
                            MoveFolderFragment fragment = new MoveFolderFragment();
                            fragment.setArguments(bundle);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frameLayout_main, fragment).addToBackStack(null);
                            transaction.commitAllowingStateLoss();
                        } else {
                            Bundle bundle = new Bundle();
                            folderEntity.setFolderName(getResources().getString(R.string.home));
                            bundle.putParcelable(FOLDER_OBJ, folderEntity);
                            bundle.putString(MOVE_FROM, String.valueOf(folderEntity.getInsertedFrom()));
                            MoveFolderFragment fragment = new MoveFolderFragment();
                            fragment.setArguments(bundle);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frameLayout_main, fragment).addToBackStack(null);
                            transaction.commitAllowingStateLoss();
                        }

                        //finish();
                    }
                } else {
                    //  finish();
                }
            } else {
                // finish();
            }
        });
    }


    public void moveTaskToFolder() {
        if (folderEntity_moveto != null && folderEntity_moveto.getId() != 0) {
            moveToFolderId = folderEntity_moveto.getId();
            mBottomSheetMoveTo.setState(BottomSheetBehavior.STATE_COLLAPSED);
            binding.bckView.setVisibility(View.GONE);
            binding.txtMoveToPath.setText("../" + folderEntity_moveto.getFolderName() + " " + getResources().getString(R.string.folder));
        } else {
            moveToFolderId = Integer.parseInt(FROM_HOME_FRAGMENT);
            mBottomSheetMoveTo.setState(BottomSheetBehavior.STATE_COLLAPSED);
            binding.bckView.setVisibility(View.GONE);
            binding.txtMoveToPath.setText("../" + getResources().getString(R.string.home) + " " + getResources().getString(R.string.folder));
        }

    }

    /*
    * This method initially will cancel all active alarms. Using its id(i.e taskId)
    * */
    public void cancelAlarmForAllTask() {

        String currentDate = ApplicationUtils.getCurrentDate();
        AppExecutors appExecutors = new AppExecutors();
        List<TaskDetailsEntity> entityList = new ArrayList<>();
        appExecutors.getExeDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                MyTaskDatabase database = ((MyTaskApp) getApplicationContext()).getDatabase();
                cancelAlarmList = database.getTaskDetailsDao().getAllTasksByDate(currentDate, "0", NEVER);
                if (cancelAlarmList != null && cancelAlarmList.size() > 0) {
                    for (int i = 0; i < cancelAlarmList.size(); i++) {
                        ApplicationUtils.cancelTaskAlarm(AddTaskActivity.this, cancelAlarmList.get(i).getTaskId());
                    }

                    /*Start Alarm Manager to proceed with further notifications */
                    ApplicationUtils.cancelTaskAlarm(AddTaskActivity.this, MAIN_NOTIFICATION_CODE);
                    ApplicationUtils.fireAlarmDaily12AM(AddTaskActivity.this, MAIN_NOTIFICATION_CODE);

                }
            }
        });
    }

}
