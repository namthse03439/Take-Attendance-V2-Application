package com.example.intern.takeattendanceapplicationv2.BaseClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.intern.takeattendanceapplicationv2.Information.ScheduleManager;
import com.example.intern.takeattendanceapplicationv2.LogInActivity;
import com.example.intern.takeattendanceapplicationv2.MainActivity;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public static void resizeImage(Context context, String mCurrentPhotoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();

        double ratio = Math.sqrt(GlobalVariable.imageArea / (oldHeight * oldWidth));

        int newWidth = (int) (oldWidth * ratio);
        int newHeight = (int) (oldHeight * ratio);
        Bitmap resized = bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        File file = new File(mCurrentPhotoPath);
        if(file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            resized.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch(Exception e){
            ErrorClass.showError(context, 5);
            e.printStackTrace();
        }
    }

    public static boolean haveFullTimetable(Context context) {
        SharedPreferences pref = context.getSharedPreferences("ATK_pref", 0);
        String timeTable = pref.getString("fullTimeTable", null);
        return timeTable != null;
    }

    public static void checkLoggedin(final Context context) {
        SharedPreferences pref = context.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getPersonID();

        boolean result = false;

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() != 200) {
                        SharedPreferences pref = context.getSharedPreferences("ATK_pref", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("authorizationCode", null);
                        editor.apply();

                        Intent intent = new Intent(context, LogInActivity.class);
                        context.startActivity(intent);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    ErrorClass.showError(context, 6);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorClass.showError(context, 7);
            }
        });
    }

    public static void loadTimetableByWeek(final Context context) {

        SharedPreferences pref = context.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getTimetableByWeek();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject data = new JSONObject(response.body().string());
                    System.out.print("OK!");
                }
                catch (Exception e){
                    e.printStackTrace();
                    ErrorClass.showError(context, 8);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorClass.showError(context, 9);
            }
        });

    }

    public static void getFullTimeTable(Context context) {
        SharedPreferences pref = context.getSharedPreferences("ATK_pref", 0);
        String data = pref.getString("fullTimetable", "[]");
        try {
            JSONArray temp = new JSONArray(data);
            GlobalVariable.scheduleManager.setSchedule(temp);
        }
        catch (Exception e)
        {
            ErrorClass.showError(context, 0);
            e.printStackTrace();
        }
    }

    public static boolean obtainedAuCode (Context context) {
        SharedPreferences pref = context.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);
        if (auCode != null && auCode != "{\"password\":[\"Incorrect username or password.\"]}"){
            return true;
        }
        return false;
    }

    public static void setAuCodeInSP(Context context, String authorizationCode) {
        SharedPreferences pref = context.getSharedPreferences("ATK_pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("authorizationCode", "Bearer " + authorizationCode);
        editor.apply();
    }

    public static String getThisPersonID(Context context, String auCode) {

        String personID = null;

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getPersonID();

        try {
            Response<ResponseBody> response = call.execute();
            JSONObject data = new JSONObject(response.body().string());
            personID = data.getString("person_id");
        }
        catch(Exception e){
            e.printStackTrace();
            ErrorClass.showError(context, 10);
        }

        return personID;
    }

    public static String get1FaceID(Context context, HttpRequests httpRequests, File imgFile){
        String faceID = null;
        try {
            PostParameters postParameters = new PostParameters().setImg(imgFile).setMode("oneface");
            JSONObject faceResult = httpRequests.detectionDetect(postParameters);
            faceID = faceResult.getJSONArray("face").getJSONObject(0).getString("face_id");
        }
        catch(Exception e){
            e.printStackTrace();
            ErrorClass.showError(context, 11);
        }
        return faceID;
    }
}
