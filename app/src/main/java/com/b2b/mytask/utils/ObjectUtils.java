package com.b2b.mytask.utils;

public class ObjectUtils {

    public static int getIntFromString(String val){
        int result = 0;
        try{
            result = Integer.parseInt(val);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return result;
    }
}
