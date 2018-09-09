package com.b2b.mytask.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.mytask.R;
import com.b2b.mytask.databinding.DateRowBinding;
import com.b2b.mytask.interfaces.iDateSelected;
import com.b2b.mytask.models.TaskByDate;
import com.b2b.mytask.utils.ApplicationUtils;

import java.util.List;

/**
 * Created by Nihar.s on 28/6/18.
 */

public class DateTypeAdapter extends RecyclerView.Adapter<DateTypeAdapter.RecyclerViewHolders> {

    int selected_position = -1;
    private Context context;
    private List<TaskByDate> mlstFilter;
    private iDateSelected dateSelected;
    private String str_date;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public DateTypeAdapter(Context contextm, List<TaskByDate> mlstFilter, iDateSelected dateSelected, String str_date) {
        this.context = contextm;
        this.mlstFilter = mlstFilter;
        this.dateSelected = dateSelected;
        this.str_date = str_date;
        if (str_date != null && !str_date.equalsIgnoreCase("")) {
            selected_position = -1;
        } else {
            selected_position = 1;
        }


        sharedPreferences = context.getSharedPreferences(ApplicationUtils.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        DateRowBinding dateRowBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.date_row, parent, false);
        return new RecyclerViewHolders(dateRowBinding);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {

        holder.dateRowBinding.txtDate.setText(mlstFilter.get(position).dayOfMonth);
        if (position == selected_position) {
            holder.dateRowBinding.linearMain.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_shaped_green));
            holder.dateRowBinding.txtDate.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.dateRowBinding.txtDay.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.dateRowBinding.linearMain.setBackground(null);
            holder.dateRowBinding.txtDate.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.dateRowBinding.txtDay.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        if (position != 0 && position != 8) {
            holder.dateRowBinding.txtDay.setVisibility(View.VISIBLE);
            holder.dateRowBinding.txtDay.setText(mlstFilter.get(position).day);
            holder.dateRowBinding.linearMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_position = position;
                    notifyDataSetChanged();
                    dateSelected.getSelectedDate(mlstFilter.get(position).dayOfMonth, mlstFilter.get(position).month, mlstFilter.get(position).year, mlstFilter.get(position).day, false);
                }
            });
        }

        if (str_date != null && !str_date.equalsIgnoreCase("")) {
            if (mlstFilter.get(position).dayOfMonth.equalsIgnoreCase(ApplicationUtils.getDayOfMonthFromString(str_date))) {
                if (selected_position == -1) {
                    String selected_dt = mlstFilter.get(position).dayOfMonth + "-" + mlstFilter.get(position).month + "-" + mlstFilter.get(position).year;
                    editor.putString(ApplicationUtils.START_DATE, mlstFilter.get(position).day + " " + ApplicationUtils.formatDate(selected_dt));
                    editor.commit();
                    holder.dateRowBinding.linearMain.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_shaped_green));
                    holder.dateRowBinding.txtDate.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                    holder.dateRowBinding.txtDay.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                } else {
                    str_date = "";
                    holder.dateRowBinding.linearMain.setBackground(null);
                    holder.dateRowBinding.txtDate.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                    holder.dateRowBinding.txtDay.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                }
            }

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

        DateRowBinding dateRowBinding;

        RecyclerViewHolders(DateRowBinding dateRowBinding) {
            super(dateRowBinding.getRoot());

            this.dateRowBinding = dateRowBinding;
        }
    }


}
