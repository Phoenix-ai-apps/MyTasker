package com.b2b.sampleb2b.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.b2b.sampleb2b.R;
import com.b2b.sampleb2b.models.TaskByDate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nihar.s on 12/5/18.
 */

public class ShowTaskListAdapter extends RecyclerView.Adapter<ShowTaskListAdapter.RecyclerViewHolders> {

    private Context context;
    private List<TaskByDate> mlstFilter;

    public ShowTaskListAdapter(Context contextm, List<TaskByDate> mlstFilter) {
        this.context = context;
        this.mlstFilter = mlstFilter;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_select_date, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        holder.txtDayofWeek.setText(mlstFilter.get(position).month);
        holder.txtDay.setText(mlstFilter.get(position).day);
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

        @Nullable
        @BindView(R.id.txt_dayofweek)
        AppCompatTextView txtDayofWeek;

        @Nullable
        @BindView(R.id.txt_day)
        AppCompatTextView txtDay;


        @Nullable
        @BindView(R.id.main_linear)
        LinearLayout mainLinear;


        RecyclerViewHolders(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }


}