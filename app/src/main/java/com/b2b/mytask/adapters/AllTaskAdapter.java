package com.b2b.mytask.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.mytask.AppExecutors;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.R;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.databinding.TemplateTaskItemBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.db.entities.TaskDetailsEntity;
import com.b2b.mytask.interfaces.IEditDeletePopup;
import com.b2b.mytask.models.AddTaskDetails;
import com.b2b.mytask.utils.ApplicationUtils;
import com.b2b.mytask.utils.DialogUtils;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Nihar.s on 30/7/18.
 */

public class AllTaskAdapter extends RecyclerSwipeAdapter<AllTaskAdapter.AllTaskViewHolder> implements AllConstants {

    private static final String TAG = "AllTaskAdapter";
    private List<TaskDetailsEntity> detailsEntityList = new ArrayList<>();
    private Context context;
    private String previousDate = "";
    private Dialog dialog = null;
    private IEditDeletePopup iEditDeletePopup;
    private int fillColor;

    public AllTaskAdapter(IEditDeletePopup iEditDeletePopup) {
        this.iEditDeletePopup = iEditDeletePopup;
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

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_item;
    }

    @NonNull
    @Override
    public AllTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = (AppCompatActivity) parent.getContext();
        TemplateTaskItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.template_task_item, parent,
                        false);
        return new AllTaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AllTaskViewHolder holder, int position) {
        TaskDetailsEntity taskDetailsEntity = detailsEntityList.get(position);

        holder.binding.swipeItem.setVisibility(View.VISIBLE);
        holder.binding.swipeItem.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.binding.swipeItem.setLeftSwipeEnabled(true);
        holder.binding.swipeItem.setRightSwipeEnabled(true);
        // Drag From RIght
        holder.binding.swipeItem.addDrag(SwipeLayout.DragEdge.Right, holder.binding.swipeItem.findViewById(R.id.linear_right_swipe));
        // Drag From LEFT
        holder.binding.swipeItem.addDrag(SwipeLayout.DragEdge.Left, holder.binding.swipeItem.findViewById(R.id.linear_left_swipe));

        if (taskDetailsEntity.getTaskFinishStatus().equalsIgnoreCase(DEFAULT_TASK_STATUS)) {
            holder.binding.imgFinish.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done));
            holder.binding.txtTaskStatus.setText(context.getResources().getString(R.string.finish));
            holder.binding.includeTemplate.txtTaskName.addStrike = false;
            holder.binding.includeTemplate.txtTaskName.invalidate();

        } else {
            holder.binding.imgFinish.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_unfinish));
            holder.binding.txtTaskStatus.setText(context.getResources().getString(R.string.unfinish));
            holder.binding.includeTemplate.txtTaskName.addStrike = true;
            holder.binding.includeTemplate.txtTaskName.invalidate();
        }

        if (!previousDate.equalsIgnoreCase(detailsEntityList.get(position).getTaskDate())) {
            holder.binding.linearHeader.setVisibility(View.VISIBLE);
            holder.binding.txtHeaderTask.setText(ApplicationUtils.formatDateAllTask(detailsEntityList.get(position).getTaskDate(), 1));
            previousDate = detailsEntityList.get(position).getTaskDate();
        } else if (previousDate.equalsIgnoreCase(detailsEntityList.get(position).getTaskDate())) {

        } else {
            holder.binding.linearHeader.setVisibility(View.GONE);
        }


        holder.binding.includeTemplate.txtTaskName.setText(taskDetailsEntity.getTaskName());
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
            holder.binding.includeTemplate.imgTaskPriority.setBackgroundTintList(ColorStateList.valueOf(fillColor));
        }

        holder.binding.includeTemplate.txtTaskTime.setText(taskDetailsEntity.getTaskTime());

        holder.binding.includeTemplate.constraintMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousDate = "";
                AppExecutors appExecutors = new AppExecutors();
                appExecutors.getExeDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                        if (database != null) {
                            FolderEntity folderEntity = database.getFolderDao().getDetailsOfTaskFromAllFolder(taskDetailsEntity.getAllfolderid());
                            if (folderEntity != null) {
                                iEditDeletePopup.getClickEvent(SWIPE_EDIT, folderEntity);
                            }
                        }
                    }
                });
            }
        });

        holder.binding.linearDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   iEditDeletePopup.getClickEvent(SWIPE_DELETE);
                // delete from AllFolder where id = task.getId() or InsertedFrom = String.valueOf(task.getId)
                holder.binding.swipeItem.close();
                previousDate = "";
                dialog = DialogUtils.showFolderDeleteDialog(null, taskDetailsEntity, context, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppExecutors appExecutors = new AppExecutors();
                        appExecutors.getExeDiskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                                if (database != null) {

                                    int delete = database.getFolderDao().deleteFolderByFolderID(taskDetailsEntity.getAllfolderid());
                                    Log.e("Adapter", delete + " Folder record deleted");

                                    int deleteTask = database.getTaskDetailsDao().deleteTaskByTaskId(taskDetailsEntity.getTaskId());
                                    Log.e("Adapter", deleteTask + " Task record deleted");


                                }
                                dialog.dismiss();

                            }
                        });
                    }
                });
            }

        });

        holder.binding.linearEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousDate = "";
                holder.binding.swipeItem.close();
                AppExecutors appExecutors = new AppExecutors();
                appExecutors.getExeDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
                        if (database != null) {

                            FolderEntity folderEntity = database.getFolderDao().getDetailsOfTaskFromAllFolder(taskDetailsEntity.getAllfolderid());
                            if (folderEntity != null) {
                                iEditDeletePopup.getClickEvent(SWIPE_EDIT, folderEntity);
                            }

                            Log.e("Adapter", folderEntity + " Folder record deleted");

                        }

                    }
                });
            }
        });

        holder.binding.linearComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.swipeItem.close();
                AppExecutors appExecutors = new AppExecutors();
                appExecutors.getExeDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        previousDate = "";
                        MyTaskDatabase database = ((MyTaskApp) context.getApplicationContext()).getDatabase();

                        if (database != null) {
                            if (holder.binding.txtTaskStatus.getText().toString().trim().equalsIgnoreCase(context.getResources().getString(R.string.finish))) {
                                database.getTaskDetailsDao().updateTaskFinishStatus(taskDetailsEntity.getTaskId(), FINISH_TASK_STATUS);
                                AddTaskDetails addTaskDetails = updateTaskFinishStatusInAllFolder(taskDetailsEntity, FINISH_TASK_STATUS);
                                database.getFolderDao().updateTakDetailsforTaskStatusV1(addTaskDetails, taskDetailsEntity.getAllfolderid());
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.binding.imgFinish.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_unfinish));
                                        holder.binding.txtTaskStatus.setText(context.getResources().getString(R.string.unfinish));
                                        holder.binding.includeTemplate.txtTaskName.addStrike = true;
                                        holder.binding.includeTemplate.txtTaskName.invalidate();
                                    }
                                });

                            } else {
                                database.getTaskDetailsDao().updateTaskFinishStatus(taskDetailsEntity.getTaskId(), DEFAULT_TASK_STATUS);
                                AddTaskDetails addTaskDetails = updateTaskFinishStatusInAllFolder(taskDetailsEntity, DEFAULT_TASK_STATUS);
                                database.getFolderDao().updateTakDetailsforTaskStatusV1(addTaskDetails, taskDetailsEntity.getAllfolderid());

                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.binding.imgFinish.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done));
                                        holder.binding.txtTaskStatus.setText(context.getResources().getString(R.string.finish));
                                        holder.binding.includeTemplate.txtTaskName.addStrike = false;
                                        holder.binding.includeTemplate.txtTaskName.invalidate();
                                    }
                                });
                            }

                        }

                    }
                });
            }
        });


        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(holder.itemView, position);
        /*Below Line Used to close all open swipe item*/
        mItemManger.closeAllItems();
    }


    private AddTaskDetails updateTaskFinishStatusInAllFolder(TaskDetailsEntity taskDetailsEntity, String taskStatus) {
        AddTaskDetails addTaskDetails = new AddTaskDetails();
        addTaskDetails.setTaskId(taskDetailsEntity.getTaskId());
        addTaskDetails.setTaskName(taskDetailsEntity.getTaskName());
        addTaskDetails.setTaskPriority(taskDetailsEntity.getTaskPriority());
        addTaskDetails.setTaskRepeatMode(taskDetailsEntity.getTaskRepeatMode());
        addTaskDetails.setTaskNote(taskDetailsEntity.getTaskNote());
        addTaskDetails.setTaskDate(taskDetailsEntity.getTaskDate());
        addTaskDetails.setTaskDate(taskDetailsEntity.getNotificationDate());
        addTaskDetails.setTaskTime(taskDetailsEntity.getTaskTime());
        addTaskDetails.setTaskFinishStatus(taskStatus);
        addTaskDetails.setParentColumn(taskDetailsEntity.getParentColumn());
        addTaskDetails.setAllfolderid(taskDetailsEntity.getAllfolderid());
        return addTaskDetails;
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

        TemplateTaskItemBinding binding;

        public AllTaskViewHolder(TemplateTaskItemBinding itemBinding) {
            super(itemBinding.getRoot());

            this.binding = itemBinding;

        }
    }
}
