package com.example.intern.takeattendanceapplicationv2t;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.example.intern.takeattendanceapplicationv2t.BaseClass.GlobalVariable;
import com.example.intern.takeattendanceapplicationv2t.Fragment.AttendanceReportByTimeFragment;
import com.example.intern.takeattendanceapplicationv2t.Fragment.TakeAttendanceToday;
import com.example.intern.takeattendanceapplicationv2t.Fragment.TimeTableFragment;
import com.example.intern.takeattendanceapplicationv2t.Fragment.TrainingFragment;


import com.example.intern.takeattendanceapplicationv2t.Information.ScheduleManager;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private final String noInternetConnection = "Check your connection\n and try again.";

    ScheduleManager manager = new ScheduleManager();

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        //===============

//        loadTimetableByWeek();
//        if(!haveFullTimetable())
//            loadFullTimetable();

        //---------------

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
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

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        android.app.Fragment fragment = null;

        switch (position) {
            case 0: {
                fragment = new TakeAttendanceToday();
                break;
            }
            case 1:{
                GlobalVariable.checkLoggedin(this);
                fragment = new TrainingFragment();
                break;
            }
            case 2:{
                fragment = new TimeTableFragment();
                break;
            }
            case 3:{
                fragment = new AttendanceReportByTimeFragment();
                break;
            }
            case 4:{
//                fragment = new NearestFragment();
                break;
            }
            default:
                //TODO load timetable today
                fragment = new TakeAttendanceToday();
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:

                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

//    void loadFullTimetable(){
//        try {
//            SharedPreferences pref = this.getSharedPreferences("ATK_pref", 0);
//            String auCode = pref.getString("authorizationCode", null);
//
//            StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
//            Call<ResponseBody> call = client.getFullTimetable();
//
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        JSONArray data = new JSONArray(response.body().string());
//                        GlobalVariable.scheduleManager.setSchedule(data);
//
//                        SharedPreferences pref = getApplicationContext().getSharedPreferences("ATK_pref", 0);
//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.putString("fullTimetable", data.toString());
//                        editor.apply();
//
//                        System.out.print("OK!");
//                    }
//                    catch (Exception e){
//                        System.out.print("Failed!");
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    System.out.print("Failed");
//                }
//            });
//
//        }
//        catch(Exception e){
//            System.out.print("load timetable by week exception!");
//        }
//    }

//    void loadTimetableToday() {
//        try {
//            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setMessage("Loading data from server...");
//            progressDialog.show();
//
////            GlobalVariable.loadedTimetableToday = false;
//
//            SharedPreferences pref = this.getSharedPreferences("ATK_pref", 0);
//            String auCode = pref.getString("authorizationCode", null);
//
//            StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
//            Call<ResponseBody> call = client.getTimetableToday();
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try{
//                        JSONArray data = new JSONArray(response.body().string());
//                        GlobalVariable.scheduleManager.setDailySchedule(data);
//
//
//
//
//                    }
//                    catch(Exception e){
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    System.out.print("Tung");
//                }
//            });
//
//
////            while(GlobalVariable.loadedTimetableToday == false){}
////            GlobalVariable.loadedTimetableToday = false;
//
//            new android.os.Handler().postDelayed(
//                    new Runnable() {
//                        public void run() {
//                            progressDialog.dismiss();
//                        }
//                    }, 1000);
//
//            System.out.print("OK!");
//
//        }
//
//        catch(Exception e){
//            System.out.print("load timetable today!");
//        }
//    }

}
