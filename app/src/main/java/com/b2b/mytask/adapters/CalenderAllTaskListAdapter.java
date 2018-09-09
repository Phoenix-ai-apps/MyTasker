package com.b2b.mytask.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.mytask.AppExecutors;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.R;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.databinding.TemplateCalenderTaskItemBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.db.entities.TaskDetailsEntity;
import com.b2b.mytask.ui.AddTaskActivity;
import com.b2b.mytask.utils.ApplicationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Nihar.s on 30/7/18.
 */

public class CalenderAllTaskListAdapter extends RecyclerView.Adapter<CalenderAllTaskListAdapter.AllTaskViewHolder> implements AllConstants {

    private static final String TAG = "CalenderAllTaskListAdapter";
    private List<TaskDetailsEntity> detailsEntityList = new ArrayList<>();
    private Context context;
    private int fillColor;

    public CalenderAllTaskListAdapter() {
    }

    public void setFolderList(final List<TaskDetailsEntity> taskList) {
        if (detailsEntityList == null) {
            detailsEntityList = taskList;
            notifyItemRangeInserted(0, taskList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return detailsEntityList.size();
                }

                @Override
                public int getNewListSize() {
                    return taskList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return detailsEntityList.get(oldItemPosition).getTaskId() ==
                            taskList.get(newItemPosition).getTaskId();
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    TaskDetailsEntity newProduct = taskList.get(newItemPosition);
                    TaskDetailsEntity oldProduct = detailsEntityList.get(oldItemPosition);
                    return newProduct.getTaskId() == oldProduct.getTaskId()
                            && Objects.equals(newProduct.getTaskName(), oldProduct.getTaskName())
                            && Objects.equals(newProduct.getParentColumn(), oldProduct.getParentColumn())
                            && Objects.equals(newProduct.getTaskDate(), oldProduct.getTaskDate())
                            && Objects.equals(newProduct.getTaskTime(), oldProduct.getTaskTime())
                            && Objects.equals(newProduct.getTaskNote(), oldProduct.getTaskNote())
                            && Objects.equals(newProduct.getTaskPriority(), oldProduct.getTaskPriority())
                            && Objects.equals(newProduct.getTaskRepeatMode(), oldProduct.getTaskRepeatMode());
                }
            });
            detailsEntityList = taskList;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public AllTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = (AppCompatActivity) parent.getContext();
        TemplateCalenderTaskItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.template_calender_task_item, parent,
                        false);
        return new AllTaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AllTaskViewHolder holder, int position) {
        TaskDetailsEntity taskDetailsEntity = detailsEntityList.get(position);
        holder.binding.txtTaskName.setText(taskDetailsEntity.getTaskName());
        if (taskDetailsEntity.getTaskPriority().equalsIgnoreCase("1")) {
            fillColor = context.getResources().getColor(R.color.yellow_500);
        } else if (taskDetailsEntity.getTaskPriority().equalsIgnoreCase("2")) {
            fillColor = context.getResources().getColor(R.color.blue_500);
        } else if (taskDetailsEntity.getTaskPriority().equalsIgnoreCase("3")) {
            fillColor = context.getResources().getColor(R.color.green_500);
        } else if (taskDetailsEntity.getTaskPriority().equalsIgnoreCase("4")) {
            fillColor = context.getResources().getColor(R.color.red_500);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.binding.imgTaskPriority.setBackgroundTintList(ColorStateList.valueOf(fillColor));
        }

        holder.binding.txtTaskTime.setText(taskDetailsEntity.getTaskTime());


        holder.binding.constraintMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AppExecutors appExecutors = new AppExecutors();
                appExecutors.getExeDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                        if (database != null) {

                            FolderEntity folderEntity = database.getFolderDao().getDetailsOfTaskFromAllFolder(taskDetailsEntity.getAllfolderid());
                            Bundle bundle = new Bundle();
                            if (folderEntity != null && folderEntity.getTaskDetails() != null
                                    && !TextUtils.isEmpty(folderEntity.getTaskDetails().getTaskName())) {
                                bundle.putString(TITLE, String.valueOf(folderEntity.getTaskDetails().getTaskName()));
                                bundle.putString(context.getString(R.string.parent_column), folderEntity.getTaskDetails().getParentColumn());
                                bundle.putParcelable(TASK, folderEntity.getTaskDetails());
                                bundle.putParcelable(FOLDER_OBJ, folderEntity);
                                ApplicationUtils.startActivityIntent(context, AddTaskActivity.class, bundle);
                            }
                        }
                    }
                });
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return detailsEntityList.size();
    }

    public class AllTaskViewHolder extends RecyclerView.ViewHolder {

        TemplateCalenderTaskItemBinding binding;

        public AllTaskViewHolder(TemplateCalenderTaskItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;

        }
    }
}
