package com.example.intern.takeattendanceapplicationv2t;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intern.takeattendanceapplicationv2t.BaseClass.ErrorClass;
import com.example.intern.takeattendanceapplicationv2t.BaseClass.Notification;
import com.example.intern.takeattendanceapplicationv2t.BaseClass.ServiceGenerator;
import com.example.intern.takeattendanceapplicationv2t.BaseClass.SignupClass;
import com.example.intern.takeattendanceapplicationv2t.BaseClass.StringClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intern.takeattendanceapplicationv2t.BaseClass.ErrorClass;
import com.example.intern.takeattendanceapplicationv2t.BaseClass.Notification;
import com.example.intern.takeattendanceapplicationv2t.BaseClass.ServiceGenerator;
import com.example.intern.takeattendanceapplicationv2t.BaseClass.SignupClass;
import com.example.intern.takeattendanceapplicationv2t.BaseClass.StringClient;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = "ChangePasswordActivity";


    @InjectView(R.id.input_old_password)    EditText _currentpasswordText;
    @InjectView(R.id.input_password)    EditText _passwordText;
    @InjectView(R.id.input_confirmpass) EditText _confirmedPasswordText;
    @InjectView(R.id.btn_changePass)    Button   _changePassButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.inject(this);

        _changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Change Password");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        Preferences.showLoading(ChangePasswordActivity.this, "Change Password", "Processing...");
        _changePassButton.setEnabled(false);

        String currentPassword = _currentpasswordText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmedPassword = _confirmedPasswordText.getText().toString();

        // Interact with local server
        //==========================

        // TODO Tung
//        SignupClass user = new SignupClass(username, password, email, studentId, this);
//        signupAction(user);

        //--------------------------

    }

    public void onSignupSuccess() {
        //TODO
//        Preferences.dismissLoading();
//        setResult(RESULT_OK, null);
//
//        Toast.makeText(getBaseContext(), "Signed up successfully!", Toast.LENGTH_LONG).show();
//
//        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
//        startActivity(intent);

    }

    public void onSignupFailed() {
        //TODO
//        Preferences.dismissLoading();
//        Toast.makeText(getBaseContext(), "Signup failed!", Toast.LENGTH_LONG).show();
//
//        _signupButton.setEnabled(true);

    }

    void showMessage(String message) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setMessage(message);
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

    public boolean validate() {
        boolean valid = true;

        String currentPassword = _currentpasswordText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmedPassword = _confirmedPasswordText.getText().toString();

        if (currentPassword.isEmpty() || currentPassword.length() < 4 || currentPassword.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (password.compareTo(currentPassword) == 0) {
            _passwordText.setError("new password must be different with current password");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (confirmedPassword.compareTo(password) != 0) {
            _confirmedPasswordText.setError("These passwords don't match. Try again?");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void signupAction(SignupClass user) {

        //TODO
//        String returnMessage = "";
//
//        StringClient client = ServiceGenerator.createService(StringClient.class);
//
//        Call<ResponseBody> call = client.signup(user);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//
//                    int messageCode = response.code();
//                    if(messageCode == 200){
//                        onSignupSuccess();
//                    }
//                    else{
//                        // handle when cannot signup
//                        onSignupFailed();
//                        Notification.showMessage(SignUpActivity.this, 5);
//                        Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
//                        startActivity(intent);
//                    }
//
//                }
//                catch(Exception e){
//                    e.printStackTrace();
//                    ErrorClass.showError(SignUpActivity.this, 27);
//                    Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
//                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                ErrorClass.showError(SignUpActivity.this, 28);
//            }
//        });
    }
}

