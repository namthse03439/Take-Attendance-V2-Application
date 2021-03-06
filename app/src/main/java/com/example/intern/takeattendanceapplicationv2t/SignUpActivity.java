package com.example.intern.takeattendanceapplicationv2t;

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

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static boolean isRegisterDevice = false;

    @InjectView(R.id.input_username)    EditText _usernameText;
    @InjectView(R.id.input_studentId)   EditText _studentIdText;
    @InjectView(R.id.input_email)       EditText _emailText;
    @InjectView(R.id.input_password)    EditText _passwordText;
    @InjectView(R.id.input_confirmpass) EditText _confirmedPasswordText;
    @InjectView(R.id.btn_signup)        Button   _signupButton;
    @InjectView(R.id.link_login)        TextView _loginLink;
    @InjectView(R.id.item_check)        CheckBox _checkBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        _checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_checkBox.isChecked())
                {
                    isRegisterDevice = true;
                }
                else {
                    isRegisterDevice = false;
                }
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        Preferences.showLoading(SignUpActivity.this, "Sign Up", "Creating Account...");
        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        String username = _usernameText.getText().toString();
        String studentId = _studentIdText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmedPassword = _confirmedPasswordText.getText().toString();

        // Interact with local server
        //==========================

        SignupClass user = new SignupClass(username, password, email, studentId, this);
        signupAction(user);

        //--------------------------

    }

    public void onSignupSuccess() {
        Preferences.dismissLoading();
        setResult(RESULT_OK, null);

        Toast.makeText(getBaseContext(), "Signed up successfully!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
        startActivity(intent);

    }

    public void onSignupFailed() {
        Preferences.dismissLoading();
        Toast.makeText(getBaseContext(), "Signup failed!", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);

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

        String username = _usernameText.getText().toString();
        String studentId = _studentIdText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmedPassword = _confirmedPasswordText.getText().toString();


        if (username.isEmpty() || username.length() < 4 || username.length() > 255) {
            _usernameText.setError("enter a valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (studentId.isEmpty()) {
            _studentIdText.setError("enter a valid studentId");
            valid = false;
        } else {
            _studentIdText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
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

        if (isRegisterDevice == false)
        {
            showMessage("Please register device before finish creating acount!");
            valid = false;
        }

        return valid;
    }

    public void signupAction(SignupClass user) {

        String returnMessage = "";

        StringClient client = ServiceGenerator.createService(StringClient.class);

        Call<ResponseBody> call = client.signup(user);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    int messageCode = response.code();
                    if(messageCode == 200){
                        onSignupSuccess();
                    }
                    else{
                        // handle when cannot signup
                        onSignupFailed();
                        Notification.showMessage(SignUpActivity.this, 5);
                        Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }

                }
                catch(Exception e){
                    e.printStackTrace();
                    ErrorClass.showError(SignUpActivity.this, 27);
                    Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorClass.showError(SignUpActivity.this, 28);
            }
        });
    }
}
