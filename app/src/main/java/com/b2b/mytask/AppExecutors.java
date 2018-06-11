package com.b2b.mytask;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public class AppExecutors {

    private final Executor exeDiskIO;
    private final Executor exeNetworkIO;
    private final Executor exeMainThread;

    private AppExecutors(Executor exeDiskIO, Executor exeNetworkIO, Executor exeMainThread){
        this.exeDiskIO     = exeDiskIO;
        this.exeNetworkIO  = exeNetworkIO;
        this.exeMainThread = exeMainThread;
    }

    public Executor getExeDiskIO(){
        return exeDiskIO;
    }

    public Executor getExeNetworkIO(){
        return exeNetworkIO;
    }

    public Executor getExeMainThread(){
        return exeMainThread;
    }

    public AppExecutors(){
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
                new MainThreadExecutor());
    }

    private static class MainThreadExecutor implements Executor{
        private Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            handler.post(command);
        }
    }
}
