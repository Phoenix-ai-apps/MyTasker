package com.b2b.mytask.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.b2b.mytask.R;
import com.b2b.mytask.ui.fragment.ShowTaskListByDate.TaskListByDateFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowTaskListByDateActivity extends AppCompatActivity implements View.OnClickListener {

    //@formatter:off
    @BindView(R.id.img_back_arrow)          AppCompatImageView imgBack;

    //@formatter:onl

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist_by_date);
        ButterKnife.bind(this);
        initialiseResource();

    }

    public void initialiseResource() {

        setToolbar();
        addFragment(new TaskListByDateFragment());

        imgBack.setOnClickListener(this);
    }

    private void addFragment(TaskListByDateFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_main, fragment);
        transaction.commitAllowingStateLoss();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppCompatTextView toolbarTitle = toolbar.findViewById(R.id.txt_toolbar_title);
        AppCompatImageView img_back_arrow = toolbar.findViewById(R.id.img_back_arrow);
        img_back_arrow.setVisibility(View.VISIBLE);
        toolbarTitle.setText("Task List");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
           /* toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });*/

        }
    }


    public void backToPrevious(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_arrow:
                finish();
                break;


        }
    }
}


