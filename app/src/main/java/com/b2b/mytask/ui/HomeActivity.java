package com.b2b.mytask.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.b2b.mytask.R;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.databinding.ActivityHomeBinding;
import com.b2b.mytask.utils.ApplicationUtils;
import com.b2b.mytask.ui.fragment.HomeActivity.HomeFragment;
import com.b2b.mytask.utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, AllConstants {

    private ActivityHomeBinding homeBinding;
    private BroadcastReceiver   mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initialiseResource();
    }

    public void initialiseResource() {
        homeBinding.incToolbar.imgCalender.setOnClickListener(this);
        homeBinding.incToolbar.imgCalender.setVisibility(View.VISIBLE);
        setToolbar();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);
                    displayFirebaseRegId();

                } else if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    homeBinding.txtPushMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();

        addFragment(new HomeFragment());
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e("HomeActivity.java", "Firebase reg id: " + regId);
       /* if (!TextUtils.isEmpty(regId))
            homeBinding.txtRegId.setText("Firebase Reg Id: " + regId);
        else
            homeBinding.txtRegId.setText("Firebase Reg Id is not received yet!");*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
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
