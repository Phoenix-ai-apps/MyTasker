package com.b2b.mytask.ui;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.b2b.mytask.R;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.databinding.ActivitySplashScreenBinding;
import com.b2b.mytask.utils.ApplicationUtils;

/**
 * Created by Nihar.s on 30/8/18.
 */


public class SplashScreenActivity extends AppCompatActivity implements AllConstants {


    /**
     * Called when the activity is first created.
     */
    private ActivitySplashScreenBinding binding;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/

        //Add a character every 150ms
        binding.typeWriter.setCharacterDelay(70);
        binding.typeWriter.animateText(getString(R.string.app_name));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                ApplicationUtils.simpleIntentFinish(SplashScreenActivity.this, HomeActivity.class, Bundle.EMPTY);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}