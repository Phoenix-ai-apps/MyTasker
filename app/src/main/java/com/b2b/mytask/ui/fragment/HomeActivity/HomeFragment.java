package com.b2b.mytask.ui.fragment.HomeActivity;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.b2b.mytask.AppExecutors;
import com.b2b.mytask.DataRepository;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.R;
import com.b2b.mytask.adapters.CustomFolderTaskAdapter;
import com.b2b.mytask.adapters.GridviewAdapter;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.databinding.FragementHomeBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.db.entities.TaskDetailsEntity;
import com.b2b.mytask.interfaces.IEditDeletePopup;
import com.b2b.mytask.ui.AddTaskActivity;
import com.b2b.mytask.ui.AllTaskActivity;
import com.b2b.mytask.ui.ShowTaskListByDateActivity;
import com.b2b.mytask.utils.ApplicationUtils;
import com.b2b.mytask.viewModel.FolderViewModel;
import com.b2b.mytask.viewModel.TaskDetailsViewModel;

import java.util.List;

/**
 * Created by root on 19/4/18.
 */
public class HomeFragment extends Fragment implements IEditDeletePopup, AllConstants, View.OnClickListener {

    private static final String TAG = "HomeFragment";
    public GridviewAdapter gridviewAdapter;
    // @formatter:on
    int folderColor;
    boolean isFreshFolder = false;
    private int[] mColours = new int[0];
    private Context context;
    private IEditDeletePopup iEditDeletePopup;
    private CustomFolderTaskAdapter customFolderTaskAdapter;
    private MyTaskDatabase database;
    private AppExecutors appExecutors;
    private FragementHomeBinding mBinding;
    private List<FolderEntity> folderEntities;
    private boolean isFABOpen = false;
    private boolean isAvailableInDB = false;
    private FolderEntity entityFromDB;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragement_home, container, false);
        mBinding.setLifecycleOwner(this);
        initializeResources();
        return mBinding.getRoot();
    }

    private void initializeResources() {
        context = MyTaskApp.getInstance();
        database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
        appExecutors = new AppExecutors();
        mColours = getResources().getIntArray(R.array.colours);
        mBinding.fab.setOnClickListener(this);
        mBinding.rlFabTask.setOnClickListener(this);
        mBinding.rlFabFolder.setOnClickListener(this);
        mBinding.viewBg.setOnClickListener(this);
        mBinding.includeMainContent.includeHomeMain.layoutAllTask.setOnClickListener(this);
        mBinding.includeMainContent.includeHomeMain.layoutThisWeek.setOnClickListener(this);
        mBinding.includeMainContent.includeHomeMain.layoutToday.setOnClickListener(this);
        mBinding.includeMainContent.includeHomeMain.layoutCompleted.setOnClickListener(this);
        iEditDeletePopup = (IEditDeletePopup) this;
        customFolderTaskAdapter = new CustomFolderTaskAdapter(iEditDeletePopup);
        mBinding.includeMainContent.includeRecyclerview.recyclerview.setAdapter(customFolderTaskAdapter);
        mBinding.incToolbar.imgCalender.setOnClickListener(this);
        mBinding.incToolbar.imgCalender.setVisibility(View.VISIBLE);
        //  ((AppCompatActivity)getActivity()).setSupportActionBar(mBinding.toolbar);
        setToolbar();
        entityFromDB = new FolderEntity();


        mBinding.includeMainContent.includeRecyclerview.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mBinding.fab.getVisibility() == View.VISIBLE) {
                    mBinding.fab.hide();
                    mBinding.fabFolder.hide();
                    mBinding.fabTask.hide();
                } else if (dy < 0 && mBinding.fab.getVisibility() != View.VISIBLE) {
                    mBinding.fab.show();
                    mBinding.fabFolder.show();
                    mBinding.fabTask.show();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final FolderViewModel allFolderViewModel =
                ViewModelProviders.of(this).get(FolderViewModel.class);

        final TaskDetailsViewModel allTasksviewModel =
                ViewModelProviders.of(this).get(TaskDetailsViewModel.class);

        subscribeUi(allFolderViewModel, allTasksviewModel);
    }

    private void subscribeUi(FolderViewModel allFolderViewModel, TaskDetailsViewModel allTasksviewModel) {
        // Update the list when the data changes
        allFolderViewModel.getAllFoldersByInsertedFolder(FROM_HOME_FRAGMENT).observe(this, new Observer<List<FolderEntity>>() {
            @Override
            public void onChanged(@Nullable List<FolderEntity> myProducts) {
                if (myProducts != null) {
                    mBinding.setIsLoading(false);
                    folderEntities = myProducts;
                    customFolderTaskAdapter.setFolderList(myProducts);
                } else {
                    mBinding.setIsLoading(true);
                }
                // espresso does not know how to wait for data binding's loop so we execute changes
                // sync.
                mBinding.executePendingBindings();
            }
        });

        allTasksviewModel.getAllTasks().observe(this, new Observer<List<TaskDetailsEntity>>() {
            @Override
            public void onChanged(@Nullable List<TaskDetailsEntity> myProducts) {
                if (myProducts != null) {
                    mBinding.includeMainContent.includeHomeMain.txtAllTaskCount.setText(String.valueOf(myProducts.size()) + " " + "tasks");
                }
            }
        });

        allTasksviewModel.getAllTaskDetailsbyWeek(ApplicationUtils.getStartDateOfCurrentWeek(), ApplicationUtils.getEndDateOfCurrentWeek()).observe(this, new Observer<List<TaskDetailsEntity>>() {
            @Override
            public void onChanged(@Nullable List<TaskDetailsEntity> myProducts) {
                if (myProducts != null) {
                    mBinding.includeMainContent.includeHomeMain.txtThisWeekCount.setText(String.valueOf(myProducts.size()) + " " + "tasks");

                }
            }
        });

        allTasksviewModel.getAllTaskDetailsbyToday(ApplicationUtils.getCurrentDate()).observe(this, new Observer<List<TaskDetailsEntity>>() {
            @Override
            public void onChanged(@Nullable List<TaskDetailsEntity> myProducts) {
                if (myProducts != null) {
                    mBinding.includeMainContent.includeHomeMain.txtTodayTaskCount.setText(String.valueOf(myProducts.size()) + " " + "tasks");

                }
            }
        });

        allTasksviewModel.getAllTaskDetailsbyCompleted().observe(this, new Observer<List<TaskDetailsEntity>>() {
            @Override
            public void onChanged(@Nullable List<TaskDetailsEntity> myProducts) {
                if (myProducts != null) {
                    mBinding.includeMainContent.includeHomeMain.txtCompletedTaskCount.setText(String.valueOf(myProducts.size()) + " " + "tasks");

                }
            }
        });

    }

    private void setToolbar() {
        mBinding.incToolbar.txtToolbarTitle.setText("Angel Broking-B2B");
       /* setSupportActionBar(mBinding.incToolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }*/
    }

    public void addFolder(FolderEntity folderEntity) {
        //  folderColor = mColours[0];

        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_new_folder);
        dialog.setCancelable(false);
        dialog.show();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        GridView gridView = (GridView) dialog.findViewById(R.id.gridView);

        final EditText edtFolderName = (EditText) dialog.findViewById(R.id.edt_folder_name);
        TextView txtSave = (TextView) dialog.findViewById(R.id.txt_save);
        AppCompatImageView imgFolder = (AppCompatImageView) dialog.findViewById(R.id.img_title_folder);
        final AppCompatImageView fabCancel = (AppCompatImageView) dialog.findViewById(R.id.fab_cancel);
        TextInputLayout inputName = (TextInputLayout) dialog.findViewById(R.id.input_name);

        if (folderEntity != null && !TextUtils.isEmpty(folderEntity.getFolderName()) && folderEntity.getColor() != 0) {
            edtFolderName.setText(folderEntity.getFolderName());
            imgFolder.setColorFilter(new PorterDuffColorFilter(folderEntity.getColor(), PorterDuff.Mode.SRC_IN));
            folderColor = folderEntity.getColor();

        }

        gridviewAdapter = new GridviewAdapter(getActivity(), mColours, folderColor);
        gridView.setAdapter(gridviewAdapter);
        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        gridviewAdapter.setSelectedPosition(position);
                        gridviewAdapter.notifyDataSetChanged();
                        folderColor = mColours[position];
                        imgFolder.setColorFilter(new PorterDuffColorFilter(folderColor, PorterDuff.Mode.SRC_IN));
                    }
                });
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderName = edtFolderName.getText().toString().trim();

                if (!TextUtils.isEmpty(folderName)) {
                    if (folderColor != 0) {
                        if (!isAvailableInDB) {
                            getActivity()
                                    .getWindow()
                                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                            FolderEntity folderTask = new FolderEntity(); // This obj for Current Object Identification
                            folderTask.setInsertedFrom(FROM_HOME_FRAGMENT);
                            folderTask.setFolderName(folderName);
                            folderTask.setColor(folderColor);

                            appExecutors.getExeDiskIO().execute(() -> {

                                if (folderEntity != null) {
                                    entityFromDB = DataRepository.getDataRepository(database).getFolderByName(folderEntity.getFolderName(), String.valueOf(folderEntity.getInsertedFrom()));
                                } else {
                                    entityFromDB = DataRepository.getDataRepository(database).getFolderByName(folderName, FROM_HOME_FRAGMENT);

                                }
                                if (entityFromDB == null) {
                                    database.getFolderDao().insertFolder(folderTask);
                                    dialog.dismiss();
                                    Log.e(TAG, "Data Inserted in Folder Table-- Folder Column");
                                    // onUiThread(false, FOLDER);
                                } else {
                                    if (entityFromDB != null && !TextUtils.isEmpty(entityFromDB.getFolderName())) {
                                        folderEntity.setFolderName(folderName);
                                        folderEntity.setColor(folderColor);
                                        database.getFolderDao().UpdateFolderDetails(folderEntity.getFolderName(), folderEntity.getColor(), folderEntity.getId());
                                        dialog.dismiss();
                                    }
                                }
                            });
                            // customFolderTaskAdapter.setFolderList(list);
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ApplicationUtils.customToast(getActivity(), "Folder with this name already exists");

                                }
                            });
                        }
                    } else {
                        ApplicationUtils.customToast(getActivity(), "Select Folder Colour");

                    }
                } else {
                    ApplicationUtils.customToast(getActivity(), "Enter Folder Name");
                }
            }
        });


        edtFolderName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (folderEntity != null && folderEntity.getFolderName() != null) {
                    if (!s.toString().equalsIgnoreCase(folderEntity.getFolderName())) {
                        appExecutors.getExeDiskIO().execute(() -> {
                            // TODO Auto-generated method stub
                            entityFromDB = DataRepository.getDataRepository(database).getFolderByName(s.toString(), String.valueOf(folderEntity.getInsertedFrom()));
                            if (entityFromDB != null) {
                                isAvailableInDB = true;
                            } else {
                                isAvailableInDB = false;
                            }
                        });
                    } else {
                        isAvailableInDB = false;
                    }
                } else {
                    appExecutors.getExeDiskIO().execute(() -> {
                        // TODO Auto-generated method stub
                        entityFromDB = DataRepository.getDataRepository(database).getFolderByName(s.toString(), String.valueOf(FROM_HOME_FRAGMENT));
                        if (entityFromDB != null) {
                            isAvailableInDB = true;
                        } else {
                            isAvailableInDB = false;
                        }
                    });

                }
            }
        });


        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApplicationUtils.hideKeyboard(getActivity());
                dialog.dismiss();
            }
        });
    }


    public void addTask() {

        FolderEntity folderEntity = new FolderEntity();
        folderEntity.setInsertedFrom(FROM_HOME_FRAGMENT);
        Bundle bundle = new Bundle();
        bundle.putParcelable(FOLDER_OBJ, folderEntity);
        ApplicationUtils.startActivityIntent(getActivity(), AddTaskActivity.class, bundle);

    }

    @Override
    public void getClickEvent(String str_click, FolderEntity folderEntity) {
        if (folderEntity != null) {
            if (folderEntity != null && !TextUtils.isEmpty(folderEntity.getFolderName())) {
                //Folder
                addFolder(folderEntity);
            } else {
                //Task
                Bundle bundle = new Bundle();
                if (folderEntity != null && folderEntity.getTaskDetails() != null
                        && !TextUtils.isEmpty(folderEntity.getTaskDetails().getTaskName())) {
                    bundle.putString(TITLE, String.valueOf(folderEntity.getTaskDetails().getTaskName()));
                    bundle.putString(getResources().getString(R.string.parent_column), folderEntity.getTaskDetails().getParentColumn());
                    bundle.putParcelable(TASK, folderEntity.getTaskDetails());
                    bundle.putParcelable(FOLDER_OBJ, folderEntity);
                    ApplicationUtils.startActivityIntent(getActivity(), AddTaskActivity.class, bundle);
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_fab_task:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeFABMenu();
                        mBinding.viewBg.setVisibility(View.GONE);
                        folderColor = 0;
                        addTask();
                    }
                });

                break;

            case R.id.rl_fab_folder:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeFABMenu();
                        mBinding.viewBg.setVisibility(View.GONE);
                        folderColor = 0;
                        addFolder(null);
                    }
                });
                break;

            case R.id.fab:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFABOpen) {
                            mBinding.viewBg.setVisibility(View.VISIBLE);
                            showFABMenu();
                        } else {
                            mBinding.viewBg.setVisibility(View.GONE);
                            closeFABMenu();
                        }
                    }
                });

                break;
            case R.id.view_bg:
                mBinding.viewBg.setVisibility(View.GONE);
                closeFABMenu();
                break;

            case R.id.layout_today:
                Bundle bundle_today = new Bundle();
                bundle_today.putString(TOOLBAR_TITLE, mBinding.includeMainContent.includeHomeMain.txtToday.getText().toString().trim());
                bundle_today.putString(NAVIGATES_FROM, FROM_HOME_TODAY);
                bundle_today.putString(CURRENT_DATE_OF_WEEK, ApplicationUtils.getCurrentDate());
                ApplicationUtils.startActivityIntent(getActivity(), AllTaskActivity.class, bundle_today);

                break;

            case R.id.layout_this_week:
                Bundle bundle_weekly = new Bundle();
                bundle_weekly.putString(TOOLBAR_TITLE, mBinding.includeMainContent.includeHomeMain.txtThisWeek.getText().toString().trim());
                bundle_weekly.putString(NAVIGATES_FROM, FROM_HOME_THISWEEK);
                bundle_weekly.putString(START_DATE_OF_WEEK, ApplicationUtils.getStartDateOfCurrentWeek());
                bundle_weekly.putString(END_DATE_OF_WEEK, ApplicationUtils.getEndDateOfCurrentWeek());
                ApplicationUtils.startActivityIntent(getActivity(), AllTaskActivity.class, bundle_weekly);

                break;

            case R.id.layout_all_task:
                Bundle bundle_all_task = new Bundle();
                bundle_all_task.putString(TOOLBAR_TITLE, mBinding.includeMainContent.includeHomeMain.txtAllTasks.getText().toString().trim());
                bundle_all_task.putString(NAVIGATES_FROM, FROM_HOME_ALLTASK);
                ApplicationUtils.startActivityIntent(getActivity(), AllTaskActivity.class, bundle_all_task);
                break;

            case R.id.layout_completed:
                Bundle bundle_completed = new Bundle();
                bundle_completed.putString(TOOLBAR_TITLE, mBinding.includeMainContent.includeHomeMain.txtCompleted.getText().toString().trim());
                bundle_completed.putString(NAVIGATES_FROM, FROM_HOME_COMPLETED);
                bundle_completed.putString(NAVIGATES_FROM, FROM_HOME_COMPLETED);
                ApplicationUtils.startActivityIntent(getActivity(), AllTaskActivity.class, bundle_completed);
                break;

            case R.id.img_calender:

                ApplicationUtils.startActivityIntent(getActivity(), ShowTaskListByDateActivity.class, Bundle.EMPTY);

                break;
            default:
                break;

        }
    }

    private void showFABMenu() {

        isFABOpen = true;
        mBinding.rlFabFolder.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        mBinding.rlFabTask.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        mBinding.ctvTask.setVisibility(View.VISIBLE);
        mBinding.ctvFolder.setVisibility(View.VISIBLE);


    }

    private void closeFABMenu() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isFABOpen = false;
                mBinding.rlFabFolder.animate().translationY(0);
                mBinding.rlFabTask.animate().translationY(0);
                mBinding.ctvTask.setVisibility(View.GONE);
                mBinding.ctvFolder.setVisibility(View.GONE);
            }
        });

    }

}
