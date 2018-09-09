package com.b2b.mytask.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nihar.s on 28/6/18.
 */

public class TaskByDate implements Parcelable {

    public static final Creator<TaskByDate> CREATOR = new Creator<TaskByDate>() {
        @Override
        public TaskByDate createFromParcel(Parcel source) {
            return new TaskByDate(source);
        }

        @Override
        public TaskByDate[] newArray(int size) {
            return new TaskByDate[size];
        }
    };
    public String dayOfMonth;
    public String month;
    public String month_num;
    public String year;
    public String day;
    public boolean isClicked;

    public TaskByDate() {

    }

    protected TaskByDate(Parcel in) {
        this.dayOfMonth = in.readString();
        this.month = in.readString();
        this.year = in.readString();
        this.day = in.readString();
        this.month_num = in.readString();
        this.isClicked = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dayOfMonth);
        dest.writeString(this.month);
        dest.writeString(this.year);
        dest.writeString(this.day);
        dest.writeString(this.month_num);
        dest.writeByte(this.isClicked ? (byte) 1 : (byte) 0);
    }
}
