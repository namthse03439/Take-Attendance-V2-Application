package com.example.intern.takeattendanceapplicationv2.Fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.intern.takeattendanceapplicationv2.BaseClass.GlobalVariable;
import com.example.intern.takeattendanceapplicationv2.BaseClass.ServiceGenerator;
import com.example.intern.takeattendanceapplicationv2.BaseClass.StringClient;
import com.example.intern.takeattendanceapplicationv2.DetailedInformationActivity;
import com.example.intern.takeattendanceapplicationv2.Information.ScheduleManager;
import com.example.intern.takeattendanceapplicationv2.MainActivity;
import com.example.intern.takeattendanceapplicationv2.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TakeAttendanceToday.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TakeAttendanceToday#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TakeAttendanceToday extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Calendar calendar;
    private TableLayout[] tls = new TableLayout[2];
    TextView textTime;

    private View myView;

    private Activity context;

    private OnFragmentInteractionListener mListener;

    public TakeAttendanceToday() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TakeAttendanceToday.
     */
    // TODO: Rename and change types and number of parameters
    public static TakeAttendanceToday newInstance(String param1, String param2) {
        TakeAttendanceToday fragment = new TakeAttendanceToday();
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

        context = this.getActivity();
    }

    private String getDateOfWeekString(int dateOfWeek)
    {
        switch (dateOfWeek)
        {
            case 1:
                return "MON";
            case 2:
                return "TUES";
            case 3:
                return "WED";
            case 4:
                return "THUR";
            case 5:
                return "FRI";
            case 6:
                return "SAT";
            case 7:
                return "SUN";
            default:
                return "null";
        }
    }

    int getTime(String time)
    {
        int temp_index = time.lastIndexOf(":");
        String temp_current_time = String.valueOf(time.subSequence(0, temp_index));
        int current_time = Integer.parseInt(temp_current_time);

        return current_time;
    }

    private void getTableLayout()
    {
        tls[0] = (TableLayout) myView.findViewById(R.id.tableLayout1);
        tls[1] = (TableLayout) myView.findViewById(R.id.tableLayout2);
    }

    private void displayTimeColumn()
    {
        for(int i = 0; i < ScheduleManager.timeNumber; i++)
        {
            List<String> values = new ArrayList<>();
            values.add(ScheduleManager.dailyTime[i]);

            TextView tvs = new TextView(context);

            tvs.setGravity(Gravity.CENTER);
            if (i % 2 == 1)
            {
                tvs.setBackgroundColor(Color.parseColor("#bbdefb"));
            }
            else
            {
                tvs.setBackgroundColor(Color.WHITE);
            }

            tvs.setText(values.get(0));

            //tvs.setLines(5);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            params.height = 140;
            tvs.setLayoutParams(params);

            TableRow trs = new TableRow(context);
            TableLayout.LayoutParams layoutRow = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trs.setLayoutParams(layoutRow);

            trs.addView(tvs);

            tls[0].addView(trs);
        }
    }

    private void createSubjectView(boolean isExistSubject, final int startTime, final int endTime, final int index, final JSONObject subject)
    {
        if (!isExistSubject)
        {
            for(int i = startTime; i < endTime; i++)
            {
                TextView tvs = new TextView(context);
                //tvs.setLines(5);

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                params.height = 140;
                tvs.setLayoutParams(params);

                TableRow trs = new TableRow(context);
                TableLayout.LayoutParams layoutRow = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                trs.setLayoutParams(layoutRow);

                trs.addView(tvs);

                tls[1].addView(trs);
            }

            return;
        }

        try
        {
            TextView tvs = new TextView(context);
            String temp;
            String result = "";
            String subject_area = subject.getString("subject_area");
            String class_section = subject.getString("class_section");

            temp = subject_area + " " + class_section + System.getProperty ("line.separator");
            result += temp;

            temp = subject.getString("location");
            result += temp;

            tvs.setGravity(Gravity.CENTER);
            tvs.setTextColor(Color.WHITE);



            final int status = Integer.parseInt(subject.getString("status"));
            switch (status) {
                case 0:
                    tvs.setBackgroundColor(Color.LTGRAY);
                    break;
                case 1:
                    tvs.setBackgroundColor(Color.GREEN);
                    break;
                case 2:
                    tvs.setBackgroundColor(Color.YELLOW);
                    break;
                case 3:
                    tvs.setBackgroundColor(Color.RED);
                    break;
            }

            tvs.setText(result);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            params.height = 140 * (endTime - startTime);
            tvs.setLayoutParams(params);

            GradientDrawable gd = new GradientDrawable();
            gd.setColor(0xFFCCFF99); // Changes this drawbale to use a single color instead of a gradient
            gd.setCornerRadius(5);
            gd.setStroke(1, 0xFF000000);
            tvs.setBackgroundDrawable(gd);

            TableRow trs = new TableRow(context);
            TableLayout.LayoutParams layoutRow = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trs.setLayoutParams(layoutRow);

            trs.addView(tvs);

            tls[1].addView(trs);

            tvs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariable.scheduleManager.setCurrentLession(index);
                    Intent intend = new Intent(context, DetailedInformationActivity.class);
                    startActivity(intend);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void displayScheduleToday()
    {
        JSONArray schedule = GlobalVariable.scheduleManager.getDailySchedule();

        int time = 8;
        for(int subjectIndex = 0; subjectIndex < schedule.length(); subjectIndex++)
        {
            JSONObject subject = null;
            try
            {
                subject = schedule.getJSONObject(subjectIndex);
                int startSubjectTime = getTime(subject.getString("start_time"));
                int endSubjectTime = getTime(subject.getString("end_time"));

                createSubjectView(false, time, startSubjectTime, -1, null);
                createSubjectView(true, startSubjectTime, endSubjectTime, subjectIndex, subject);
                time = endSubjectTime;

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        createSubjectView(false, time, 17, -1, null);
    }


    private void setTimeView()
    {
        calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MM/dd/yyyy");
        String stringTime = simpleDateFormat.format(calendar.getTime());

        textTime = (TextView) myView.findViewById(R.id.text_Time);
        textTime.setText(stringTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_take_attendance_today, container, false);

        setTimeView();

        try {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading data from server...");
            progressDialog.show();

            SharedPreferences pref = getActivity().getSharedPreferences("ATK_pref", 0);
            String auCode = pref.getString("authorizationCode", null);

            StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
            Call<ResponseBody> call = client.getTimetableToday();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try{
                        JSONArray data = new JSONArray(response.body().string());
                        GlobalVariable.scheduleManager.setDailySchedule(data);

                        getTableLayout();
                        displayTimeColumn();
                        displayScheduleToday();

                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.print("Tung");
                }
            });


//            while(GlobalVariable.loadedTimetableToday == false){}
//            GlobalVariable.loadedTimetableToday = false;

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 1000);

            System.out.print("OK!");

        }

        catch(Exception e){
            System.out.print("load timetable today!");
        }

        return myView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_takeattendancetitle, menu);
        ActionBar actionBar = context.getActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(getString(R.string.title_fragment_take_attendance));
        }
        super.onCreateOptionsMenu(menu, inflater);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
