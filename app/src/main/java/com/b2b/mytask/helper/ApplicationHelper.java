package com.b2b.mytask.helper;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public class ApplicationHelper {

    private static ApplicationHelper applicationHelper;

    public static ApplicationHelper getInstance(){
        return applicationHelper = new ApplicationHelper();
    }
}
