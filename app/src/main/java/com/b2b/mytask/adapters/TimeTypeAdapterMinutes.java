package com.b2b.mytask.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

public class TimeTypeAdapterMinutes extends RecyclerView.Adapter<TimeTypeAdapterMinutes.RecyclerViewHolders> {

    private int selected_position = -1;
    private Context context;
    private List<Integer> stringListTimeMinutes;
    private iTimeSelected timeSelected;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String str_hours = "", str_minutes = "", str_am_pm = "";


    public TimeTypeAdapterMinutes(Context contextm, List<Integer> stringListTimeMinutes, iTimeSelected timeSelected, String str_hours, String minutes, String str_am_pm) {
        this.context = contextm;
        this.stringListTimeMinutes = stringListTimeMinutes;
        this.str_minutes = minutes;
        this.timeSelected = timeSelected;
        this.str_am_pm = str_am_pm;
        this.str_hours = str_hours;
        sharedPreferences = context.getSharedPreferences(ApplicationUtils.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (!TextUtils.isEmpty(str_minutes)) {
            selected_position = -1;
        } else {
            selected_position = 0;
        }
        if (str_minutes.equalsIgnoreCase("00") || str_minutes.equalsIgnoreCase("05")) {
            str_minutes = str_minutes.replaceFirst("0", "") + "";
        }

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        TimeRowBinding timeRowBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.time_row, parent, false);
        return new RecyclerViewHolders(timeRowBinding);

    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {


        holder.timeRowBinding.txtTime.setText(":" + stringListTimeMinutes.get(position).toString());


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
                str_minutes = "";
                selected_position = position;
                notifyDataSetChanged();
                timeSelected.getSelectedMinutes(stringListTimeMinutes.get(position).toString());
            }
        });


        if (stringListTimeMinutes.get(position).toString().equalsIgnoreCase(str_minutes)) {
            if (selected_position == -1) {
                editor.putString(ApplicationUtils.START_TIME, str_hours + ":" + str_minutes + " " + str_am_pm).commit();
                holder.timeRowBinding.txtTime.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.circle_shaped_green));
                holder.timeRowBinding.txtTime.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            } else {
                holder.timeRowBinding.txtTime.setBackgroundDrawable(null);
                holder.timeRowBinding.txtTime.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }
        }


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return stringListTimeMinutes.size();
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
