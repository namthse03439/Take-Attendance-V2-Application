package com.example.intern.takeattendanceapplicationv2.BaseClass;

/**
 * Created by Tung on 5/30/2016.
 */
public class SignupClass {
    String username = "NULL";
    String password = "NULL";
    String email = "NULL";
    String SID = "NULL";

    public SignupClass(){
    }

    public SignupClass(String _username, String _password, String _email, String _SID){
        username = _username;
        password = _password;
        email = _email;
        SID = _SID;
    }

}
