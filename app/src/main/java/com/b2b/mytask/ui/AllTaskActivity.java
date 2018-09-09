package com.b2b.mytask.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.b2b.mytask.R;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.databinding.ActivityAllTaskBinding;
import com.b2b.mytask.ui.fragment.AllTaskActivity.AllTaskFragment;

/**
 * Created by Nihar.s on 30/7/18.
 */

public class AllTaskActivity extends AppCompatActivity implements AllConstants, View.OnClickListener {

    private ActivityAllTaskBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_task);

        initialization();
    }

    private void initialization() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(TOOLBAR_TITLE)) {
                binding.includeToolbar.txtToolbarTitle.setText(bundle.getString(TOOLBAR_TITLE));
            }
        }

        AllTaskFragment fragment = new AllTaskFragment();
        fragment.setArguments(bundle);
        addFragment(fragment);

        binding.includeToolbar.imgBackArrow.setVisibility(View.VISIBLE);
        binding.includeToolbar.imgBackArrow.setOnClickListener(this);
    }

    private void addFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_main, fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_back_arrow:
                finish();
        }
    }
}
