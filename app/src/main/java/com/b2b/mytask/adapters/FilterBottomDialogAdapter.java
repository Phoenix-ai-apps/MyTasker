package com.b2b.mytask.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.mytask.R;
import com.b2b.mytask.databinding.TemplateFilterRowBinding;

import java.util.List;

public class FilterBottomDialogAdapter extends RecyclerView.Adapter<FilterBottomDialogAdapter.RecyclerViewHolders> {

    int selected_position = 1;
    private Context context;
    private List<String> mlstFilter;

    public FilterBottomDialogAdapter(Context context, List<String> mlstFilter) {
        this.context = context;
        this.mlstFilter = mlstFilter;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        TemplateFilterRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.template_filter_row, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(binding);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        if (mlstFilter != null && mlstFilter.size() > 0) {
            holder.binding.txtFilter.setText(mlstFilter.get(position));
        }

        if (position != selected_position) {
            holder.binding.ivTick.setVisibility(View.GONE);
            holder.binding.llRow.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        holder.binding.filterMainConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_position = position;
                notifyDataSetChanged();
                holder.binding.ivTick.setVisibility(View.VISIBLE);
                holder.binding.llRow.setBackgroundColor(context.getResources().getColor(R.color.grey_100));
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mlstFilter.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder {

        final TemplateFilterRowBinding binding;

        RecyclerViewHolders(TemplateFilterRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}