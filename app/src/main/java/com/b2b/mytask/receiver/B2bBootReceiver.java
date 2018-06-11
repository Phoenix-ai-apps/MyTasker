package com.b2b.mytask.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class B2bBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){



        }

    }
}
