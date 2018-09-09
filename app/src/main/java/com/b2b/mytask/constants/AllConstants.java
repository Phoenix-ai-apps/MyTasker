package com.b2b.mytask.constants;

/**
 * Created by root on 26/4/18.
 */

public interface AllConstants {
    int    DATABASE_VERSION       = 4;
    String DATABASE_NAME          = "myTask.db";
    String TITLE                  = "title";
    String TASK_ID                = "taskId";
    String ADD_FOLDER             = "folder";
    String FROM_HOME_FRAGMENT     = "0";
    String DEFAULT_TASK_STATUS    = "0";
    String FINISH_TASK_STATUS     = "1";
    String SWIPE_EDIT             = "edit";
    String SWIPE_DELETE           = "delete";
    String FOLDER_OBJ             = "folderObj";
    String TASK_OBJ               = "taskObj";
    String FOLDER                 = "folder";
    String TASK                   = "task";
    String TOPIC_GLOBAL           = "global";
    String FOLDER_TYPE            = "folder_type";
    // broadcast receiver intent filters
    String REGISTRATION_COMPLETE  = "registrationComplete";
    String PUSH_NOTIFICATION      = "pushNotification";
    // id to handle the notification in the notification tray
    int NOTIFICATION_ID           = 100;
    int NOTIFICATION_ID_BIG_IMAGE = 101;
    String SHARED_PREF            = "ah_firebase";
    String REPEAT_MODE            = "repeatMode";
    String MOVE_TO                = "moveTo";
    String MOVE_FROM              = "moveFrom";
    String TOOLBAR_TITLE          = "toolbarTitle";
    String FROM_HOME_ALLTASK      = "fromhomealltask";
    String FROM_HOME_COMPLETED    = "fromhomecompleted";
    String NAVIGATES_FROM         = "navigatesfrom";
    String FROM_HOME_THISWEEK     = "fromhomethisweek";
    String FROM_HOME_TODAY        = "fromhometoday";
    String START_DATE_OF_WEEK     = "startdateofweek";
    String END_DATE_OF_WEEK       = "enddateofweek";
    String EVERY_DAY              = "Every Day";
    String EVERY_MONTH            = "Every Month";
    String EVERY_YEAR             = "Every Year";
    String NEVER                  = "Never";
    String EVERY_WEEK             = "Every Week";
    String CURRENT_DATE_OF_WEEK   = "currentdateofweek";


    int PRIORITY_TYPE_LOW         = 1;
    int PRIORITY_TYPE_MEDIUM      = 2;
    int PRIORITY_TYPE_HIGH        = 3;
    int PRIORITY_TYPE_VERY_HIGH   = 4;
    int MAIN_NOTIFICATION_CODE    = 123456789;

    /**
     * Duration of wait
     **/
    int SPLASH_DISPLAY_LENGTH = 1500;




}
