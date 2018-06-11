package com.b2b.mytask.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.b2b.mytask.R;


/**
 * Created by root on 19/4/18.
 */
public class GridviewAdapter extends BaseAdapter {
    private Context context;
    private int[]   items;
    LayoutInflater  inflater;
    private int     selectedPosition = 0;

    public GridviewAdapter(Context context, int[] items) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.colour_grid_item, null);
        }
        LinearLayout linear_colour = convertView.findViewById(R.id.linear_colour);
        linear_colour.setBackgroundColor(items[position]);
        ImageView img_tick = convertView.findViewById(R.id.img_tick);

        if (position == selectedPosition) {
            img_tick.setVisibility(View.VISIBLE);
        } else {
            img_tick.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }
}

