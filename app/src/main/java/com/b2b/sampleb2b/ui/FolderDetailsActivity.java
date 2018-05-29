package com.b2b.sampleb2b.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.b2b.sampleb2b.R;
import com.b2b.sampleb2b.adapters.FilterBottomDialogAdapter;
import com.b2b.sampleb2b.constants.AllConstants;
import com.b2b.sampleb2b.utils.ApplicationUtils;
import com.b2b.sampleb2b.ui.fragment.FolderDetailsActivity.FolderDetailsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FolderDetailsActivity extends AppCompatActivity implements AllConstants, View.OnClickListener {

    private static final String TAG = "FolderDetailsActivity";

    //@formatter:off

    @BindView(R.id.txt_toolbar_title)       AppCompatTextView txtToolbarTitle;
    @BindView(R.id.img_back_arrow)          AppCompatImageView imgBack;
    @BindView(R.id.aiv_share)               AppCompatImageView aivShare;
    @BindView(R.id.img_filter)              AppCompatImageView imgFilter;
    @BindView(R.id.recyclerview)            RecyclerView recyclerview;
    @BindView(R.id.title_dialog)            AppCompatTextView titleBottomDialog;

     //@formatter:on

    private BottomSheetBehavior mBottomSheetBehavior;
    private List<String> listFilter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_details);

        ButterKnife.bind(this);

        initializeResources();

    }

    private void initializeResources() {
        Bundle bundle = this.getIntent().getExtras();
        String title = bundle.getString(TITLE);
        txtToolbarTitle.setText(title);

        imgBack.setOnClickListener(this);
        aivShare.setOnClickListener(this);
        imgFilter.setOnClickListener(this);

        addFragment(new FolderDetailsFragment());

        //Find bottom Sheet ID
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        titleBottomDialog.setText(R.string.select_a_filter);
        titleBottomDialog.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_swap, 0, 0, 0);

        //By default set BottomSheet Behavior as Collapsed and Height 0
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setPeekHeight(0);

        //If you want to handle callback of Sheet Behavior you can use below code
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.d(TAG, "State Collapsed");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.d(TAG, "State Dragging");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.d(TAG, "State Expanded");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d(TAG, "State Hidden");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.d(TAG, "State Settling");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        listFilter = new ArrayList<>();
        listFilter.add("Filter 1");
        listFilter.add("Filter 2");
        listFilter.add("Filter 3");
        listFilter.add("Filter 4");
        listFilter.add("Filter 5");
        listFilter.add("Filter 6");
        listFilter.add("Filter 1");
        listFilter.add("Filter 2");
        listFilter.add("Filter 3");
        listFilter.add("Filter 4");
        listFilter.add("Filter 5");
        listFilter.add("Filter 6");


        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

    }


    private void addFragment(FolderDetailsFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_main, fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void backToPrevious(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.aiv_share:
                ApplicationUtils.shareData(FolderDetailsActivity.this);
                break;

            case R.id.img_back_arrow:
                finish();
                break;

            case R.id.img_filter:
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    FilterBottomDialogAdapter filterBottomDialogAdapter = new FilterBottomDialogAdapter(this, listFilter);
                    recyclerview.setAdapter(filterBottomDialogAdapter);
                    recyclerview.setNestedScrollingEnabled(false);
                    filterBottomDialogAdapter.notifyDataSetChanged();
//If state is in collapse mode expand it
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
//else if state is expanded collapse it
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;

        }

    }

}
