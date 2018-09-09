package com.b2b.mytask.adapters;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.mytask.AppExecutors;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.R;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.databinding.TemplateTaskFolderItemBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.interfaces.Folder;
import com.b2b.mytask.interfaces.IEditDeletePopup;
import com.b2b.mytask.ui.AddTaskActivity;
import com.b2b.mytask.ui.FolderDetailsActivity;
import com.b2b.mytask.ui.fragment.FolderDetailsActivity.FolderDetailsFragment;
import com.b2b.mytask.utils.ApplicationUtils;
import com.b2b.mytask.utils.DialogUtils;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by root on 23/4/18.
 */

public class CustomFolderTaskAdapter extends RecyclerSwipeAdapter<CustomFolderTaskAdapter.RecyclerViewHolders> implements AllConstants {

    List<? extends Folder> mFolderList;
    private List<FolderEntity> listchild = new ArrayList<>();
    //private List<FolderTask> list = new ArrayList<>();
    private AppCompatActivity activity;
    private IEditDeletePopup iEditDeletePopup;
    private Dialog dialog = null;
    private int taskCount;


    public CustomFolderTaskAdapter(IEditDeletePopup iEditDeletePopup) {
        this.iEditDeletePopup = iEditDeletePopup;
    }

    public void setFolderList(final List<? extends Folder> folderList) {
        if (mFolderList == null) {
            mFolderList = folderList;
            notifyItemRangeInserted(0, folderList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mFolderList.size();
                }

                @Override
                public int getNewListSize() {
                    return folderList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mFolderList.get(oldItemPosition).getId() ==
                            folderList.get(newItemPosition).getId();
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Folder newProduct = folderList.get(newItemPosition);
                    Folder oldProduct = mFolderList.get(oldItemPosition);
                    return newProduct.getId() == oldProduct.getId()
                            && Objects.equals(newProduct.getFolderName(), oldProduct.getFolderName())
                            && Objects.equals(newProduct.getInsertedFrom(), oldProduct.getInsertedFrom())
                            && newProduct.getTaskDetails() == oldProduct.getTaskDetails();
                }
            });
            mFolderList = folderList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        activity = (AppCompatActivity) parent.getContext();
        TemplateTaskFolderItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.template_task_folder_item, parent,
                        false);
        return new RecyclerViewHolders(binding);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {

        final FolderEntity folderEntity = (FolderEntity) mFolderList.get(position);
        holder.binding.swipeItem.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.binding.swipeItem.setRightSwipeEnabled(true);
        holder.binding.swipeItem.setLeftSwipeEnabled(false);
        holder.binding.txtTask.addStrike = false;
        holder.binding.setFolder(mFolderList.get(position));
        // Drag From RIGHT
        holder.binding.swipeItem.addDrag(SwipeLayout.DragEdge.Right, holder.binding.swipeItem.findViewById(R.id.linear_right_swipe));

        if (folderEntity != null && TextUtils.isEmpty(folderEntity.getFolderName())) {
            holder.binding.swipeItem.setLeftSwipeEnabled(true);
            // Drag From Left
            holder.binding.swipeItem.addDrag(SwipeLayout.DragEdge.Left, holder.binding.swipeItem.findViewById(R.id.linear_left_swipe));

             /*TASK*/
            if (folderEntity.getTaskDetails().getTaskFinishStatus().equalsIgnoreCase(DEFAULT_TASK_STATUS)) {
                holder.binding.imgFinish.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_done));
                holder.binding.txtTaskStatus.setText(activity.getResources().getString(R.string.finish));
                holder.binding.txtTask.addStrike = false;
                holder.binding.txtTask.invalidate();

            } else {
                holder.binding.imgFinish.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_unfinish));
                holder.binding.txtTaskStatus.setText(activity.getResources().getString(R.string.unfinish));
                holder.binding.txtTask.addStrike = true;
                holder.binding.txtTask.invalidate();
            }

            holder.binding.imgFolder.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_task));
            holder.binding.imgFolder.setColorFilter(new PorterDuffColorFilter(activity.getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN));

            holder.binding.linearPriority.setVisibility(View.VISIBLE);
            holder.binding.txtPriorityName.setVisibility(View.VISIBLE);
            setPriorityToTask(holder, Integer.parseInt(folderEntity.getTaskDetails().getTaskPriority()));

        } else if (folderEntity != null && !TextUtils.isEmpty(folderEntity.getFolderName())) {
            holder.binding.linearPriority.setVisibility(View.GONE);
            holder.binding.txtPriorityName.setVisibility(View.GONE);
            /*FOLDER*/
            holder.binding.imgFolder.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_white_solid_folder));
            holder.binding.imgFolder.setColorFilter(new PorterDuffColorFilter(folderEntity.getColor(), PorterDuff.Mode.SRC_IN));

        }


        holder.binding.linearMain.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (folderEntity != null && !TextUtils.isEmpty(folderEntity.getFolderName())) {
                            Bundle bundle = new Bundle();
                            if (folderEntity != null && !TextUtils.isEmpty(folderEntity.getFolderName())) {
                                bundle.putString(TITLE, folderEntity.getFolderName());
                            }
                            bundle.putParcelable(FOLDER_OBJ, folderEntity);
                            if (activity instanceof FolderDetailsActivity) {
                                FolderDetailsFragment fragment = new FolderDetailsFragment();
                                fragment.setArguments(bundle);
                                ((FolderDetailsActivity) activity).binding.includeToolbar.setFolder(folderEntity);
                                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frameLayout_main, fragment).addToBackStack(null);
                                transaction.commitAllowingStateLoss();
                            } else {
                                //Activity Started from Home Fragment
                                ApplicationUtils.startActivityIntent(activity, FolderDetailsActivity.class, bundle);
                            }
                        } else {
                            Bundle bundle = new Bundle();
                            if (folderEntity != null && folderEntity.getTaskDetails() != null
                                    && !TextUtils.isEmpty(folderEntity.getTaskDetails().getTaskName())) {
                                bundle.putString(TITLE, String.valueOf(folderEntity.getTaskDetails().getTaskName()));
                                bundle.putString(activity.getString(R.string.parent_column), folderEntity.getTaskDetails().getParentColumn());
                                bundle.putParcelable(TASK, folderEntity.getTaskDetails());
                                bundle.putParcelable(FOLDER_OBJ, folderEntity);
                                ApplicationUtils.startActivityIntent(activity, AddTaskActivity.class, bundle);
                            }
                        }
                    }
                });

        holder.binding.linearEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (folderEntity != null) {
                    holder.binding.swipeItem.close();
                    iEditDeletePopup.getClickEvent(SWIPE_EDIT, folderEntity);
                }
            }
        });
        holder.binding.linearDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   iEditDeletePopup.getClickEvent(SWIPE_DELETE);
                // delete from AllFolder where id = task.getId() or InsertedFrom = String.valueOf(task.getId)
                holder.binding.swipeItem.close();
                dialog = DialogUtils.showFolderDeleteDialog(folderEntity, null, activity, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppExecutors appExecutors = new AppExecutors();
                        appExecutors.getExeDiskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                MyTaskDatabase database = ((MyTaskApp) activity.getApplicationContext()).getDatabase();
                                if (database != null) {
                                    if (folderEntity != null && !TextUtils.isEmpty(folderEntity.getFolderName())) {

                                        int i = 0;
                                        List<Integer> addItems_folders = new ArrayList<>();
                                        List<Integer> addItems_task = new ArrayList<>();
                                        List<FolderEntity> folderEntityList = database.getFolderDao().getListFolderByParent(folderEntity.getId());
                                        for (FolderEntity e : folderEntityList) {
                                            addItems_folders.add(e.getId());
                                        }

                                        while (i < addItems_folders.size()) {
                                            List<FolderEntity> folderEntityList1 = database.getFolderDao().getListFolderByParent(addItems_folders.get(i));
                                            for (FolderEntity e : folderEntityList1) {
                                                addItems_folders.add(e.getId());
                                                if (TextUtils.isEmpty(e.getFolderName())) {
                                                    addItems_task.add(Integer.parseInt(e.getInsertedFrom()));
                                                }
                                            }
                                            i++;
                                        }

                                        addItems_folders.add(folderEntity.getId());
                                        addItems_task.add(folderEntity.getId());
                                        for (Integer e : addItems_folders) {
                                            Log.d(TAG, "run folders: " + e);

                                            database.getFolderDao().deleteFolderByFolderID(e);

                                        }
                                        for (Integer e : addItems_task) {
                                            Log.d(TAG, "run tasks: " + e);
                                            database.getTaskDetailsDao().deleteTaskByInsertedId(e);
                                        }


                                    } else {

                                        /*TASK DELETION*/
                                        int delete = database.getFolderDao().deleteFolderByIdORInsertedBy(folderEntity.getId(), String.valueOf(folderEntity.getId()));

                                        Log.e("Adapter", delete + " Folder record deleted");
                                        if (TextUtils.isEmpty(folderEntity.getFolderName())) {
                                            int deleteTask = database.getTaskDetailsDao().deleteTaskByTaskId(folderEntity.getTaskDetails().getTaskId());
                                            Log.e("Adapter", deleteTask + " Task record deleted");
                                        }
                                    }
                                }
                                dialog.dismiss();

                            }
                        });
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
                        MyTaskDatabase database = ((MyTaskApp) activity.getApplicationContext()).getDatabase();
                        if (database != null) {
                            if (holder.binding.txtTaskStatus.getText().toString().trim().equalsIgnoreCase(activity.getResources().getString(R.string.finish))) {
                                 /*UPDATE IN TASK DETAILS */
                                database.getTaskDetailsDao().updateTaskFinishStatus(folderEntity.getTaskDetails().getTaskId(),
                                        FINISH_TASK_STATUS);
                                 /*UPDATE IN ALL FOLDER*/
                                folderEntity.getTaskDetails().setTaskFinishStatus(FINISH_TASK_STATUS);
/*                                database.getFolderDao().updateTakDetailsforTaskStatus(folderEntity.getTaskDetails(),
                                        folderEntity.getTaskDetails().getTaskId(), String.valueOf(folderEntity.getTaskDetails().getTaskId()));*/
                                database.getFolderDao().updateTakDetailsforTaskStatusV1(folderEntity.getTaskDetails(), folderEntity.getId());
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.binding.imgFinish.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_unfinish));

                                        holder.binding.txtTaskStatus.setText(activity.getResources().getString(R.string.unfinish));
                                        holder.binding.txtTask.addStrike = true;
                                        holder.binding.txtTask.invalidate();
                                    }
                                });

                            } else {
                                   /*UPDATE IN TASK DETAILS */
                                database.getTaskDetailsDao().updateTaskFinishStatus(folderEntity.getTaskDetails().getTaskId(), DEFAULT_TASK_STATUS);
                                 /*UPDATE IN ALL FOLDER*/
                                folderEntity.getTaskDetails().setTaskFinishStatus(DEFAULT_TASK_STATUS);
                                /*database.getFolderDao().updateTakDetailsforTaskStatus(folderEntity.getTaskDetails(),
                                        folderEntity.getTaskDetails().getTaskId(), String.valueOf(folderEntity.getTaskDetails().getTaskId()));*/
                                database.getFolderDao().updateTakDetailsforTaskStatusV1(folderEntity.getTaskDetails(), folderEntity.getId());

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.binding.imgFinish.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_done));
                                        holder.binding.txtTaskStatus.setText(activity.getResources().getString(R.string.finish));
                                        holder.binding.txtTask.addStrike = false;
                                        holder.binding.txtTask.invalidate();
                                    }
                                });
                            }

                        }

                    }
                });
            }
        });
        //holder.binding.executePendingBindings();
        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(holder.itemView, position);
        mItemManger.closeAllItems();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mFolderList == null ? 0 : mFolderList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_item;
    }


    public void setPriorityToTask(RecyclerViewHolders holder, int priority) {

        if (priority == PRIORITY_TYPE_LOW) {
            GradientDrawable gD = new GradientDrawable();
            gD.setColor(activity.getResources().getColor(R.color.yellow_500));
            gD.setShape(GradientDrawable.OVAL);
            holder.binding.linearPriority.setBackground(gD);
            holder.binding.txtPriorityName.setText(activity.getResources().getString(R.string.low));
        } else if (priority == PRIORITY_TYPE_MEDIUM) {
            GradientDrawable gD = new GradientDrawable();
            gD.setColor(activity.getResources().getColor(R.color.blue_500));
            holder.binding.txtPriorityName.setText(activity.getResources().getString(R.string.medium));
            gD.setShape(GradientDrawable.OVAL);
            holder.binding.linearPriority.setBackground(gD);
        } else if (priority == PRIORITY_TYPE_HIGH) {
            GradientDrawable gD = new GradientDrawable();
            gD.setColor(activity.getResources().getColor(R.color.green_500));
            gD.setShape(GradientDrawable.OVAL);
            holder.binding.linearPriority.setBackground(gD);
            holder.binding.txtPriorityName.setText(activity.getResources().getString(R.string.high));
        } else if (priority == PRIORITY_TYPE_VERY_HIGH) {
            GradientDrawable gD = new GradientDrawable();
            gD.setColor(activity.getResources().getColor(R.color.red_500));
            gD.setShape(GradientDrawable.OVAL);
            holder.binding.linearPriority.setBackground(gD);
            holder.binding.txtPriorityName.setText(activity.getResources().getString(R.string.very_high));

        }

    }

    public static class RecyclerViewHolders extends RecyclerView.ViewHolder {
        // @formatter:off
        // @formatter:on
        final TemplateTaskFolderItemBinding binding;

        RecyclerViewHolders(TemplateTaskFolderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
