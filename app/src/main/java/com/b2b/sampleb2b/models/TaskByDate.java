package com.b2b.sampleb2b.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nihar.s on 15/5/18.
 */

public class TaskByDate implements Parcelable {

    public static final Parcelable.Creator<TaskByDate> CREATOR = new Parcelable.Creator<TaskByDate>() {
        @Override
        public TaskByDate createFromParcel(Parcel source) {
            return new TaskByDate(source);
        }

        @Override
        public TaskByDate[] newArray(int size) {
            return new TaskByDate[size];
        }
    };
    public String month;
    public String day;

    public TaskByDate() {
    }

    protected TaskByDate(Parcel in) {
        this.month = in.readString();
        this.day = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.month);
        dest.writeString(this.day);
    }
}
