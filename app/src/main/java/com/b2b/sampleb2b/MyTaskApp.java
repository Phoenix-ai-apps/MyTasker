package com.b2b.sampleb2b;

import android.app.Application;
import android.content.Context;

import com.b2b.sampleb2b.db.MyTaskDatabase;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public class MyTaskApp extends Application {
    private AppExecutors   appExecutors;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        appExecutors = new AppExecutors();
        context = getApplicationContext();
    }

    public MyTaskDatabase getDatabase(){
        return MyTaskDatabase.getInstance(this, appExecutors);
    }

    public DataRepository getDataRepository(){
        return DataRepository.getDataRepository(getDatabase());
    }

    public static Context getInstance(){
        return MyTaskApp.context;
    }

}
