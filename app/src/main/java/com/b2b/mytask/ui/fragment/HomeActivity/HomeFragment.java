package com.b2b.mytask.ui.fragment.HomeActivity;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.b2b.mytask.AppExecutors;
import com.b2b.mytask.DataRepository;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.R;
import com.b2b.mytask.adapters.CustomFolderTaskAdapter;
import com.b2b.mytask.adapters.GridviewAdapter;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.databinding.FragementHomeBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.FolderCycleFlowEntity;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.db.entities.SubFolderEntity;
import com.b2b.mytask.db.entities.TaskDetailsEntity;
import com.b2b.mytask.models.AddTaskDetails;
import com.b2b.mytask.interfaces.IEditDeletePopup;
import com.b2b.mytask.viewModel.FolderViewModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 19/4/18.
 */
public class HomeFragment extends Fragment implements IEditDeletePopup, AllConstants, View.OnClickListener {

    private static final String TAG = "HomeFragment";

    // @formatter:on
    int                             folderColor;
    private int[]                   mColours = new int[0];
    private List<String>            task_list;
    private Context                 context;
    public  GridviewAdapter         gridviewAdapter;
    private IEditDeletePopup        iEditDeletePopup;
    private CustomFolderTaskAdapter customFolderTaskAdapter;
    private MyTaskDatabase          database    ;
    private AppExecutors            appExecutors;
    private FragementHomeBinding    mBinding;
    private List<FolderEntity>      folderEntities;
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragement_home, container, false);
        mBinding.setLifecycleOwner(this);
        initializeResources();
        return mBinding.getRoot();
    }

    private void initializeResources() {
        context          = MyTaskApp.getInstance();
        database         = ((MyTaskApp) context.getApplicationContext()).getDatabase();
        appExecutors     = new AppExecutors();
        mColours         = getResources().getIntArray(R.array.colours);
        task_list        = new ArrayList<>();
        iEditDeletePopup = (IEditDeletePopup) this;
        mBinding.footer.imgAddTask.setOnClickListener(this);
        mBinding.footer.txtNewFolder.setOnClickListener(this);
        customFolderTaskAdapter = new CustomFolderTaskAdapter(iEditDeletePopup);
        mBinding.recyclerview.setAdapter(customFolderTaskAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FolderViewModel viewModel =
                ViewModelProviders.of(this).get(FolderViewModel.class);

        subscribeUi(viewModel);
    }

    private void subscribeUi(FolderViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getAllFoldersByInsertedFolder(FROM_HOME_FRAGMENT).observe(this, new Observer<List<FolderEntity>>() {
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
    }

    public void addFolder() {
        folderColor = mColours[0];
        final Dialog dialog = new Dialog(getActivity());
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
        TextView txtSave             = (TextView) dialog.findViewById(R.id.txt_save);
        AppCompatImageView imgFolder = (AppCompatImageView) dialog.findViewById(R.id.img_title_folder);
        final TextView  txtCancel    = (TextView) dialog.findViewById(R.id.txt_cancel);
        TextInputLayout inputName    = (TextInputLayout) dialog.findViewById(R.id.input_name);

        gridviewAdapter = new GridviewAdapter(getActivity(), mColours);
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
                    task_list.add(folderName);
                    dialog.dismiss();
                    getActivity()
                    .getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    FolderEntity folderTask = new FolderEntity(); // This obj for Current Object Identification
                    folderTask.setInsertedFrom(FROM_HOME_FRAGMENT);
                    folderTask.setFolderName(folderName);
                    folderTask.setColor(folderColor);

                    appExecutors.getExeDiskIO().execute(()->{
                        FolderEntity entityFromDB = DataRepository.getDataRepository(database).getFolderByName(folderName, FROM_HOME_FRAGMENT);
                        if(entityFromDB == null){
                            database.getFolderDao().insertFolder(folderTask);
                            Log.e(TAG, "Data Inserted in Folder Table-- Folder Column");
                            onUiThread(false, FOLDER);
                        }else {
                            if(entityFromDB != null && !TextUtils.isEmpty(entityFromDB.getFolderName())){
                              onUiThread(true, FOLDER);
                            }
                        }
                    });

                // customFolderTaskAdapter.setFolderList(list);

                } else {
                    Toast.makeText(getActivity(), "Enter FolderTask Name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void addTask() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_new_task);
        dialog.setCancelable(false);
        dialog.show();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        final AppCompatEditText edtTaskName = dialog.findViewById(R.id.edt_task_name);
        AppCompatTextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        final AppCompatTextView btnSave = dialog.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String task_name = edtTaskName.getText().toString().trim();
                        if (!TextUtils.isEmpty(task_name)) {
                            task_list.add(task_name);
                            dialog.dismiss();
                            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            FolderEntity folderTask = new FolderEntity();
                            AddTaskDetails addTaskDetails = new AddTaskDetails();
                            addTaskDetails.setTaskName(task_name);
                            addTaskDetails.setParentColumn(FROM_HOME_FRAGMENT);
                            folderTask.setInsertedFrom(FROM_HOME_FRAGMENT);
                            folderTask.setTaskDetails(addTaskDetails);

                            TaskDetailsEntity taskDetailsEntity = new TaskDetailsEntity();
                            taskDetailsEntity.setTaskName(task_name);
                            taskDetailsEntity.setParentColumn(FROM_HOME_FRAGMENT);

                            MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                            AppExecutors appExecutors = new AppExecutors();
                            appExecutors.getExeDiskIO().execute(()->{
                                TaskDetailsEntity entityFromDB =
                                        DataRepository.getDataRepository(database).getTaskByName(task_name, FROM_HOME_FRAGMENT);
                                if(entityFromDB == null){
                                    database.getFolderDao().insertFolder(folderTask);
                                    database.getTaskDetailsDao().insertTaskDetails(taskDetailsEntity);
                                   // list.add(folderTask);
                                    onUiThread(false, TASK);
                                    Log.e(TAG, "Data Inserted in Folder Table-- Folder Column");
                                }else {
                                    if(entityFromDB != null && entityFromDB.getTaskName().equalsIgnoreCase(task_name)){
                                        onUiThread(true, TASK);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Enter Task Name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btnCancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
    }

    private void onUiThread(boolean flag, String from){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(flag){
                    if(from.equalsIgnoreCase(FOLDER)){
                        Toast.makeText(getActivity(), "Folder with this name already exists", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getActivity(), "Task with this name already exists", Toast.LENGTH_LONG).show();
                    }
                }else{
                  // customFolderTaskAdapter.setFolderList(list);
                }
            }
        });
    }

    @Override
    public void getClickEvent(String str_click) {
        Toast.makeText(context, "" + str_click, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add_task:
                addTask();
                break;

            case R.id.txt_new_folder:
                addFolder();
                break;

            default:
                break;

        }
    }


}
