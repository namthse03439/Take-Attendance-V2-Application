package com.example.intern.takeattendanceapplicationv2.Information;

/**
 * Created by Sonata on 6/3/2016.
 */
import org.json.JSONArray;
import org.json.JSONObject;

public class ScheduleManager {
    public final static String[] dailyTime = { "8:00AM", "9:00AM", "10:00AM", "11:00AM", "12:00PM",
            "1:00PM", "2:00PM", "3:00PM", "4:00PM", "5.00PM"};

    public final static int timeNumber = 10;

    public final static int weekId = 0;

    JSONArray semesterSchedule;
    JSONArray dailySchedule = null;
    boolean isTakeAttendace[] = new boolean[timeNumber];
    public int currentLessionIndex;

    public void setSchedule(JSONArray schedule)
    {
        semesterSchedule = schedule;
    }
    public void setDailySchedule(JSONArray schedule)
    {
        if (schedule != null)
        {
            dailySchedule = schedule;
            for (int i = 0; i < timeNumber; i++)
            {
                isTakeAttendace[i] = false;
            }
        }
    }
    public void setIsTakeAttendance(int i, boolean value)
    {
        isTakeAttendace[i] = value;
    }
    public boolean isTakeAttendance(int i)
    {
        return isTakeAttendace[i];
    }
    public void setCurrentLession(int index)
    {
        currentLessionIndex = index;
    }
    public boolean isInitDailySchedule()
    {
        return (dailySchedule != null);
    }
    public JSONObject getWeeklySchedule(int weekId)
    {
        JSONObject data = null;
        try
        {
            data = semesterSchedule.getJSONObject(weekId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;
    }

    public JSONArray getDailySchedule()
    {
        return dailySchedule;
    }



}
