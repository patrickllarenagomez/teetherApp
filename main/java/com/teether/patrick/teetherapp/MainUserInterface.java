package com.teether.patrick.teetherapp;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainUserInterface extends NavigationDrawer {

    private RecyclerView recyclerViewUserAppointment;
    private RecyclerView.Adapter adapter;
    private DividerItemDecoration mDividerItemDecoration;
    private cSessions cSessions;
    private int user_id;
    private ProgressBar userAppointmentProgressBar;
    private String backgroundData;
    private List<cUserAppointments> cUserAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        //setContentView(R.layout.activity_main_user_interface);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main_user_interface, null, false);
        drawer.addView(contentView, 0);

        cSessions = new cSessions(this);
        user_id = cSessions.getUserID();
        userAppointmentProgressBar = (ProgressBar) findViewById(R.id.userAppointmentProgressBar);

        recyclerViewUserAppointment = (RecyclerView) findViewById(R.id.recyclerViewUserAppointment);
        recyclerViewUserAppointment.setHasFixedSize(true);
        recyclerViewUserAppointment.setLayoutManager(new LinearLayoutManager(this));

        mDividerItemDecoration = new DividerItemDecoration(
                recyclerViewUserAppointment.getContext(),
                1
        );
        recyclerViewUserAppointment.addItemDecoration(mDividerItemDecoration);



        getAppointmentData();
        userAppointmentProgressBar.setVisibility(View.VISIBLE);
    }

    private void getAppointmentData() {
        try {
            String data_string = URLEncoder.encode("user_id", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(user_id), "UTF-8");

            backgroundData = data_string;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        cAsyncTaskWaitData getAppointmentData = new cAsyncTaskWaitData(new cAsyncTaskWaitData.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                parseAppoimentData(result);

            }
        });
        getAppointmentData.myURL = getResources().getString(R.string.base_url) + "getUserAppointments.php";
        getAppointmentData.execute(backgroundData);
    }

    private void parseAppoimentData(String result) {
        cUserAppointments = new ArrayList<>();

        DateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        Date appointTime = null;
        SimpleDateFormat newFormat = new SimpleDateFormat("hh:mm a");

        try {
            JSONObject object = new JSONObject(result);
            JSONArray Jarray = object.getJSONArray("server_response");

            for (int i = 0; i < Jarray.length(); i++) {
                JSONObject JSONObj = Jarray.getJSONObject(i);

                String time;
                time = JSONObj.getString("appointment_time");

                try {
                    appointTime = (Date) formatter.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String newTime = newFormat.format(appointTime);

                if(newTime.equals("12:00 AM"))
                    newTime = "12:00 PM";

                String date = JSONObj.getString("appointment_date");


                SimpleDateFormat month_date = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisDate = null;
                try {
                    thisDate = sdf.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String chosenDay = month_date.format(thisDate);

                String status = JSONObj.getString("is_approved");

                if(status.equals("0"))
                {
                    status = "Pending";
                }
                else if(status.equals("1"))
                {
                    status = "Accepted";
                }
                else if(status.equals("2"))
                {
                    status = "Rejected";
                }
                else if(status.equals("3"))
                {
                    status = "Cancelled";
                }

                cUserAppointments constructorUserAppointments = new cUserAppointments(

                        JSONObj.getInt("appointment_id"),
                        JSONObj.getString("clinic_name"),
                        JSONObj.getString("clinic_address"),
                        chosenDay + " " +newTime,
                        status
                );
                cUserAppointments.add(constructorUserAppointments);
            }

            adapter = new cUserAppointmentsAdapter(cUserAppointments, this);
            recyclerViewUserAppointment.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        userAppointmentProgressBar.setVisibility(View.GONE);
    }
}
