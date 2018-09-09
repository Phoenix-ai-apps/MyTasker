package com.b2b.mytask.ui.fragment.ShowTaskListByDate;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.b2b.mytask.R;
import com.b2b.mytask.databinding.FragmentTaskListBinding;
import com.b2b.mytask.models.TaskByDate;
import com.b2b.mytask.utils.ApplicationUtils;
import com.b2b.mytask.utils.Events;
import com.b2b.mytask.utils.TasksEventBus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nihar.s on 15/5/18.
 */

public class TaskListByDateFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    private String selected_date = "";
    private FragmentTaskListBinding binding;
    private List<TaskByDate> taskByDateList_final;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_list, container, false);
        binding.setLifecycleOwner(this);
        initialiseResource();
        return binding.getRoot();
    }

    public void initialiseResource() {
        setToolbar();
        taskByDateList_final = new ArrayList<>();

        taskByDateList_final = ApplicationUtils.getNextNPreviousDates();

        setupViewPager(binding.viewpagerListDate, taskByDateList_final);
        binding.tabsListDate.setupWithViewPager(binding.viewpagerListDate);

        binding.viewpagerListDate.setCurrentItem(30);

        //Adding onTabSelectedListener to swipe views
        binding.tabsListDate.addOnTabSelectedListener(this);

    }


    private void setToolbar() {
        binding.includeToolbar.imgBackArrow.setVisibility(View.VISIBLE);
        binding.includeToolbar.txtToolbarTitle.setText("Task List");
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.includeToolbar.toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
           /* toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });*/

        }
    }


    private void setupViewPager(ViewPager viewPager, List<TaskByDate> taskByDateList) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        for (TaskByDate e : taskByDateList) {
            adapter.addFrag(new ViewPagerListByDateFragment(), e.month + "\n " + e.day);
        }
        viewPager.setAdapter(adapter);
        addDivider();
    }

    private void addDivider() {
        LinearLayout linearLayout = (LinearLayout) binding.tabsListDate.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(30);
        linearLayout.setDividerDrawable(drawable);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!TasksEventBus.getBus().isRegistered(this))
            TasksEventBus.getBus().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TasksEventBus.getBus().unregister(this);
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        binding.viewpagerListDate.setCurrentItem(tab.getPosition());

        selected_date = taskByDateList_final.get(tab.getPosition()).year + "-" + taskByDateList_final.get(tab.getPosition()).month_num + "-" + taskByDateList_final.get(tab.getPosition()).day;

        TasksEventBus.getBus().post(new Events.FragmentToFragment(selected_date));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessage(Events.FragmentToFragment activityMessage) {
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
