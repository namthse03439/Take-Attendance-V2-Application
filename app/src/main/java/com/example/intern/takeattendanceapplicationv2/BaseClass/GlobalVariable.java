package com.example.intern.takeattendanceapplicationv2.BaseClass;

import com.example.intern.takeattendanceapplicationv2.Information.ScheduleManager;

import org.json.JSONObject;

/**
 * Created by Lord One on 6/7/2016.
 */
public class GlobalVariable {
    public static final String apiKey = "8253a8dfd06d885e754ef8c596d4e809";
    public static final String apiSecret = "HlTQpKjISJ0Fxp1kkd4COSf12-_ErMrH";
    public static final int maxLengthFaceList = 5;
    public static ScheduleManager scheduleManager = new ScheduleManager();
    public static boolean loadedTimetableToday = false;
    public static final double imageArea = 200000;

}
