package com.example.intern.takeattendanceapplicationv2;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.internal.Excluder;
import com.example.intern.takeattendanceapplicationv2.BaseClass.ErrorClass;
import com.example.intern.takeattendanceapplicationv2.BaseClass.GlobalVariable;
import com.example.intern.takeattendanceapplicationv2.BaseClass.Notification;
import com.example.intern.takeattendanceapplicationv2.BaseClass.ServiceGenerator;
import com.example.intern.takeattendanceapplicationv2.BaseClass.StringClient;
import com.example.intern.takeattendanceapplicationv2.BaseClass.TakeAttendanceClass;
import com.example.intern.takeattendanceapplicationv2.Fragment.TakeAttendanceToday;
import com.example.intern.takeattendanceapplicationv2.Fragment.TrainingFragment;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DetailedInformationActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;

    private BeaconManager beaconManager;
    private Region region;

    Button mBeaconInRangeBtn;
    Button mCaptureImageBtn;

    boolean remindDiscover = false;
    int currentIndex;
    boolean isTakeAttendance;
    JSONObject subject;

    Animation animation = null;

    boolean inStage;
    boolean outStage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_information);

        beaconManager = new BeaconManager(this);

        currentIndex = GlobalVariable.scheduleManager.currentLessionIndex;
        isTakeAttendance = GlobalVariable.scheduleManager.isTakeAttendance(currentIndex);
        inStage = isTakeAttendance;
        JSONArray schedule = GlobalVariable.scheduleManager.getDailySchedule();
        try
        {
            subject = schedule.getJSONObject(currentIndex);

            String UUIDs = subject.getString("uuid");
            int major = Integer.parseInt(subject.getString("major"));
            int minor = Integer.parseInt(subject.getString("minor"));

            region = new Region("ranged region", UUID.fromString(UUIDs), major, minor);

        } catch (Exception e)
        {
            e.printStackTrace();
            ErrorClass.showError(this, 26);
        }

        getSubjectInformation();
        initDetailedData();

        mBeaconInRangeBtn = (Button) findViewById(R.id.btn_beaconInRange);
        mBeaconInRangeBtn.setWidth(120);
        mBeaconInRangeBtn.setLines(2);

        mCaptureImageBtn = (Button) findViewById(R.id.btn_captureImage);
        mCaptureImageBtn.setWidth(120);
        mCaptureImageBtn.setLines(2);

        if (isTakeAttendance)
        {
            mBeaconInRangeBtn.setVisibility(Button.INVISIBLE);
            mCaptureImageBtn.setVisibility(Button.INVISIBLE);
        }
        else
        {
            initBeaconEvent();
            initBlinkingButton();

            mBeaconInRangeBtn.startAnimation(animation);
            mBeaconInRangeBtn.setVisibility(Button.VISIBLE);
            mBeaconInRangeBtn.setBackgroundColor(Color.parseColor("#cc0000"));
            mBeaconInRangeBtn.setTextColor(Color.WHITE);
            addListenerToBeaconInRangerBtn();

            mCaptureImageBtn.setVisibility(Button.INVISIBLE);
            mCaptureImageBtn.setBackgroundColor(Color.parseColor("#008000"));
            mCaptureImageBtn.setTextColor(Color.WHITE);
            mCaptureImageBtn.setVisibility(Button.INVISIBLE);
            addListenerToCaptureImageBtn();
        }
    }

    protected void getSubjectInformation()
    {
        currentIndex = GlobalVariable.scheduleManager.currentLessionIndex;
        isTakeAttendance = GlobalVariable.scheduleManager.isTakeAttendance(currentIndex);
        JSONArray schedule = GlobalVariable.scheduleManager.getDailySchedule();
        try
        {
            subject = schedule.getJSONObject(currentIndex);

            String UUIDs = subject.getString("uuid");
            int major = Integer.parseInt(subject.getString("major"));
            int minor = Integer.parseInt(subject.getString("minor"));

            region = new Region("ranged region", UUID.fromString(UUIDs), major, minor);

        } catch (Exception e)
        {
            e.printStackTrace();
            ErrorClass.showError(this, 26);
        }
    }

    private void initBlinkingButton()
    {
        animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
    }

    private void addListenerToBeaconInRangerBtn()
    {
        mBeaconInRangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remindDiscover)
                {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(DetailedInformationActivity.this);
                    builder2.setMessage("Please capture image to take your attendance!");
                    builder2.setCancelable(true);

                    builder2.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert12 = builder2.create();
                    alert12.show();
                }
                else
                {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(DetailedInformationActivity.this);
                    builder2.setMessage("Please stay close to the beacon\n " +
                            "and wait Beacon In Range change to green color!");
                    builder2.setCancelable(true);

                    builder2.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert12 = builder2.create();
                    alert12.show();
                }
            }
        });
    }

    private void addListenerToCaptureImageBtn()
    {
        mCaptureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.clearAnimation();
                if (remindDiscover)
                {
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    String getTime()
    {
        String result = "";
        try
        {
            String time = subject.getString("start_time");
            String weekDay = subject.getString("weekday");

            result = time + " " + weekDay;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ErrorClass.showError(this, 25);
        }

        return result;
    }

    public void initDetailedData()
    {
        try
        {
            int status = Integer.parseInt(subject.getString("status"));
            for(int i = 0; i < 7; i++)
            {
                TextView tv = new TextView(this);
                String text = null;
                int id = -1;

                switch (i)
                {
                    case 0:
                        text = subject.getString("subject_area");
                        id = R.id.tv1;
                        break;

                    case 1:
                        text = subject.getString("class_section");
                        id = R.id.tv2;
                        break;

                    case 2:
                        text = "Mr.Zhang Qinjie";
                        id = R.id.tv3;
                        break;

                    case 3:
                        text = subject.getString("location");
                        id = R.id.tv4;
                        break;

                    case 4:
                        text = getTime();
                        id = R.id.tv5;
                        break;

                    case 5:
                        id = R.id.tv6;

                        switch (status) {
                            case 0:
                                tv = (TextView) findViewById(id);
                                text = "NOT YET";
                                tv.setText(text);
                                tv.setTextColor(Color.parseColor("#C0C0C0"));
                                break;
                            case 1:
                                tv = (TextView) findViewById(id);
                                text = "PRESENT";
                                tv.setText(text);
                                tv.setTextColor(Color.parseColor("#00FF7F"));
                                break;
                            case 2:
                                tv = (TextView) findViewById(id);
                                text = "LATE";
                                tv.setText(text);
                                tv.setTextColor(Color.parseColor("#FFA500"));
                                break;
                            case 3:
                                tv = (TextView) findViewById(id);
                                text = "ABSENT";
                                tv.setText(text);
                                tv.setTextColor(Color.parseColor("#CC0000"));
                                break;
                        }
                        break;

                    case 6:
                        text = subject.getString("recorded_at");
                        if (text.equals("null"))
                        {
                            text = "--:--";
                        }

                        id = R.id.tv7;
                        break;
                }

                try
                {
                    if (i != 5)
                    {
                        tv = (TextView) findViewById(id);
                        tv.setText(text);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    ErrorClass.showError(this, 29);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ErrorClass.showError(this, 24);
        }
    }

    private void initBeaconEvent()
    {
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon = list.get(0);

                    int txPower = beacon.getMeasuredPower();
                    double rssi = beacon.getRssi();
                    double distance = calculateDistance(txPower, rssi);

                    if (distance <= 5.0) {
                        if (!remindDiscover) {
                            remindDiscover = true;
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(DetailedInformationActivity.this);
                            builder2.setMessage("You are in right location!");
                            builder2.setCancelable(true);

                            builder2.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert12 = builder2.create();
                            alert12.show();

                            mBeaconInRangeBtn.clearAnimation();
                            mBeaconInRangeBtn.setBackgroundColor(Color.parseColor("#008000"));

                            mCaptureImageBtn.setVisibility(Button.VISIBLE);
                            mCaptureImageBtn.startAnimation(animation);
                        }
                    } else {
                        remindDiscover = false;

                        mBeaconInRangeBtn.setBackgroundColor(Color.parseColor("#cc0000"));
                        mBeaconInRangeBtn.startAnimation(animation);

                        mCaptureImageBtn.clearAnimation();
                        mCaptureImageBtn.setVisibility(Button.INVISIBLE);
                    }
                }
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try
                {
                    String UUIDs = subject.getString("uuid");
                    int major = Integer.parseInt(subject.getString("major"));
                    int minor = Integer.parseInt(subject.getString("minor"));

                    region = new Region("ranged region", UUID.fromString(UUIDs), major, minor);
                    beaconManager.startRanging(region);

//                    beaconManager.startMonitoring(new Region("monitored region",
//                            UUID.fromString(UUIDs), major, minor));
                } catch (Exception e)
                {
                    e.printStackTrace();
                    ErrorClass.showError(DetailedInformationActivity.this, 23);
                }
            }
        });

//        try
//        {
//            String UUIDs = subject.getString("uuid");
//            int major = Integer.parseInt(subject.getString("major"));
//            int minor = Integer.parseInt(subject.getString("minor"));
//
//            region = new Region("ranged region", UUID.fromString(UUIDs), major, minor);
//
//            beaconManager.startRanging(region);

//            beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
//                @Override
//                public void onEnteredRegion(Region region, List<Beacon> list) {
//                    AlertDialog.Builder builder2 = new AlertDialog.Builder(DetailedInformationActivity.this);
//                    builder2.setMessage("onEnteredRegion!");
//                    builder2.setCancelable(true);
//
//                    builder2.setPositiveButton(
//                            "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//
//                    AlertDialog alert12 = builder2.create();
//                    alert12.show();
//                }
//
//                @Override
//                public void onExitedRegion(Region region) {
//                    AlertDialog.Builder builder2 = new AlertDialog.Builder(DetailedInformationActivity.this);
//                    builder2.setMessage("onExitedRegion!");
//                    builder2.setCancelable(true);
//
//                    builder2.setPositiveButton(
//                            "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//
//                    AlertDialog alert12 = builder2.create();
//                    alert12.show();
//                }
//            });

            beaconManager.setForegroundScanPeriod(5000, 2000);
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }

    protected static double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
                ErrorClass.showError(this, 22);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);

        outStage = GlobalVariable.scheduleManager.isTakeAttendance(currentIndex);
        if (inStage != outStage)
        {
            resultIntent.putExtra(TakeAttendanceToday.UPDATE_SCHEDULE_VIEW, 1);
        }
        else
        {
            resultIntent.putExtra(TakeAttendanceToday.UPDATE_SCHEDULE_VIEW, 0);
        }

        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            VerifyThread verifyThread = new VerifyThread(mCurrentPhotoPath, this, beaconManager, region);
            verifyThread.start();
        }
        else
        {
            mCaptureImageBtn.startAnimation(animation);
        }
    }
}

