package com.b2b.mytask.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.b2b.mytask.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by root on 21/4/18.
 */

public class ApplicationUtils {

    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static String dueTime;
    public static String dueDate;

    public static String getMonthByNumber(int dayOfWeek) {

        String dayString = null;
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                dayString = "Sunday";
                break;
            case Calendar.MONDAY:
                dayString = "Monday";
                break;
            case Calendar.TUESDAY:
                dayString = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayString = "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayString = "Thursday";
                break;
            case Calendar.FRIDAY:
                dayString = "Friday";
                break;
            case Calendar.SATURDAY:
                dayString = "Saturday";
                break;

        }
        return dayString;
    }


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


    public static void showDialog(final Activity activity, String title) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_close_activity);
        dialog.show();
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        AppCompatTextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        AppCompatTextView txtTitle = dialog.findViewById(R.id.txt_title);
        txtTitle.setText(title);
        final AppCompatTextView btnSave = dialog.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.finish();
                    }
                });

        btnCancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
    }

    public static String showDatePicker(final Context context, final AppCompatTextView txtMeetingDate) {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        dueDate = "";
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                        int day = gregorianCalendar.get(gregorianCalendar.DAY_OF_WEEK);
                        String dayOfWeek = ApplicationUtils.getMonthByNumber(day);

                        String mon = ApplicationUtils.MONTHS[monthOfYear];
                        dueDate = dayOfWeek + " , " + dayOfMonth + " " + mon + " " + year;
                        if (!TextUtils.isEmpty(dueDate)) {
                            if (txtMeetingDate != null) {
                                txtMeetingDate.setText(context.getString(R.string.due) + " " + dueDate);
                            }
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

        return dueDate;


    }

    public static void showTimePicker(final Context context, final AppCompatTextView txtRemindAlarm, final AppCompatTextView txtRepeatalarmDate) {

        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);


        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    int hour, minutes;

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        SimpleDateFormat format = new SimpleDateFormat("HH:MM");
                        try {

                            hour = hourOfDay;
                            minutes = minute;
                            String timeSet = "";
                            if (hour > 12) {
                                hour -= 12;
                                timeSet = "PM";
                            } else if (hour == 0) {
                                hour += 12;
                                timeSet = "AM";
                            } else if (hour == 12) {
                                timeSet = "PM";
                            } else {
                                timeSet = "AM";
                            }

                            String min = "";
                            if (minutes < 10)
                                min = "0" + minutes;
                            else
                                min = String.valueOf(minutes);

                            // Append in a StringBuilder
                            dueTime = new StringBuilder().append(hour).append(':')
                                    .append(min).append(" ").append(timeSet).toString();
                            if (!TextUtils.isEmpty(dueTime)) {
                                txtRemindAlarm.setText(context.getString(R.string.remind_me) + " " + dueTime + " !");
                                txtRepeatalarmDate.setVisibility(View.VISIBLE);

                                if (!TextUtils.isEmpty(dueDate)) {
                                    txtRepeatalarmDate.setText(dueDate);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

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

    public static void hideKeyboard(Activity activity){

        InputMethodManager inputManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }


}
