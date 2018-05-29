package com.b2b.sampleb2b.ui.fragment.FolderDetailsActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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

import com.b2b.sampleb2b.R;
import com.b2b.sampleb2b.adapters.GridviewAdapter;
import com.b2b.sampleb2b.constants.AllConstants;
import com.b2b.sampleb2b.models.FolderTask;
import com.b2b.sampleb2b.interfaces.IEditDeletePopup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cso on 21/4/18.
 */

public class FolderDetailsFragment extends Fragment implements AllConstants, IEditDeletePopup, View.OnClickListener {

    public GridviewAdapter gridviewAdapter;
    // @formatter:off
    @BindView(R.id.rv_work)     RecyclerView rvWork;
    @BindView(R.id.txt_new_folder)         AppCompatTextView txtnewfolder;
    @BindView(R.id.img_add_task)           AppCompatImageView imgTask;
    // @formatter:on
    int folderColor;
    private int[] mColours = new int[0];
    private List<String> task_list = new ArrayList<>();
    private List<FolderTask> list = new ArrayList<>();
    private IEditDeletePopup iEditDeletePopup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_folder_details, container, false);

        ButterKnife.bind(this, rootView);

        initializeResources();

        return rootView;
    }

    private void initializeResources() {

        mColours = getResources().getIntArray(R.array.colours);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvWork.setLayoutManager(manager);
        iEditDeletePopup = (IEditDeletePopup) this;

        imgTask.setOnClickListener(this);
        txtnewfolder.setOnClickListener(this);

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

        gridviewAdapter = new GridviewAdapter(getActivity(), mColours);
        gridView.setAdapter(gridviewAdapter);
        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        gridviewAdapter.setSelectedPosition(position);
                        gridviewAdapter.notifyDataSetChanged();
                        folderColor = mColours[position];
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
                        FolderTask folderTask = new FolderTask();
                        folderTask.setFrom(ADD_FOLDER);
                        folderTask.setFolderName(folderName);
                        folderTask.setColor(folderColor);
                        list.add(folderTask);
                       // rvWork.setAdapter(new CustomFolderTaskAdapter(list, iEditDeletePopup));

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
                            FolderTask folderTask = new FolderTask();
                            folderTask.setFolderName(task_name);
                            folderTask.setFrom(ADD_TASK);
                            folderTask.setColor(0);
                            list.add(folderTask);
                           // rvWork.setAdapter(new CustomFolderTaskAdapter(list, iEditDeletePopup));

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
