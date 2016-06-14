package com.example.intern.takeattendanceapplicationv2.BaseClass;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intern.takeattendanceapplicationv2.R;

import butterknife.InjectView;

/**
 * Created by Lord One on 6/14/2016.
 */
public class ErrorClass {
    private static String [] errorList = {
            "Exception caught when loading Full Timetable - GlobalVariale.getFullTimeTable", // 0
            "Username or Password incorrect - LogInActivity.loginAction", // 1
            "Exception caught when login - LogInActivity.loginAction", // 2
            "Failed when call for response - LogInActivity.loginAction", // 3
            "Error occurred while creating the File - Training Fragment.dispatchTakePictureIntent ", // 4
            "Exception caught while resize image - GlobalVariable.resizeImage", // 5
            "Exception caught while checking if user is logged in - GlobalVariable.checkLoggedIn", //6
            "Error while call for response - GlobalVariable.checkLoggedIn", // 7
            "Exception caught when convert to JSONObject from response.body - GlobalVariable.loadTimetableByWeek", // 8
            "Error while call for response - GlobalVariable.loadTimetableByWeek", // 9
            "Exception caught while get response from local server - GlobalVariable.getThisPersonID", // 10
            "Exception caught while get response from local server - GlobalVariable.getThisPersonID", // 11
            "Exception caught while request face ID List from local server - TrainingFragment.getThisFaceIDList", // 12
            "Exception caught while modify the face ID List - TrainingFragment.substitute1FacefromPerson", //13
            "Exception caught while send face ID List to local server - TrainingFragment.postFaceIDListtoLocalServer", // 14
            "Receive message code != 200 - TrainingFragment.postFaceIDListtoLocalServer", // 15
            "Exception caught while create person and get person ID - TrainingFragment.create1Person", // 16
            ""

    };

    public static void showError(final Context context, int errorCode){
//        Toast.makeText(context, errorList[errorCode], Toast.LENGTH_LONG).show();
        final Dialog dialog = new Dialog(context);
        dialog.setTitle("Error");
        dialog.setContentView(R.layout.show_error);

        TextView mErrorText = (TextView) dialog.findViewById(R.id.error_text);
        mErrorText.setText(errorList[errorCode]);

        Button mOkButton = (Button) dialog.findViewById(R.id.ok_button);

        dialog.show();

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(context, context.getClass());
                context.startActivity(intent);
            }
        });
    }
}
