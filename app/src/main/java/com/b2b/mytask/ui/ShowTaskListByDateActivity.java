package com.b2b.mytask.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.b2b.mytask.R;
import com.b2b.mytask.databinding.ActivityTasklistByDateBinding;
import com.b2b.mytask.ui.fragment.ShowTaskListByDate.TaskListByDateFragment;

public class ShowTaskListByDateActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityTasklistByDateBinding binding;

    private String selectedDate = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tasklist_by_date);

        initialiseResource();

    }

    public void initialiseResource() {
        // setToolbar();
        addFragment(new TaskListByDateFragment());
    }

    private void addFragment(Fragment fragment) {
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
            case R.id.img_back_arrow:
                finish();
                break;
        }
    }


}


