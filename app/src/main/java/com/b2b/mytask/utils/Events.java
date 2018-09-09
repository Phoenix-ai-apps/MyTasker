package com.b2b.mytask.utils;

import com.b2b.mytask.models.MeetingPeopleDO;

import java.util.ArrayList;


public class Events {

    public static class ActivityActivityMessage {
        private ArrayList<MeetingPeopleDO> message;

        public ActivityActivityMessage(ArrayList<MeetingPeopleDO> message) {
            this.message = message;
        }

        public ArrayList<MeetingPeopleDO> getMessage() {
            return message;
        }
    }

    public static class FragmentToFragment {
        private String message;

        public FragmentToFragment(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

}
