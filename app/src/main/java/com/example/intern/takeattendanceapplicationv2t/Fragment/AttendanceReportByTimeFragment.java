package com.example.intern.takeattendanceapplicationv2t.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.intern.takeattendanceapplicationv2t.Information.ScheduleManager;
import com.example.intern.takeattendanceapplicationv2t.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private TableLayout[] tls = new TableLayout[1];

    private View myView;

    private Activity context;

    private Date start_date;
    private Date end_date;

    private static boolean initDate = false;

    private TextView mStartDateView;
    private TextView mEndDateView;

    static final int START_DATE_DIALOG_ID = 999;
    static final int END_DATE_DIALOG_ID = 1000;


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
    }

    private void setCurrentDateOnView(Date date, int viewCode)
    {
        String dateView = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(date);

        if (viewCode == R.id.text_startDate)
        {
            mStartDateView.setText(dateView);
        }
        else if (viewCode == R.id.text_endDate)
        {
            mEndDateView.setText(dateView);
        }
    }

    private void initDatePicker()
    {
        if (initDate == false)
        {
            final Calendar calendar = Calendar.getInstance();
            final Calendar temp = Calendar.getInstance();

            while (temp.get(Calendar.DAY_OF_WEEK) > calendar.getFirstDayOfWeek()) {
                temp.add(Calendar.DATE, -1); // Substract 1 day until first day of week.
            }
            start_date = temp.getTime();

            temp.add(Calendar.DATE, 6);
            end_date = temp.getTime();
        }

        mStartDateView = (TextView) myView.findViewById(R.id.text_startDate);
        setCurrentDateOnView(start_date,R.id.text_startDate);

        mEndDateView = (TextView) myView.findViewById(R.id.text_endDate);
        setCurrentDateOnView(end_date, R.id.text_endDate);
    }

    private void createTimeColumn()
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
            tvs.setLines(5);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            tvs.setLayoutParams(params);

            TableRow trs = new TableRow(context);
            final float scale = context.getResources().getDisplayMetrics().density;
            int pixels = (int) (70 * scale + 0.5f);
            trs.setMinimumWidth(pixels);
            trs.addView(tvs);

            tls[0].addView(trs);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_attendance_report_by_time, container, false);

        initDatePicker();
        getTableLayout();
        createTimeColumn();

        return myView;
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

    protected Dialog onCreateDialog(int datePickerId) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        switch (datePickerId) {
            case START_DATE_DIALOG_ID:
                // set start date picker as current date
                return new DatePickerDialog(context, startDatePickerListener, year, month, day);
            case END_DATE_DIALOG_ID:
                // set end date picker as current date
                return new DatePickerDialog(context, endDatePickerListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener startDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay)
        {

            if (selectedYear > end_date.getYear() + 1900)
                return;

            if (selectedYear == end_date.getYear() + 1900 && selectedMonth > end_date.getMonth())
                return;

            if (selectedYear == end_date.getYear() + 1900 && selectedMonth == end_date.getMonth() && selectedDay > end_date.getDate())
                return;

            String dateView = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);

            end_date.setYear(selectedYear-1900);
            end_date.setMonth(selectedMonth);
            end_date.setDate(selectedDay);

            // set selected date into textview
            mStartDateView.setText(dateView);
        }
    };

    private DatePickerDialog.OnDateSetListener endDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay)
        {
            if (selectedYear < start_date.getYear() + 1900)
                return;

            if (selectedYear == start_date.getYear() + 1900 && selectedMonth < start_date.getMonth())
                return;

            if (selectedYear == start_date.getYear() + 1900 && selectedMonth == start_date.getMonth() && selectedDay < start_date.getDate())
                return;

            String dateView = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);

            start_date.setYear(selectedYear-1900);
            start_date.setMonth(selectedMonth);
            start_date.setDate(selectedDay);

            // set selected date into textview
            mEndDateView.setText(dateView);
        }
    };

    private void addListenerOnFromButton()
    {
        Button btnFrom = (Button) myView.findViewById(R.id.btn_from);
        btnFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.showDialog(START_DATE_DIALOG_ID);
            }
        });
    }

    private void addListenerOnToButton()
    {
        Button btnTo = (Button) myView.findViewById(R.id.btn_to);
        btnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.showDialog(END_DATE_DIALOG_ID);
            }
        });
    }
}
