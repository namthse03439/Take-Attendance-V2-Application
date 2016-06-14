package com.example.intern.takeattendanceapplicationv2;

/**
 * Created by Sonata on 6/13/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;

public class Preferences {
    public static ProgressDialog loading;
    public static boolean isShownLoading = false;

    public static int requestCode = 0;

    public static void showLoading(Activity context, String title, String message){
        try {
            if (!isShownLoading) {
                loading = ProgressDialog.show(context, title, message, false, false);
                isShownLoading = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void dismissLoading(){
        try {
            if (isShownLoading) {
                loading.dismiss();
                isShownLoading = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
