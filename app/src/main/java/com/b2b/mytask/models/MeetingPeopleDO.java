package com.b2b.mytask.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nihar.s on 5/7/18.
 */

public class MeetingPeopleDO implements Parcelable {

    public static final Creator<MeetingPeopleDO> CREATOR = new Creator<MeetingPeopleDO>() {
        @Override
        public MeetingPeopleDO createFromParcel(Parcel source) {
            return new MeetingPeopleDO(source);
        }

        @Override
        public MeetingPeopleDO[] newArray(int size) {
            return new MeetingPeopleDO[size];
        }
    };
    public int user_id;
    public String user_name;

    public MeetingPeopleDO() {
    }

    protected MeetingPeopleDO(Parcel in) {
        this.user_id = in.readInt();
        this.user_name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.user_id);
        dest.writeString(this.user_name);
    }

    @Override
    public boolean equals(Object obj) {

        return (this.user_id == ((MeetingPeopleDO)obj).user_id);
    }
}
