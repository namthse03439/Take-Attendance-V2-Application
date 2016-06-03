package com.example.intern.takeattendanceapplicationv2.Fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.intern.takeattendanceapplicationv2.Information.ScheduleManager;
import com.example.intern.takeattendanceapplicationv2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


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
    private TableLayout[] tls = new TableLayout[3];
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
        myView = inflater.inflate(R.layout.fragment_take_attendance_today, container, false);

        //+ Set time view
        calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MM/dd/yyyy");
        String stringTime = simpleDateFormat.format(calendar.getTime());

        textTime = (TextView) myView.findViewById(R.id.text_Time);
        textTime.setText(stringTime);
        //- Set time view

        //+ Add time to table
        getTableLayout();
        for(int i = 0; i < ScheduleManager.timeNumber; i++)
        {
            List<String> values = new ArrayList<>();
            values.add(ScheduleManager.dailyTime[i]);

            TextView tvs = new TextView(context);
            tvs.setGravity(Gravity.CENTER);

            tvs.setText(values.get(0));
            tvs.setLines(5);

            TableRow trs = new TableRow(context);
            trs.addView(tvs);

            tls[0].addView(trs);
        }
        //- Add time to table

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
