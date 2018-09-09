package com.b2b.mytask.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.b2b.mytask.R;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.interfaces.iDateSelected;
import com.b2b.mytask.models.TaskByDate;
import com.b2b.mytask.receiver.SetAlarmForAllTaskReceiver_V1;
import com.b2b.mytask.receiver.TaskAlarmNotificationReceiver_V1;
import com.google.gson.Gson;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by root on 21/4/18.
 */

public class ApplicationUtils implements AllConstants {

    public static final String MY_PREF = "MyPref";
    public static final String START_DATE = "StartDate";
    public static final String START_TIME = "StartTime";
    public static final String SELECTED_DATE = "selectedDate";
    public static final String END_DATE = "EndDate";
    public static final String END_TIME = "EndTime";
    public static final String FROM = "from";
    public static final String GETTASKID = "task_id";
    public static String dueTime;
    public static String dueDate;


    public static void startActivityIntent(Context context, Class aClass, Bundle bundle) {
        Intent intent = new Intent(context, aClass);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void simpleIntentFinish(Activity activity, Class aClass, Bundle bundle) {
        Intent intent = new Intent(activity, aClass);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();

    }

    public static void showDatePicker(final Context context, final iDateSelected iDateSelected) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        final TaskByDate taskByDate = new TaskByDate();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            DatePicker datePicker = datePickerDialog
                                    .getDatePicker();
                            taskByDate.dayOfMonth = String.valueOf(datePicker.getDayOfMonth());
                            taskByDate.month = String.valueOf(datePicker.getMonth() + 1);
                            taskByDate.year = String.valueOf(datePicker.getYear());
                            taskByDate.day = DateFormat.format("EEEE", new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth() - 1)).toString();
                            iDateSelected.getSelectedDate(taskByDate.dayOfMonth, taskByDate.month, taskByDate.year, taskByDate.day, true);

                        }
                    }
                });
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public static void shareData(Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }


    /**
     * add days to date in java
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    /**
     * subtract days to date in java
     *
     * @param date
     * @param days
     * @return
     */
    public static Date subtractDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }

    public static String toJson(Object object) {
        if (object != null) {
            Gson gson = new Gson();
            String json = gson.toJson(object);
            return json;
        } else {
            return "";
        }
    }

    public static void hideKeyboard(Activity activity) {

        InputMethodManager inputManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public static int expand(final View v, int expand_collapse_flag) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        expand_collapse_flag = 0;

        return expand_collapse_flag;
    }

    public static int collapse(final View v, int expand_collapse_flag) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        expand_collapse_flag = 1;
        return expand_collapse_flag;
    }

    public static String getInitialOfName(String str) {
        return str.substring(0, 1).toUpperCase();
    }

    public static void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public static String formatDate(String dt) {
        String inputPattern = "dd-MM-yyyy";
        String outputPattern = "dd MMM, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(dt);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    public static String formatDateAllTask(String str_date, int isConvert) {
        String inputPattern = "", outputPattern = "";
        if (isConvert == 0) {
            inputPattern = "E dd MMM, yyyy";
            outputPattern = "yyyy-MM-dd";
        } else {
            inputPattern = "yyyy-MM-dd";
            outputPattern = "E dd MMM, yyyy";
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(str_date);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /*
    * Method Returns DAY OF MONTH
    * */
    public static String getDayOfMonthFromString(String str_date) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE dd MMM, yyyy");
        String day = null;
        try {
            Date date = format.parse(str_date);
            day = (String) DateFormat.format("dd", date); // 20
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

    /*START DATE OF WEEK ,DATE CALCULATED BY CONSIDERING MONDAY AS FIRST DAY OF WEEK*/
    public static String getStartDateOfCurrentWeek() {
        DecimalFormat mFormat = new DecimalFormat("00");
        mFormat.setRoundingMode(RoundingMode.DOWN);

        Calendar c1 = Calendar.getInstance();
        Calendar cal = (Calendar) c1.clone();
        int day = cal.get(Calendar.DAY_OF_YEAR);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.set(Calendar.DAY_OF_YEAR, --day);
        }
        int year1 = cal.get(Calendar.YEAR);
        int month1 = cal.get(Calendar.MONTH) + 1;
        int day1 = cal.get(Calendar.DAY_OF_MONTH);

        return year1 + "-" + mFormat.format(Double.valueOf(month1)) + "-" + mFormat.format(Double.valueOf(day1));
    }


    public static String getEndDateOfCurrentWeek() {
        DecimalFormat mFormat = new DecimalFormat("00");
        mFormat.setRoundingMode(RoundingMode.DOWN);

        Calendar c1 = Calendar.getInstance();
        Calendar cal = (Calendar) c1.clone();
        int day = cal.get(Calendar.DAY_OF_YEAR);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.set(Calendar.DAY_OF_YEAR, ++day);
        }
        int year7 = cal.get(Calendar.YEAR);
        int month7 = cal.get(Calendar.MONTH) + 1;
        int day7 = cal.get(Calendar.DAY_OF_MONTH);
        return year7 + "-" + mFormat.format(Double.valueOf(month7)) + "-" + mFormat.format(Double.valueOf(day7));
    }

    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c);
    }

    public static List<TaskByDate> getNextNPreviousDates() {
        List<TaskByDate> taskByDateList_after = new ArrayList<>();
        List<TaskByDate> taskByDateList_before = new ArrayList<>();
        List<TaskByDate> taskByDateList_final = new ArrayList<>();

        Date date = new Date();

        int i = 0;
        while (i <= 30) {
            Date newDate = ApplicationUtils.subtractDays(date, i);
            TaskByDate taskByDate = new TaskByDate();
            taskByDate.month = (String) DateFormat.format("MMM", newDate);
            taskByDate.day = (String) DateFormat.format("dd", newDate);
            taskByDate.year = (String) DateFormat.format("yyyy", newDate);
            taskByDate.month_num = (String) DateFormat.format("MM", newDate);
            taskByDateList_before.add(taskByDate);
            i++;
        }
        i = 1;
        while (i <= 30) {
            Date newDate = ApplicationUtils.addDays(date, i);
            TaskByDate taskByDate = new TaskByDate();
            taskByDate.month = (String) DateFormat.format("MMM", newDate);
            taskByDate.day = (String) DateFormat.format("dd", newDate);
            taskByDate.year = (String) DateFormat.format("yyyy", newDate);
            taskByDate.month_num = (String) DateFormat.format("MM", newDate);
            taskByDateList_after.add(taskByDate);
            i++;
        }


        Collections.reverse(taskByDateList_before);
        taskByDateList_final.addAll(taskByDateList_before);
        taskByDateList_final.addAll(taskByDateList_after);
        return taskByDateList_final;

    }

    public static void customToast(Activity activity, String txt_message) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(txt_message);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }

    public static String getNextDateFromCurrentDate(String str_date) {
        String yesterdayAsString = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = dateFormat.parse(str_date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            yesterdayAsString = dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();

        }
        return yesterdayAsString;
    }

    public static String getNextDateOfWeekFromCurrentDate(String str_date) {
        String yesterdayAsString = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = dateFormat.parse(str_date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 7);
            yesterdayAsString = dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();

        }
        return yesterdayAsString;
    }

    public static String getNextDateOfMonthFromCurrentDate(String str_date) {
        String yesterdayAsString = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = dateFormat.parse(str_date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);
            yesterdayAsString = dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();

        }
        return yesterdayAsString;
    }

    public static String getNextDateOfYearFromCurrentDate(String str_date) {
        String yesterdayAsString = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = dateFormat.parse(str_date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, 1);
            yesterdayAsString = dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();

        }
        return yesterdayAsString;
    }

    /*This method will fire every day morning 12:15AM will fetch all data from db from current date.*/
    /*As per Calender DOC,  Noon and midnight are represented by 0, not by 12. */
    public static void fireAlarmDaily12AM(Context context, int alarmCode) {
        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR, 0);
        alarmStartTime.set(Calendar.MINUTE, 15);
        alarmStartTime.set(Calendar.SECOND, 0);
        alarmStartTime.set(Calendar.MILLISECOND, 0);
        alarmStartTime.set(Calendar.AM_PM, Calendar.AM);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, SetAlarmForAllTaskReceiver_V1.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(),
                24 * 60 * 60 * 1000, pendingIntent);

        Log.d(TAG, "cancelTaskAlarm: " + alarmCode + " Cancelled");

    }

    /* This method will cancel existing Task with alarmCode
    * */
    public static void cancelTaskAlarm(Context context, int alarmCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, TaskAlarmNotificationReceiver_V1.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        Log.d(TAG, "cancelTaskAlarm: " + alarmCode + " Cancelled");
    }

    public static boolean comparetimings(String currentTime, String timeFromDB) {

        String pattern = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        boolean isValid = false;

        try {
            Date date1 = sdf.parse(timeFromDB);
            Date date2 = sdf.parse(currentTime);

            if (date1.after(date2)) {
                Log.d(TAG, "checktimings: before");
                isValid = true;
                  /*Add all data in List*/
            } else {
                Log.d(TAG, "checktimings: After");

                isValid = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isValid;
    }


    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
}
