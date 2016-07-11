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
import com.google.gson.JsonObject;

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

    String currentPassword;
    String password;
    String confirmedPassword;

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

        Preferences.showLoading(ChangePasswordActivity.this, "Change Password", "Processing...");
        if (!validate()) {
            onChangePasswordFailed();
            return;
        }

        _changePassButton.setEnabled(false);

        currentPassword = _currentpasswordText.getText().toString();
        password = _passwordText.getText().toString();
        confirmedPassword = _confirmedPasswordText.getText().toString();

        changePasswordAction();

    }

    public void onChangePasswordSuccess() {
        //TODO
        Preferences.dismissLoading();
//        setResult(RESULT_OK, null);
//
//        Toast.makeText(getBaseContext(), "Signed up successfully!", Toast.LENGTH_LONG).show();
//
//        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
//        startActivity(intent);

    }

    public void onChangePasswordFailed() {
        //TODO
        Preferences.dismissLoading();
//        Toast.makeText(getBaseContext(), "Signup failed!", Toast.LENGTH_LONG).show();
//
//        _signupButton.setEnabled(true);

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

        if (confirmedPassword.compareTo(password) != 0) {
            _confirmedPasswordText.setError("These passwords don't match. Try again?");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    void changePasswordAction() {
        SharedPreferences pref = this.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        JsonObject toUp = new JsonObject();
        toUp.addProperty("oldPassword", currentPassword);
        toUp.addProperty("newPassword", password);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.changePassword(toUp);

        call.enqueue (new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int messageCode = response.code();
                    if (messageCode == 200) {
                        Notification.showMessage(ChangePasswordActivity.this, 7);
                        onChangePasswordSuccess();
                    } else if (messageCode == 400) {
                        JSONObject data = new JSONObject(response.errorBody().string());
                        int errorCode = data.getInt("code");
                        if(errorCode == 1)
                            Notification.showMessage(ChangePasswordActivity.this, 8);
                        else if(errorCode == 8)
                            Notification.showMessage(ChangePasswordActivity.this, 9);
                        onChangePasswordFailed();
                    } else {
                        ErrorClass.showError(ChangePasswordActivity.this, 36);
                        onChangePasswordFailed();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    onChangePasswordFailed();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorClass.showError(ChangePasswordActivity.this, 37);
                onChangePasswordFailed();
            }
        });

    }

}

