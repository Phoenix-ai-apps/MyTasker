package com.b2b.mytask.ui.fragment.FolderDetailsActivity;

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
import android.support.v7.widget.LinearLayoutManager;
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
import com.b2b.mytask.databinding.FragmentFolderDetailsBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.interfaces.IEditDeletePopup;
import com.b2b.mytask.ui.AddTaskActivity;
import com.b2b.mytask.utils.ApplicationUtils;
import com.b2b.mytask.viewModel.FolderViewModel;

import java.util.List;

/**
 * Created by cso on 21/4/18.
 */

public class FolderDetailsFragment extends Fragment implements AllConstants, IEditDeletePopup, View.OnClickListener {

    private static final String TAG = "FolderDetailsFragment";
    public static int CURR_FOLDER_ID = 0;
    public GridviewAdapter gridviewAdapter;
    int folderColor;
    private int[] mColours = new int[0];
    private IEditDeletePopup iEditDeletePopup;
    private FragmentFolderDetailsBinding binding;
    private CustomFolderTaskAdapter customFolderTaskAdapter;
    private Context context;
    private String title;
    private FolderEntity folderEntity;
    private String globalName;
    private boolean isFABOpen = false;
    private boolean isAvailableInDB = false;
    private FolderEntity entityFromDB;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_folder_details, container, false);
        binding.setLifecycleOwner(this);
        initializeResources();
        return binding.getRoot();
    }

    private void initializeResources() {
        entityFromDB = new FolderEntity();
        if (context == null) {
            context = MyTaskApp.getInstance();
        }
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey(TITLE)) {
                title = bundle.getString(TITLE);
            }
            if (bundle.containsKey(FOLDER_OBJ)) {
                folderEntity = bundle.getParcelable(FOLDER_OBJ);
                CURR_FOLDER_ID = folderEntity.getId();
            }


        }
        mColours = getResources().getIntArray(R.array.colours);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        iEditDeletePopup = (IEditDeletePopup) this;
        binding.fab.setOnClickListener(this);
        binding.rlFabFolder.setOnClickListener(this);
        binding.rlFabTask.setOnClickListener(this);
        customFolderTaskAdapter = new CustomFolderTaskAdapter(iEditDeletePopup);
        binding.rvWork.setAdapter(customFolderTaskAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FolderViewModel viewModel =
                ViewModelProviders.of(this).get(FolderViewModel.class);
        subscribeUi(viewModel);
    }

    private void subscribeUi(FolderViewModel viewModel) {
        viewModel.getAllFoldersByInsertedFolder(String.valueOf(folderEntity.getId())).observe(this, new Observer<List<FolderEntity>>() {
            @Override
            public void onChanged(@Nullable List<FolderEntity> myProducts) {
                if (myProducts != null) {
                    customFolderTaskAdapter.setFolderList(myProducts);
                } else {
                }
                // espresso does not know how to wait for data binding's loop so we execute changes
                // sync.
                binding.executePendingBindings();
            }
        });
    }

    public void addFolder(FolderEntity task) {
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

        if (task != null && !TextUtils.isEmpty(task.getFolderName()) && task.getColor() != 0) {
            edtFolderName.setText(task.getFolderName());
            imgFolder.setColorFilter(new PorterDuffColorFilter(task.getColor(), PorterDuff.Mode.SRC_IN));
            folderColor = task.getColor();
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
                try {
                    String folderName = edtFolderName.getText().toString().trim();
                    if (!TextUtils.isEmpty(folderName)) {
                        if (folderColor != 0) {
                            if (!isAvailableInDB) {
                                dialog.dismiss();
                                getActivity()
                                        .getWindow()
                                        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                                AppExecutors appExecutors = new AppExecutors();
                                appExecutors.getExeDiskIO().execute(() -> {
                                    FolderEntity entityFromDB;
                                    if (task != null) {
                                        entityFromDB =
                                                database.getFolderDao().getFolderByName(task.getFolderName(), String.valueOf(task.getInsertedFrom()));
                                    } else {
                                        entityFromDB =
                                                database.getFolderDao().getFolderByName(folderName, String.valueOf(folderEntity.getId()));
                                    }
                                    if (entityFromDB == null) {
                                    /*Insert Data*/
                                        FolderEntity folderTask = new FolderEntity();
                                        folderTask.setInsertedFrom(String.valueOf(folderEntity.getId()));
                                        folderTask.setFolderName(folderName);
                                        folderTask.setColor(folderColor);
                                        database.getFolderDao().insertFolder(folderTask);
                                        // This code decides the flow of Folder From home Fragment
                                        //  onUiThread(false, FOLDER);
                                        Log.e(TAG, "Data Inserted in Folder Table-- Folder Column");

                                    } else {
                                    /*Update Data*/
                                        if (entityFromDB != null && !TextUtils.isEmpty(entityFromDB.getFolderName())) {
                                            task.setFolderName(folderName);
                                            task.setColor(folderColor);
                                            database.getFolderDao().UpdateFolderDetails(task.getFolderName(), task.getColor(), task.getId());
                                            //  onUiThread(true, FOLDER);
                                        }
                                    }
                                });
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
                } catch (Exception e) {
                    e.printStackTrace();
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
                if (task != null && task.getFolderName() != null) {
                    if (!s.toString().equalsIgnoreCase(task.getFolderName())) {
                        MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                        AppExecutors appExecutors = new AppExecutors();
                        appExecutors.getExeDiskIO().execute(() -> {
                            // TODO Auto-generated method stub
                            entityFromDB = DataRepository.getDataRepository(database).getFolderByName(s.toString(), String.valueOf(task.getInsertedFrom()));
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
                    MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                    AppExecutors appExecutors = new AppExecutors();
                    appExecutors.getExeDiskIO().execute(() -> {
                        // TODO Auto-generated method stub
                        entityFromDB = DataRepository.getDataRepository(database).getFolderByName(s.toString(), String.valueOf(folderEntity.getId()));
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
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putParcelable(FOLDER_OBJ, folderEntity);
        ApplicationUtils.startActivityIntent(getActivity(), AddTaskActivity.class, bundle);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rl_fab_folder:
                folderColor = 0;
                addFolder(null);
                closeFABMenu();
                break;

            case R.id.rl_fab_task:
                folderColor = 0;
                addTask();
                closeFABMenu();
                break;

            case R.id.fab:
                if (!isFABOpen) {
//                    mBinding.viewBg.setVisibility(View.VISIBLE);
                    showFABMenu();
                } else {
//                    mBinding.viewBg.setVisibility(View.GONE);
                    closeFABMenu();
                }
                break;

            default:
                break;

        }

    }

    private void showFABMenu() {
        isFABOpen = true;
        binding.rlFabFolder.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        binding.rlFabTask.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        binding.ctvTask.setVisibility(View.VISIBLE);
        binding.ctvFolder.setVisibility(View.VISIBLE);
    }

    private void closeFABMenu() {
        isFABOpen = false;
        binding.rlFabFolder.animate().translationY(0);
        binding.ctvFolder.setVisibility(View.GONE);
        binding.rlFabTask.animate().translationY(0);
        binding.ctvTask.setVisibility(View.GONE);
    }

    @Override
    public void getClickEvent(String str_click, FolderEntity task) {
        if (task != null && !TextUtils.isEmpty(task.getFolderName())) {
            //Folder
            addFolder(task);
        } else {
            //Task
            Bundle bundle = new Bundle();
            if (task != null && task.getTaskDetails() != null
                    && !TextUtils.isEmpty(task.getTaskDetails().getTaskName())) {
                bundle.putString(TITLE, String.valueOf(task.getTaskDetails().getTaskName()));
                bundle.putString(getResources().getString(R.string.parent_column), task.getTaskDetails().getParentColumn());
                bundle.putParcelable(TASK, task.getTaskDetails());
                bundle.putParcelable(FOLDER_OBJ, task);
                ApplicationUtils.startActivityIntent(getActivity(), AddTaskActivity.class, bundle);
            }
        }

    }
}
