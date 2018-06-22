package com.b2b.mytask.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import com.b2b.mytask.databinding.TemplateTaskFolderItemBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.interfaces.Folder;
import com.b2b.mytask.ui.AddTaskActivity;
import com.b2b.mytask.ui.FolderDetailsActivity;
import com.b2b.mytask.R;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.ui.fragment.FolderDetailsActivity.FolderDetailsFragment;
import com.b2b.mytask.utils.ApplicationUtils;
import com.b2b.mytask.interfaces.IEditDeletePopup;
import com.b2b.mytask.utils.DialogUtils;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;
import java.util.Objects;

/**
 * Created by root on 23/4/18.
 */

public class CustomFolderTaskAdapter extends RecyclerSwipeAdapter<CustomFolderTaskAdapter.RecyclerViewHolders> implements AllConstants {

    //private List<FolderTask> list = new ArrayList<>();
    private AppCompatActivity activity;
    private IEditDeletePopup iEditDeletePopup;
    List<? extends Folder> mFolderList;
    private Dialog dialog = null;

    public CustomFolderTaskAdapter( IEditDeletePopup iEditDeletePopup) {
        this.iEditDeletePopup = iEditDeletePopup;
    }

    public void setFolderList(final List<? extends Folder> folderList){
        if(mFolderList == null){
            mFolderList = folderList;
            notifyItemRangeInserted(0,folderList.size());
        }else {
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
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.template_task_folder_item, parent,
                        false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(binding);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        final FolderEntity task = (FolderEntity)mFolderList.get(position);
        holder.binding.setFolder(mFolderList.get(position));

        holder.binding.swipeItem.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.binding.swipeItem.setLeftSwipeEnabled(false);
        holder.binding.swipeItem.setRightSwipeEnabled(true);
        // Drag From Left
        holder.binding.swipeItem.addDrag(SwipeLayout.DragEdge.Right, holder.binding.swipeItem.findViewById(R.id.linear_swipe));
        if(task != null && TextUtils.isEmpty(task.getFolderName())){
            holder.binding.imgFolder.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_task));
            holder.binding.txtSeenInbox.setVisibility(View.GONE);
            holder.binding.txtUnseenInbox.setVisibility(View.GONE);
        }else if(task != null && !TextUtils.isEmpty(task.getFolderName())){
            holder.binding.imgFolder.setColorFilter(new PorterDuffColorFilter(task.getColor(), PorterDuff.Mode.SRC_IN));
            holder.binding.txtSeenInbox.setVisibility(View.VISIBLE);
            holder.binding.txtUnseenInbox.setVisibility(View.VISIBLE);
        }

        holder.binding.linearMain.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (task != null && !TextUtils.isEmpty(task.getFolderName())) {
                            Bundle bundle = new Bundle();
                            if(task != null && !TextUtils.isEmpty(task.getFolderName())){
                                bundle.putString(TITLE, task.getFolderName());
                            }
                            bundle.putParcelable(FOLDER_OBJ, task);
                            if(activity instanceof FolderDetailsActivity){
                                FolderDetailsFragment fragment = new FolderDetailsFragment();
                                fragment.setArguments(bundle);
                                ((FolderDetailsActivity) activity).binding.includeToolbar.setFolder(task);
                                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frameLayout_main, fragment).addToBackStack(null);
                                transaction.commitAllowingStateLoss();
                            }else {
                                //Activity Started from Home Fragment
                                ApplicationUtils.startActivityIntent(activity, FolderDetailsActivity.class, bundle);
                            }
                        } else {
                            Bundle bundle = new Bundle();
                            if(task != null && task.getTaskDetails() != null
                                    && !TextUtils.isEmpty(task.getTaskDetails().getTaskName())){
                                bundle.putString(TITLE, task.getTaskDetails().getTaskName());
                            }
                            bundle.putParcelable(FOLDER_OBJ, task);
                            ApplicationUtils.startActivityIntent(activity, AddTaskActivity.class, bundle);
                        }
                    }
                });

        holder.binding.linearEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iEditDeletePopup.getClickEvent(SWIPE_EDIT);
            }
        });
        holder.binding.linearDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   iEditDeletePopup.getClickEvent(SWIPE_DELETE);
                // delete from AllFolder where id = task.getId() or InsertedFrom = String.valueOf(task.getId)
                 dialog = DialogUtils.showFolderDeleteDialog(task, activity, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppExecutors appExecutors = new AppExecutors();
                        appExecutors.getExeDiskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                MyTaskDatabase database = ((MyTaskApp) activity.getApplicationContext()).getDatabase();
                                if(database != null){
                                    int delete = database.getFolderDao().deleteFolderByIdAndInsertedBy(task.getId(), String.valueOf(task.getId()));
                                    Log.e("Adapter",delete +" Folder record deleted");
                                    if(TextUtils.isEmpty(task.getFolderName())){
                                        int deleteTask = database.getTaskDetailsDao().deleteTaskByParent(String.valueOf(task.getId()));
                                        Log.e("Adapter",deleteTask +" Task record deleted");
                                    }
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        });

        holder.binding.executePendingBindings();
        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(holder.itemView, position);
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
