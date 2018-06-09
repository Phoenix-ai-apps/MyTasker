package com.b2b.sampleb2b.ui.fragment.FolderDetailsActivity;

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
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
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

import com.b2b.sampleb2b.AppExecutors;
import com.b2b.sampleb2b.DataRepository;
import com.b2b.sampleb2b.MyTaskApp;
import com.b2b.sampleb2b.R;
import com.b2b.sampleb2b.adapters.CustomFolderTaskAdapter;
import com.b2b.sampleb2b.adapters.GridviewAdapter;
import com.b2b.sampleb2b.constants.AllConstants;
import com.b2b.sampleb2b.databinding.FragmentFolderDetailsBinding;
import com.b2b.sampleb2b.db.MyTaskDatabase;
import com.b2b.sampleb2b.db.entities.FolderEntity;
import com.b2b.sampleb2b.db.entities.SubFolderEntity;
import com.b2b.sampleb2b.db.entities.TaskDetailsEntity;
import com.b2b.sampleb2b.models.AddTaskDetails;
import com.b2b.sampleb2b.interfaces.IEditDeletePopup;
import com.b2b.sampleb2b.viewModel.FolderViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cso on 21/4/18.
 */

public class FolderDetailsFragment extends Fragment implements AllConstants, IEditDeletePopup, View.OnClickListener {

    private static final String TAG     = "FolderDetailsFragment";
    public GridviewAdapter                 gridviewAdapter;
    int                                    folderColor;
    private int[]                          mColours         = new int[0];
    private List<String>                   task_list        = new ArrayList<>();
    private List<FolderEntity>             list             = new ArrayList<>();
    private IEditDeletePopup               iEditDeletePopup;
    private FragmentFolderDetailsBinding   binding;
    private CustomFolderTaskAdapter        customFolderTaskAdapter;
    private Context                        context;
    private String                         title;
    private FolderEntity                   folderEntity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_folder_details, container, false);
        binding.setLifecycleOwner(this);
        initializeResources();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FolderViewModel viewModel =
                ViewModelProviders.of(this).get(FolderViewModel.class);

        subscribeUi(viewModel);
    }

    private void subscribeUi(FolderViewModel viewModel){
        // getAllFoldersByInsertedFolder(title); || getAllFoldersByFoldeName
        viewModel.getAllFoldersByInsertedFolder(title).observe(this, new Observer<List<FolderEntity>>() {
            @Override
            public void onChanged(@Nullable List<FolderEntity> folderEntities) {
                if(folderEntities != null && folderEntities.size() > 0){
                    customFolderTaskAdapter.setFolderList(folderEntities);
                }
                binding.executePendingBindings();
            }
        });
    }

    private void initializeResources() {
        if(context == null){
            context = MyTaskApp.getInstance();
        }
        Bundle bundle = this.getArguments();
        if(bundle != null){
            if(bundle.containsKey(TITLE)){
                title = bundle.getString(TITLE);
            }
            if(bundle.containsKey(FOLDER_OBJ)){
              //  folderEntity = bundle.getParcelable(FOLDER_OBJ);
            }
        }
        mColours = getResources().getIntArray(R.array.colours);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        iEditDeletePopup = (IEditDeletePopup) this;
        binding.footer.imgAddTask.setOnClickListener(this);
        binding.footer.txtNewFolder.setOnClickListener(this);
        customFolderTaskAdapter = new CustomFolderTaskAdapter(iEditDeletePopup);
        binding.rvWork.setAdapter(customFolderTaskAdapter);
    }

    public void onClickNewFolder() {
        folderColor = mColours[0];
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_new_folder);
        dialog.show();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
        GridView gridView = (GridView) dialog.findViewById(R.id.gridView);
        final EditText edtFolderName = (EditText) dialog.findViewById(R.id.edt_folder_name);
        TextView txtSave = (TextView) dialog.findViewById(R.id.txt_save);
        final TextView txtCancel = (TextView) dialog.findViewById(R.id.txt_cancel);
        AppCompatImageView imgFolder = (AppCompatImageView) dialog.findViewById(R.id.img_title_folder);
        gridviewAdapter = new GridviewAdapter(getActivity(), mColours);
        gridView.setAdapter(gridviewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        task_list.add(folderName);
                        dialog.dismiss();
                        getActivity()
                                .getWindow()
                                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        if(!list.isEmpty()){
                            list.clear();
                        }
                        FolderEntity folderTask = new FolderEntity();
                        SubFolderEntity subFolderEntity = new SubFolderEntity();
                        subFolderEntity.setParentFolder(title);
                        subFolderEntity.setChildFolder(folderName);

                        folderTask.setInsertedFrom(title);
                        folderTask.setFolderName(folderName);
                        folderTask.setColor(folderColor);
                        list.add(folderTask);
                        MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                        AppExecutors appExecutors = new AppExecutors();
                        appExecutors.getExeDiskIO().execute(()->{
                            FolderEntity entityFromDB = database.getFolderDao().getFolderByFrom(folderName, title);
                            if(entityFromDB == null){
                                database.getSubFolderDao().insertSubFolder(subFolderEntity);
                                database.getFolderDao().insertAllFolder(list);
                                onUiThread(false, FOLDER);
                                Log.e(TAG, "Data Inserted in Folder Table-- Folder Column");
                            }else {
                                if(entityFromDB != null && !TextUtils.isEmpty(entityFromDB.getFolderName())){
                                  onUiThread(true, FOLDER);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Enter FolderTask Name", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                }else {
                    customFolderTaskAdapter.setFolderList(list);
                }
            }
        });
    }

    public void onClickAddTask() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_new_task);
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
                            getActivity()
                                    .getWindow()
                                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            if(!list.isEmpty()){
                                list.clear();
                            }
                            FolderEntity folderTask = new FolderEntity();
                            AddTaskDetails addTaskDetails = new AddTaskDetails();
                            addTaskDetails.setTaskName(task_name);
                            addTaskDetails.setParentColumn(title);
                            folderTask.setInsertedFrom(title);
                            folderTask.setTaskDetails(addTaskDetails);
                            list.add(folderTask);

                            TaskDetailsEntity taskDetailsEntity = new TaskDetailsEntity();
                            taskDetailsEntity.setTaskName(task_name);
                            taskDetailsEntity.setParentColumn(title);

                            MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                            AppExecutors appExecutors = new AppExecutors();
                            appExecutors.getExeDiskIO().execute(()->{
                                TaskDetailsEntity entityFromDB =
                                        DataRepository.getDataRepository(database).getTaskByName(task_name, title);
                                if(entityFromDB == null){
                                    database.getFolderDao().insertAllFolder(list);
                                    database.getTaskDetailsDao().insertTaskDetails(taskDetailsEntity);
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


    @Override
    public void getClickEvent(String str_click) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txt_new_folder:
                onClickNewFolder();
                break;

            case R.id.img_add_task:
                onClickAddTask();
                break;

            default:
                break;

        }

    }
}
