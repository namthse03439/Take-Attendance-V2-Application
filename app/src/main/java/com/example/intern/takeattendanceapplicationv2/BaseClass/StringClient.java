package com.example.intern.takeattendanceapplicationv2.BaseClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Tung on 5/31/2016.
 */
public interface StringClient {

    @GET("api/home")
    Call<String> getString();

    @GET("user/logout")
    Call<ResponseBody> logout();

    @GET("api/check-student")
    Call<ResponseBody> checkStudent();

    @GET("user/person-id")
    Call<ResponseBody> getPersonID();

    @GET("user/face-id")
    Call<ResponseBody> getFaceIDList();

    @GET("timetable/week?week=1")
    Call<ResponseBody> getTimetableByWeek();

    @GET("timetable/total-week")
    Call<ResponseBody> getFullTimetable();

    @GET("timetable/today")
    Call<ResponseBody> getTimetableToday();

    @POST("user/login")
    Call<ResponseBody> login(@Body LoginClass up);

    @POST("user/signup")
    Call<ResponseBody> signup(@Body SignupClass user);

    @POST("user/set-face-id")
    Call<ResponseBody> postFaceIDList(@Body ArrayList<String> face_id);

    @POST("user/set-person-id")
    Call<ResponseBody> postPersonID(@Body String person_id);

    @POST("timetable/check-attendance")
    Call<ResponseBody> atAttendanceTime(@Body JSONObject isOntime);

    @POST("timetable/take-attendance")
    Call<ResponseBody> takeAttendance(@Body TakeAttendanceClass toUp);

}
