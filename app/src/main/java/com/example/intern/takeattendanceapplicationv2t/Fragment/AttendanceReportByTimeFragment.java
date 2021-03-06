package com.example.intern.takeattendanceapplicationv2t.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.intern.takeattendanceapplicationv2t.BaseClass.GlobalVariable;
import com.example.intern.takeattendanceapplicationv2t.BaseClass.ServiceGenerator;
import com.example.intern.takeattendanceapplicationv2t.BaseClass.StringClient;
import com.example.intern.takeattendanceapplicationv2t.Preferences;
import com.example.intern.takeattendanceapplicationv2t.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AttendanceReportByTimeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AttendanceReportByTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendanceReportByTimeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TableLayout[] tls = new TableLayout[3];

    private View myView;

    private Activity context;

    private Spinner spin;
    private Spinner subjectSpin;

    private String semesterList[];
    private String classSubject[];

    private OnFragmentInteractionListener mListener;

    public AttendanceReportByTimeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendanceReportByTimeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendanceReportByTimeFragment newInstance(String param1, String param2) {
        AttendanceReportByTimeFragment fragment = new AttendanceReportByTimeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = getActivity();
    }

    private void getTableLayout()
    {
        tls[0] = (TableLayout) myView.findViewById(R.id.tableLayout1);
        tls[1] = (TableLayout) myView.findViewById(R.id.tableLayout2);
        tls[2] = (TableLayout) myView.findViewById(R.id.tableLayout3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_attendance_report_by_time, container, false);

        spin = (Spinner) myView.findViewById(R.id.time_table_semester);
        subjectSpin = (Spinner) myView.findViewById(R.id.header2);

        getTableLayout();
        getListsemesterAndLastestSemesterClassesFunction();

        return myView;
    }

    private void initSemesterSpinner()
    {
        ArrayList<String> spinnerArray = new ArrayList<String>();
        for(int i = 0; i < semesterList.length; i++)
        {
            spinnerArray.add(semesterList[i]);
        }


        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(context,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray);

        spin.setAdapter(spinnerArrayAdapter);
        spin.setSelection(GlobalVariable.currentSemester);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (GlobalVariable.currentSemester != position)
                {
                    GlobalVariable.currentSemester = position;
                    GlobalVariable.currentSubjectView = 0;
                    getAttendanceHistoryBySemesterFunction(semesterList[position]);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    private void setSpinnerForSubjectView()
    {
        ArrayList<String> spinnerArray = new ArrayList<String>();
        for(int i = 0; i < classSubject.length; i++)
        {
            spinnerArray.add(classSubject[i]);
        }

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(context,
                R.layout.spinner_item,
                spinnerArray);

        subjectSpin.setAdapter(spinnerArrayAdapter);
        subjectSpin.setSelection(GlobalVariable.currentSubjectView);

        subjectSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (GlobalVariable.currentSubjectView != position)
                {
                    GlobalVariable.currentSubjectView = position;
                    loadRecord();
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    private String getDate(String date)
    {
        if (date.compareTo("SUN") == 0) return "Sunday";
        if (date.compareTo("MON") == 0) return "Monday";
        if (date.compareTo("TUES") == 0) return "Tuesday";
        if (date.compareTo("WED") == 0) return "Wednesday";
        if (date.compareTo("THUR") == 0) return "Thursday";
        if (date.compareTo("FRI") == 0) return "Friday";
        if (date.compareTo("SAT") == 0) return "Saturday";

        return "";
    }

    private String getComponent(String component)
    {
        if (component.compareTo("TUT") == 0) return "Tutorial";
        if (component.compareTo("LEC") == 0) return "Lecture";
        if (component.compareTo("PRA") == 0) return "Practical";

        return "";
    }

    private void removeAllView()
    {
        tls[0].removeAllViews();
        tls[1].removeAllViews();
        tls[2].removeAllViews();
    }

    private void loadRecord()
    {
        try
        {
            JSONArray selectedSubject = GlobalVariable.currentAttendanceHistory.getJSONArray(classSubject[GlobalVariable.currentSubjectView]);
            removeAllView();
            for(int i = 0 ; i < selectedSubject.length(); i++)
            {
                JSONObject subject = selectedSubject.getJSONObject(i);

                //+ Create background based on status
                GradientDrawable gd = new GradientDrawable();

                final int status = Integer.parseInt(subject.getString("status"));
                switch (status) {
                    case 0:
                        gd.setColor(0xFFC0C0C0);
                        break;
                    case 1:
                        gd.setColor(0xFF00FF7F);
                        break;
                    case 2:
                        gd.setColor(0xFFFFA500);
                        break;
                    case 3:
                        gd.setColor(0xFFCC0000);
                        break;
                }
                gd.setCornerRadius(5);
                gd.setStroke(1, 0xFF000000);
                //- Create background based on status

                //+ Define same height for each row
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                params.height = 130;
                params.setMargins(1, 1, 1, 1);
                //- Define same height for each row

                //+ Add item to Date column
                String weekDay = subject.getString("weekday");
                String date = subject.getString("date");
                String dateInfo = getDate(weekDay) + ", " + System.getProperty ("line.separator") + date;

                TextView tvDate = new TextView(context);
                tvDate.setText(dateInfo);
                tvDate.setTextColor(Color.WHITE);
                tvDate.setGravity(Gravity.CENTER);
                tvDate.setBackgroundDrawable(gd);
                tvDate.setLayoutParams(params);

                TableRow tableRowDate = new TableRow(context);
                tableRowDate.addView(tvDate);
                tableRowDate.setLayoutParams(params);

                tls[0].addView(tableRowDate);
                //- Add item to Date column

                //+ Add item to Subject Information column
                String component = subject.getString("component");
                String lecture = "Mr.Zhang Qinjie";

                //TODO
                String subjectInfo = getComponent(component) + System.getProperty ("line.separator")
                                        + lecture;

                TextView tvSubjectInfo = new TextView(context);
                tvSubjectInfo.setText(subjectInfo);
                tvSubjectInfo.setTextColor(Color.WHITE);
                tvSubjectInfo.setGravity(Gravity.CENTER);
                tvSubjectInfo.setBackgroundDrawable(gd);
                tvSubjectInfo.setLayoutParams(params);

                TableRow tableRowSubject = new TableRow(context);
                tableRowSubject.addView(tvSubjectInfo);
                tableRowSubject.setLayoutParams(params);

                tls[1].addView(tableRowSubject);
                //- Add item to Subject Information column

                //+ Add item to Time column
                String startTime = subject.getString("start_time");
                String endTime = subject.getString("end_time");

                String time = startTime + System.getProperty ("line.separator")
                                            + "-" + System.getProperty ("line.separator")
                                            + endTime;



                TextView tvTimeInfo = new TextView(context);
                tvTimeInfo.setText(time);
                tvTimeInfo.setTextColor(Color.WHITE);
                tvTimeInfo.setGravity(Gravity.CENTER);
                tvTimeInfo.setBackgroundDrawable(gd);
                tvTimeInfo.setLayoutParams(params);

                TableRow tableRowTime = new TableRow(context);
                tableRowTime.addView(tvTimeInfo);
                tableRowTime.setLayoutParams(params);

                tls[2].addView(tableRowTime);
                //- Add item to Time column
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    void getListsemesterAndLastestSemesterClassesFunction() {
        Preferences.showLoading(context, "Setup", "Loading data from server...");
        SharedPreferences pref = getActivity().getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getListSemesters();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int messageCode = response.code();
                    JSONArray listSemesters = new JSONArray(response.body().string());
                    semesterList = new String[listSemesters.length()];
                    for(int i = 0; i < listSemesters.length(); i++)
                    {
                        semesterList[i] = listSemesters.getString(i);
                    }

                    GlobalVariable.currentSemester = listSemesters.length() - 1;
                    initSemesterSpinner();

                    String lastSmt = listSemesters.getString(listSemesters.length() - 1);
                    getListClassesFunction(lastSmt);

                    getAttendanceHistoryBySemesterFunction(String.valueOf(semesterList[GlobalVariable.currentSemester]));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    void getListClassesFunction(String semester) {
        SharedPreferences pref = getActivity().getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getListClasses(semester);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Preferences.dismissLoading();
                    int messageCode = response.code();
                    JSONArray listClasses = new JSONArray(response.body().string());
                    classSubject = new String[listClasses.length()];
                    for(int i = 0; i < listClasses.length(); i++)
                    {
                        classSubject[i] = listClasses.getString(i);
                    }
                    setSpinnerForSubjectView();

                    System.out.print("OK");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    void getAttendanceHistoryBySemesterFunction(String semester) {
        try
        {
            Preferences.showLoading(context, "Initialize", "Loading data from server...");
            SharedPreferences pref = getActivity().getSharedPreferences("ATK_pref", 0);
            String auCode = pref.getString("authorizationCode", null);

            StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
            Call<ResponseBody> call = client.getAttendanceHistory(semester);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Preferences.dismissLoading();
                        int messageCode = response.code();
                        GlobalVariable.currentAttendanceHistory = new JSONObject(response.body().string());

                        getListClassesFunction(semesterList[GlobalVariable.currentSemester]);
                        GlobalVariable.currentSubjectView = 0;

                        loadRecord();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    void getTimeTableNextKDays(int k) {
        SharedPreferences pref = getActivity().getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getNextDays(k);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int messageCode = response.code();
                    JSONObject listClasses = new JSONObject(response.body().string());
                    //TODO: a Nam
                    System.out.print("OK");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
