package com.b2b.sampleb2b.adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.b2b.sampleb2b.databinding.TemplateTaskFolderItemBinding;
import com.b2b.sampleb2b.db.entities.FolderEntity;
import com.b2b.sampleb2b.interfaces.Folder;
import com.b2b.sampleb2b.ui.AddTaskActivity;
import com.b2b.sampleb2b.ui.FolderDetailsActivity;
import com.b2b.sampleb2b.R;
import com.b2b.sampleb2b.constants.AllConstants;
import com.b2b.sampleb2b.utils.ApplicationUtils;
import com.b2b.sampleb2b.models.FolderTask;
import com.b2b.sampleb2b.interfaces.IEditDeletePopup;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 23/4/18.
 */

public class CustomFolderTaskAdapter extends RecyclerSwipeAdapter<CustomFolderTaskAdapter.RecyclerViewHolders> implements AllConstants {

    //private List<FolderTask> list = new ArrayList<>();
    private Activity activity;
    private IEditDeletePopup iEditDeletePopup;
    List<? extends Folder> mFolderList;

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

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Folder newProduct = folderList.get(newItemPosition);
                    Folder oldProduct = mFolderList.get(oldItemPosition);
                    return newProduct.getId() == oldProduct.getId()
                            && Objects.equals(newProduct.getFolderName(), oldProduct.getFolderName())
                            && Objects.equals(newProduct.getFrom(), oldProduct.getFrom())
                            && newProduct.getTaskDetails() == oldProduct.getTaskDetails();
                }
            });
            mFolderList = folderList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        activity = (Activity) parent.getContext();
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
        if(task.getFrom().equalsIgnoreCase(ADD_TASK)){
            holder.binding.imgFolder.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_task));
            holder.binding.txtSeenInbox.setVisibility(View.GONE);
            holder.binding.txtUnseenInbox.setVisibility(View.GONE);
        }else if(task.getFrom().equalsIgnoreCase(ADD_FOLDER)){
            holder.binding.imgFolder.setColorFilter(new PorterDuffColorFilter(task.getColor(), PorterDuff.Mode.SRC_IN));
            holder.binding.txtSeenInbox.setVisibility(View.VISIBLE);
            holder.binding.txtUnseenInbox.setVisibility(View.VISIBLE);
        }

        holder.binding.linearMain.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (task.getFrom().equalsIgnoreCase(ADD_FOLDER)) {
                            Bundle bundle = new Bundle();
                            bundle.putString(TITLE, task.getFolderName());
                            ApplicationUtils.startActivityIntent(activity, FolderDetailsActivity.class, bundle);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString(TITLE, task.getFolderName());
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
                iEditDeletePopup.getClickEvent(SWIPE_DELETE);
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
