package com.b2b.mytask.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.b2b.mytask.R;
import com.b2b.mytask.databinding.TemplateFilterRowBinding;

import java.util.List;

public class FilterBottomDialogAdapter extends RecyclerView.Adapter<FilterBottomDialogAdapter.RecyclerViewHolders> {

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
        if(mlstFilter != null && mlstFilter.size() > 0){
            holder.binding.txtFilter.setText(mlstFilter.get(position));
        }
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