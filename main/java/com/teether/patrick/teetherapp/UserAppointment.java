package com.teether.patrick.teetherapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UserAppointment extends AppCompatActivity implements View.OnClickListener {

    private int appointment_id;
    private String clinic_name;
    private String clinic_date;
    private String clinic_address;
    private String status;
    private String backgroundData;
    private TextView textViewClinicName;
    private TextView textViewClinicAddress;
    private TextView textViewClinicDate;
    private TextView textViewStatus;
    private Button btnCancelBooking;
    private ProgressBar userProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        setContentView(R.layout.activity_user_appointment);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        appointment_id = bundle.getInt("appointment_id");
        clinic_name = bundle.getString("clinic_name");
        clinic_address = bundle.getString("clinic_address");
        clinic_date = bundle.getString("clinic_date");
        status = bundle.getString("status");

        textViewClinicName = (TextView) findViewById(R.id.textViewClinicName);
        textViewClinicAddress = (TextView) findViewById(R.id.textViewClinicAddress);
        textViewClinicDate = (TextView) findViewById(R.id.textViewClinicDate);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        btnCancelBooking = (Button) findViewById(R.id.btnCancelBooking);

        userProgressBar = (ProgressBar) findViewById(R.id.userProgressBar);


        textViewClinicName.setText(clinic_name);
        textViewClinicAddress.setText(clinic_address);

        textViewClinicDate.setText(clinic_date);
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
        textViewStatus.setText(status);



        btnCancelBooking.setOnClickListener(this);
        userProgressBar.setVisibility(View.INVISIBLE);

    }

    public void onClick(final View view)
    {
        userProgressBar.setVisibility(View.VISIBLE);
        try
        {
            String data_string = URLEncoder.encode("appointment_id", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(appointment_id), "UTF-8");
            backgroundData = data_string;

        }
        catch(IOException e)
        {
            // do nothing
        }


        cAsyncTaskWaitData cancelBooking = new cAsyncTaskWaitData(new cAsyncTaskWaitData.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                userProgressBar.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), MainUserInterface.class);
                startActivity(intent);
            }
        });
        cancelBooking.myURL = getResources().getString(R.string.base_url) + "updateUserAppointment.php";
        cancelBooking.execute(backgroundData);
    }
}

