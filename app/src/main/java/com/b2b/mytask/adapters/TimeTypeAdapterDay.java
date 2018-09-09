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
import com.b2b.mytask.databinding.TimeRowBinding;
import com.b2b.mytask.interfaces.iTimeSelected;
import com.b2b.mytask.utils.ApplicationUtils;

import java.util.List;


/**
 * Created by Nihar.s on 28/6/18.
 */

public class TimeTypeAdapterDay extends RecyclerView.Adapter<TimeTypeAdapterDay.RecyclerViewHolders> {

    private int selected_position = -1;
    private Context context;
    private List<Integer> stringListTimeDay;
    private iTimeSelected timeSelected;
    private String str_hours = "", str_minutes = "", str_am_pm = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public TimeTypeAdapterDay(Context contextm, List<Integer> stringListTimeDay, iTimeSelected timeSelected, String str_hours, String str_minutes, String str_am_pm) {
        this.context = contextm;
        this.stringListTimeDay = stringListTimeDay;
        this.timeSelected = timeSelected;
        this.str_am_pm = str_am_pm;
        this.str_hours = str_hours;
        this.str_minutes = str_minutes;

        sharedPreferences = context.getSharedPreferences(ApplicationUtils.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        TimeRowBinding timeRowBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.time_row, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(timeRowBinding);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {


        holder.timeRowBinding.txtTime.setText(stringListTimeDay.get(position).toString());

        if (position == selected_position) {
            holder.timeRowBinding.txtTime.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.circle_shaped_green));
            holder.timeRowBinding.txtTime.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.timeRowBinding.txtTime.setBackgroundDrawable(null);
            holder.timeRowBinding.txtTime.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }


        holder.timeRowBinding.txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_position = position;
                notifyDataSetChanged();
                str_am_pm = "";
                timeSelected.getSelectedTime(stringListTimeDay.get(position).toString(), "1");
            }
        });


        if (!str_am_pm.equalsIgnoreCase("") && str_am_pm.equalsIgnoreCase("AM")) {
            if (stringListTimeDay.get(position).toString().equalsIgnoreCase(str_hours)) {
                if (selected_position == -1) {
                    editor.putString(ApplicationUtils.START_TIME, str_hours + ":" + str_minutes + " " + str_am_pm).commit();
                    holder.timeRowBinding.txtTime.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.circle_shaped_green));
                    holder.timeRowBinding.txtTime.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                } else {
                    str_hours = "";
                    holder.timeRowBinding.txtTime.setBackgroundDrawable(null);
                    holder.timeRowBinding.txtTime.setTextColor(ContextCompat.getColor(context, android.R.color.black));
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
        return stringListTimeDay.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder {

        TimeRowBinding timeRowBinding;

        RecyclerViewHolders(TimeRowBinding timeRowBinding) {
            super(timeRowBinding.getRoot());
            this.timeRowBinding = timeRowBinding;
        }
    }


}
