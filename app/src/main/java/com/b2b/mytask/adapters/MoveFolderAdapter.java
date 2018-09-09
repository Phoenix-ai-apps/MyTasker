package com.b2b.mytask.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.mytask.R;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.databinding.TemplateTaskFolderItemBinding;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.interfaces.Folder;
import com.b2b.mytask.ui.AddTaskActivity;
import com.b2b.mytask.ui.fragment.AddTaskActivity.MoveFolderFragment;
import com.b2b.mytask.utils.ApplicationUtils;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;
import java.util.Objects;

/**
 * Created by root on 23/4/18.
 */

public class MoveFolderAdapter extends RecyclerSwipeAdapter<MoveFolderAdapter.RecyclerViewHolders> implements AllConstants {

    List<? extends Folder> mFolderList;
    private AppCompatActivity activity;
    private Dialog dialog = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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
        RecyclerViewHolders rcv = new RecyclerViewHolders(binding);

        sharedPreferences = activity.getSharedPreferences(ApplicationUtils.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        final FolderEntity task = (FolderEntity) mFolderList.get(position);
        holder.binding.swipeItem.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.binding.swipeItem.setLeftSwipeEnabled(false);
        holder.binding.swipeItem.setRightSwipeEnabled(false);
        // Drag From Left
        // holder.binding.swipeItem.addDrag(SwipeLayout.DragEdge.Right, holder.binding.swipeItem.findViewById(R.id.linear_swipe));
        if (task != null && !TextUtils.isEmpty(task.getFolderName())) {
            holder.binding.setFolder(task);
            holder.binding.imgFolder.setColorFilter(new PorterDuffColorFilter(task.getColor(), PorterDuff.Mode.SRC_IN));
            holder.binding.txtSeenInbox.setVisibility(View.GONE);
            holder.binding.txtUnseenInbox.setVisibility(View.GONE);
            holder.binding.linearMain.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (task != null && !TextUtils.isEmpty(task.getFolderName())) {
                                Bundle bundle = new Bundle();
                                if (task != null && !TextUtils.isEmpty(task.getFolderName())) {
                                    bundle.putString(TITLE, task.getFolderName());
                                }
                                bundle.putParcelable(FOLDER_OBJ, task);
                                bundle.putString(MOVE_FROM, String.valueOf(task.getId()));

                                MoveFolderFragment fragment = new MoveFolderFragment();
                                fragment.setArguments(bundle);
                                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frameLayout_main, fragment).addToBackStack(null);
                                transaction.commitAllowingStateLoss();

                            } else {
                                Bundle bundle = new Bundle();
                                if (task != null && task.getTaskDetails() != null
                                        && !TextUtils.isEmpty(task.getTaskDetails().getTaskName())) {
                                    bundle.putString(TITLE, task.getTaskDetails().getTaskName());
                                }
                                bundle.putParcelable(FOLDER_OBJ, task);
                                ApplicationUtils.startActivityIntent(activity, AddTaskActivity.class, bundle);
                            }
                        }
                    });

            holder.binding.executePendingBindings();
            // mItemManger is member in RecyclerSwipeAdapter Class
            mItemManger.bindView(holder.itemView, position);
        }
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
