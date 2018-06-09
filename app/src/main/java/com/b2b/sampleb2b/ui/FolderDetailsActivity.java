package com.b2b.sampleb2b.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.b2b.sampleb2b.AppExecutors;
import com.b2b.sampleb2b.MyTaskApp;
import com.b2b.sampleb2b.R;
import com.b2b.sampleb2b.adapters.FilterBottomDialogAdapter;
import com.b2b.sampleb2b.constants.AllConstants;
import com.b2b.sampleb2b.databinding.ActivityFolderDetailsBinding;
import com.b2b.sampleb2b.db.MyTaskDatabase;
import com.b2b.sampleb2b.db.entities.FolderEntity;
import com.b2b.sampleb2b.db.entities.SubFolderEntity;
import com.b2b.sampleb2b.utils.ApplicationUtils;
import com.b2b.sampleb2b.ui.fragment.FolderDetailsActivity.FolderDetailsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FolderDetailsActivity extends AppCompatActivity implements AllConstants, View.OnClickListener {

    private static final String           TAG = "FolderDetailsActivity";
    private BottomSheetBehavior           mBottomSheetBehavior;
    private List<String>                  listFilter;
    private LinearLayoutManager           mLayoutManager;
    private String                        title;
    private FolderEntity                  folderEntity;
    public ActivityFolderDetailsBinding   binding;
    private FilterBottomDialogAdapter     filterBottomDialogAdapter;
    private MyTaskDatabase                database;
    private AppExecutors                  appExecutors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_folder_details);
        binding.setLifecycleOwner(this);
        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra(TITLE)){
                title = intent.getStringExtra(TITLE);
            }
            if(intent.hasExtra(FOLDER_OBJ)){
                folderEntity = intent.getParcelableExtra(FOLDER_OBJ);
            }
        }
        initializeResources();
    }

    private void initializeResources() {
        appExecutors     = new AppExecutors();
        database         = ((MyTaskApp)getApplicationContext()).getDatabase();
        binding.includeToolbar.imgBackArrow.setOnClickListener(this);
        binding.includeToolbar.aivShare.setOnClickListener(this);
        binding.includeToolbar.imgFilter.setOnClickListener(this);
        binding.includeToolbar.setFolder(folderEntity);
        //Find bottom Sheet ID
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        binding.incFilter.titleDialog.setText(R.string.select_a_filter);
        binding.incFilter.titleDialog.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_swap, 0, 0, 0);

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
        filterBottomDialogAdapter = new FilterBottomDialogAdapter(this, listFilter);
        binding.incFilter.cmnRecView.recyclerview.setAdapter(filterBottomDialogAdapter);
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putParcelable(FOLDER_OBJ, folderEntity);
        FolderDetailsFragment fragment = new FolderDetailsFragment();
        fragment.setArguments(bundle);
        addFragment(fragment);
    }

    public void addFragment(Fragment fragment) {
         FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
         transaction.replace(R.id.frameLayout_main, fragment).addToBackStack(null);
         transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        checkCurrentFolder();
    }

    public void backToPrevious(View view) {
        checkCurrentFolder();
    }

    private void checkCurrentFolder(){
        appExecutors.getExeDiskIO().execute(()->{
            title = binding.includeToolbar.getFolder().getFolderName();
            SubFolderEntity entityFromHome = database.getSubFolderDao().getChildFolderByHome(title, FROM_HOME_FRAGMENT);
                SubFolderEntity subFolderEntity = database.getSubFolderDao().getDataFromChildFolder(title);
                if(subFolderEntity != null && !TextUtils.isEmpty(subFolderEntity.getParentFolder())){
                    FolderEntity folderEntity =
                            database.getFolderDao().getByFolderName(subFolderEntity.getParentFolder());
                    if(folderEntity != null && !TextUtils.isEmpty(folderEntity.getFolderName())){
                        Bundle bundle = new Bundle();
                        binding.setFolder(folderEntity);
                        binding.includeToolbar.setFolder(folderEntity);
                        bundle.putString(TITLE         , folderEntity.getFolderName());
                        bundle.putParcelable(FOLDER_OBJ, folderEntity);
                        FolderDetailsFragment fragment = new FolderDetailsFragment();
                        fragment.setArguments(bundle);
                        addFragment(fragment);
                    }else {
                        finish();
                    }
                }else {
                    finish();
                }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.aiv_share:
                ApplicationUtils.shareData(FolderDetailsActivity.this);
                break;

            case R.id.img_back_arrow:
               checkCurrentFolder();
                break;

            case R.id.img_filter:
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
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