class VerifyThread extends Thread{
    Thread t;
    String mCurrentPhotoPath = null;
    Activity activity;
    BeaconManager beaconManager;
    Region region;
    public VerifyThread(String _mCurrentPhotoPath, Activity _activity, BeaconManager _beaconManager, Region _region) {
        mCurrentPhotoPath = _mCurrentPhotoPath;
        activity = _activity;
        beaconManager = _beaconManager;
        region = _region;
    }

    public void run() {

        Preferences.showLoading(activity, "Verifying on progress ...", "");

        HttpRequests httpRequests = new HttpRequests(GlobalVariable.apiKey, GlobalVariable.apiSecret);
        File imgFile = new File(mCurrentPhotoPath);
        GlobalVariable.resizeImage(activity, mCurrentPhotoPath);

        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        String personID = GlobalVariable.getThisPersonID(activity, auCode);
        if(personID.compareTo("") != 0){
            String faceID = GlobalVariable.get1FaceID(activity, httpRequests, imgFile);
            double result = getVerification(httpRequests, personID, faceID);

            final JSONObject serverResult = sendResultToLocalServer(result);
            try
            {
                String record_at = serverResult.getString("recorded_at");
                if (!record_at.isEmpty())
                {
                    GlobalVariable.scheduleManager.updateSchedule(serverResult);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            beaconManager.stopRanging(region);

                            Button captureImage = (Button) activity.findViewById(R.id.btn_captureImage);
                            Button beaconInRange = (Button) activity.findViewById(R.id.btn_beaconInRange);

                            captureImage.clearAnimation();
                            beaconInRange.clearAnimation();

                            captureImage.setVisibility(Button.INVISIBLE);
                            beaconInRange.setVisibility(Button.INVISIBLE);

                            TextView status = (TextView) activity.findViewById(R.id.tv6);
                            TextView record_at = (TextView) activity.findViewById(R.id.tv7);
                            try
                            {
                                String result = serverResult.getString("is_late");
                                if (result.compareTo("true") == 0)
                                {
                                    status.setText("LATE");
                                    status.setTextColor(Color.parseColor("#FFA500"));
                                }
                                else
                                {
                                    status.setText("PRESENT");
                                    status.setTextColor(Color.parseColor("#00FF7F"));
                                }

                                record_at.setText(serverResult.getString("recorded_at"));
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            catch (Exception e)
            {

            }

        }
        else{
            Notification.showMessage(activity, 3);
        }
        Preferences.dismissLoading();
    }

    JSONObject sendResultToLocalServer(double result) {

        int currentIndex = GlobalVariable.scheduleManager.currentLessionIndex;
        JSONArray schedule = GlobalVariable.scheduleManager.getDailySchedule();
        try {
            int timetableID = ((JSONObject) schedule.get(currentIndex)).getInt("timetable_id");

            TakeAttendanceClass toUp = new TakeAttendanceClass(timetableID, result);

            SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
            String auCode = pref.getString("authorizationCode", null);

            StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
            Call<ResponseBody> call = client.takeAttendance(toUp);
            Response<ResponseBody> response = call.execute();
            int resCode = response.code();

            if(resCode == 200) { // successful
                String resStr = response.body().string();
                JSONObject resJson = new JSONObject(resStr);
                Notification.showMessage(activity, 1);
                return (resJson);
            }
            else if(resCode == 400) { // face doesn't match
                Notification.showMessage(activity, 2);
                return null;
            }
            else {
                ErrorClass.showError(activity, 20);
                return null;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            ErrorClass.showError(activity, 19);
        }
        return null;
    }

    private double getVerification(HttpRequests httpRequests, String personID, String faceID) {
        double result = 0;
        PostParameters postParameters = new PostParameters().setPersonId(personID).setFaceId(faceID);
        try{
            JSONObject fppResult = httpRequests.recognitionVerify(postParameters);
            result = fppResult.getDouble("confidence");
            if(!fppResult.getBoolean("is_same_person"))
                result = 100 - result;
        }
        catch(Exception e){
            System.out.print("Process interrupted!");
            ErrorClass.showError(activity, 21);
        }

        return result;
    }

}
