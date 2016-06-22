package com.example.intern.takeattendanceapplicationv2t.BaseClass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.intern.takeattendanceapplicationv2t.Information.ScheduleManager;
import com.example.intern.takeattendanceapplicationv2t.LogInActivity;
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

    public static void resizeImage(Activity activity, String mCurrentPhotoPath) {
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
            ErrorClass.showError(activity, 5);
            e.printStackTrace();
        }
    }

    public static boolean haveFullTimetable(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        String timeTable = pref.getString("fullTimeTable", null);
        return timeTable != null;
    }

    public static void checkLoggedin(final Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getPersonID();

        boolean result = false;

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() != 200) {
                        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("authorizationCode", null);
                        editor.apply();

                        Intent intent = new Intent(activity, LogInActivity.class);
                        activity.startActivity(intent);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    ErrorClass.showError(activity, 6);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorClass.showError(activity, 7);
            }
        });
    }

    public static void loadTimetableByWeek(final Activity activity) {

        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
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
                    ErrorClass.showError(activity, 8);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorClass.showError(activity, 9);
            }
        });

    }

    public static void getFullTimeTable(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        String data = pref.getString("fullTimetable", "[]");
        try {
            JSONArray temp = new JSONArray(data);
            GlobalVariable.scheduleManager.setSchedule(temp);
        }
        catch (Exception e)
        {
            ErrorClass.showError(activity, 0);
            e.printStackTrace();
        }
    }

    public static boolean obtainedAuCode (Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);
        if (auCode != null && auCode != "{\"password\":[\"Incorrect username or password.\"]}"){
            return true;
        }
        return false;
    }

    public static void setAuCodeInSP(Activity activity, String authorizationCode) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("authorizationCode", "Bearer " + authorizationCode);
        editor.apply();
    }

    public static String getThisPersonID(Activity activity, String auCode) {

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
            ErrorClass.showError(activity, 10);
        }

        return personID;
    }

    public static String get1FaceID(Activity activity, HttpRequests httpRequests, File imgFile) {
        String faceID = null;
        try {
            PostParameters postParameters = new PostParameters().setImg(imgFile).setMode("oneface");
            JSONObject faceResult = httpRequests.detectionDetect(postParameters);
            faceID = faceResult.getJSONArray("face").getJSONObject(0).getString("face_id");
        }
        catch(Exception e){
            e.printStackTrace();
            ErrorClass.showError(activity, 11);
        }
        if(faceID == null)
            ErrorClass.showError(activity, 30);
        return faceID;
    }

}
