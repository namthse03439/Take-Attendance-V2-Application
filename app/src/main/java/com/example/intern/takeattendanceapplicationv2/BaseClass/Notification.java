package com.example.intern.takeattendanceapplicationv2.BaseClass;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intern.takeattendanceapplicationv2.R;

/**
 * Created by Lord One on 6/14/2016.
 */
public class Notification {
    public static String [] messageList = {
            "Trained successfully!", // 0
            "Take Attendance successfully!", // 1
            "Your face doesn't match", // 2
            "Untrained person", // 3
            "Signed up successfully!\nYou must verify your email address before any further request", //4
            "You are not signed up! Please try again!", // 5

    };

    public static String [] LoginNoti = {
            "0: CODE_INCORRECT_USERNAME",
            "1: CODE_INCORRECT_PASSWORD",
            "2: CODE_INCORRECT_DEVICE",
            "3: CODE_UNVERIFIED_EMAIL",
            "4: CODE_UNVERIFIED_DEVICE",
            "5: CODE_UNVERIFIED_EMAIL_DEVICE",
            "6: CODE_INVALID_ACCOUNT"
    };
    public static void showMessage(final Activity activity, final int mesCode) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                activity);

                        builder.setMessage(messageList[mesCode]);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

            }
        });

    }

    public static void showLoginNoti(final Activity activity, final int loginNotiCode){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                activity);

                        builder.setMessage(LoginNoti[loginNotiCode]);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

            }
        });
    }

}
