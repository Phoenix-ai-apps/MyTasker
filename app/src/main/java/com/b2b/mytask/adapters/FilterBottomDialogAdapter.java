package com.b2b.mytask.adapters;

import android.content.Context;
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
import java.util.List;

/**
 * Created by Nihar.s on 12/5/18.
 */

public class FilterBottomDialogAdapter extends RecyclerView.Adapter<FilterBottomDialogAdapter.RecyclerViewHolders> {

    private Context context;
    private List<String> mlstFilter;

    public FilterBottomDialogAdapter(Context contextm, List<String> mlstFilter) {
        this.context = context;
        this.mlstFilter = mlstFilter;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_filter_row, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
       // holder.txtFilter.setText(mlstFilter.get(position));
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

/*
        @Nullable
        @BindView(R.id.txt_filter)
        AppCompatTextView txtFilter;

        @Nullable
        @BindView(R.id.filter_radio)
        RadioButton filterRadio;

        @Nullable
        @BindView(R.id.filter_main_constraint)
        ConstraintLayout filterMainConstraint;


        @Nullable
        @BindView(R.id.radio_group)
        RadioGroup radioGroup;
*/


        RecyclerViewHolders(View view) {
            super(view);
          //  ButterKnife.bind(this, view);
        }
    }


}