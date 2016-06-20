package com.example.intern.takeattendanceapplicationv2.BaseClass;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Tung on 5/30/2016.
 */
public class SignupClass {
    String username = "NULL";
    String password = "NULL";
    String email = "NULL";
    String SID = "NULL";
    String device_hash = "NULL";

    public SignupClass() {}

    public SignupClass(String _username, String _password, String _email, String _SID, Context context){
        username = _username;
        password = _password;
        email = _email;
        SID = _SID;
        device_hash = getMac(context);
    }

    private String getMac(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        return address;
    }

}
