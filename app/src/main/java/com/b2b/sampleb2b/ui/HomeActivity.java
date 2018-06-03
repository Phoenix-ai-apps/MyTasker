package com.b2b.sampleb2b.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.b2b.sampleb2b.R;
import com.b2b.sampleb2b.databinding.ActivityHomeBinding;
import com.b2b.sampleb2b.utils.ApplicationUtils;
import com.b2b.sampleb2b.ui.fragment.HomeActivity.HomeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityHomeBinding homeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        ButterKnife.bind(this);
        initialiseResource();
    }

    public void initialiseResource() {
        homeBinding.incToolbar.imgCalender.setOnClickListener(this);
        homeBinding.incToolbar.imgCalender.setVisibility(View.VISIBLE);
        setToolbar();
        addFragment(new HomeFragment());
    }


    private void addFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_main, fragment);
        transaction.commitAllowingStateLoss();

    }

    private void setToolbar() {
        homeBinding.incToolbar.txtToolbarTitle.setText("My Task");
        setSupportActionBar(  homeBinding.incToolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });*/

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_calender:
                ApplicationUtils.startActivityIntent(this, ShowTaskListByDateActivity.class, Bundle.EMPTY);
        }
    }
}
