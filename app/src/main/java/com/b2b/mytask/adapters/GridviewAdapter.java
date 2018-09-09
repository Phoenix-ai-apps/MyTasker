package com.b2b.mytask.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.b2b.mytask.R;


/**
 * Created by root on 19/4/18.
 */
public class GridviewAdapter extends BaseAdapter {
    LayoutInflater inflater;
    private Context context;
    private int[] items;
    private int selectedPosition = -1;
    private int selectedColor;

    public GridviewAdapter(Context context, int[] items, int selected_color) {
        this.context = context;
        this.items = items;
        this.selectedColor = selected_color;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.colour_grid_item, null);
        }
        LinearLayout linear_colour = convertView.findViewById(R.id.linear_colour);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            linear_colour.setBackgroundTintList(ColorStateList.valueOf(items[position]));
        }

         if (selectedColor != 0) {
            if (selectedColor == items[position]) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    selectedPosition = position;
                }
            }
        }

        ImageView img_tick = convertView.findViewById(R.id.img_tick);

        if (position == selectedPosition) {
            img_tick.setVisibility(View.VISIBLE);
            selectedColor = -1;
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

