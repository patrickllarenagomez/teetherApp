package com.teether.patrick.teetherapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.System.currentTimeMillis;
import static java.util.Calendar.AM;

public class FileAppointment extends AppCompatActivity{

    private int clinic_id;
    private String clinic_name;
    private TextView textViewClinicName;
    private Spinner timeSpinner;
    private EditText dateEditText;
    private List<String> timeArray;
    private String backgroundData;
    private String appointmentDate;
    private Calendar thisCalendar;
    int calendarDay, calendarMonth, calendarYear;
    private BiMap<String, String> dateBiMap;
    private Button btnBook;
    private ProgressBar appointmentProgressBar;
    private int user_id;
    private cSessions cSessions;
    private String appointment_time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent dataIntent = getIntent();
        Bundle bundle = dataIntent.getExtras();

        clinic_id = bundle.getInt("clinic_id");
        clinic_name = bundle.getString("clinic_name");

        setTitle("File an Appointment");
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        setContentView(R.layout.activity_file_appointment);


        cSessions = new cSessions(this);
        user_id = cSessions.getUserID();
        dateBiMap = HashBiMap.create();

        textViewClinicName = (TextView) findViewById(R.id.textViewClinicName);
        textViewClinicName.setText(clinic_name);

        appointmentProgressBar = (ProgressBar) findViewById(R.id.appointmentProgressBar);

        thisCalendar = Calendar.getInstance();
        calendarDay = thisCalendar.get(Calendar.DAY_OF_MONTH);
        calendarMonth = thisCalendar.get(Calendar.MONTH);
        calendarYear = thisCalendar.get(Calendar.YEAR);

        btnBook = (Button) findViewById(R.id.btnBook);
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = new SimpleDateFormat("yyyy-M-dd", Locale.getDefault()).format(new Date());

                if (appointmentDate.equals(date))
                    dateEditText.setError("Current day is not available for booking.");
                else
                {
                    appointment_time = timeSpinner.getSelectedItem().toString();
                    appointment_time = appointment_time.substring(0, 5).trim();

                    if(appointment_time.equals("1:00"))
                    {
                        appointment_time = "13:00";
                    }
                    else if(appointment_time.equals("2:00"))
                    {
                        appointment_time = "14:00";
                    }
                    else if(appointment_time.equals("3:00"))
                    {
                        appointment_time = "15:00";
                    }
                    else if(appointment_time.equals("4:00"))
                    {
                        appointment_time = "16:00";
                    }

                    insertBooking();
                }
            }
        });

        dateEditText = (EditText) findViewById(R.id.dateEditText);
        dateEditText.setTextIsSelectable(true);
        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dateEditText.setError(null);
                DatePickerDialog datePickerDialog = new DatePickerDialog(FileAppointment.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        SimpleDateFormat month_date = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        int thisMonth = month + 1;
                        String date = year + "-" + thisMonth + "-" + day;
                        Date thisDate = null;
                        try {
                            thisDate = sdf.parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String chosenDay = month_date.format(thisDate);
                        dateEditText.setText(chosenDay);
                        appointmentDate = year + "-" + thisMonth + "-" + day;
                        getAppointmentData(appointmentDate);
                        //Toast.makeText(getApplicationContext(), appointmentDate, Toast.LENGTH_LONG).show();
                    }
                }, calendarYear, calendarMonth, calendarDay);
                datePickerDialog.getDatePicker().setMinDate(currentTimeMillis());
                datePickerDialog.show();
            }
        });

    }

    private void insertBooking()
    {
        //data for inputstream
        try
        {
            String data_string = URLEncoder.encode("user_id", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(user_id), "UTF-8");
            data_string += "&" + URLEncoder.encode("clinic_id", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(clinic_id), "UTF-8");
            data_string += "&" + URLEncoder.encode("appointment_date", "UTF-8") + "="
                    + URLEncoder.encode(appointmentDate, "UTF-8");
            data_string += "&" + URLEncoder.encode("appointment_time", "UTF-8") + "="
                    + URLEncoder.encode(appointment_time, "UTF-8");
            data_string += "&" + URLEncoder.encode("is_approved", "UTF-8") + "="
                    + URLEncoder.encode("0", "UTF-8");
            backgroundData = data_string;

        }
        catch(IOException e)
        {
            // do nothing
        }

        cAsyncTaskWaitData insertAppointmentRecord = new cAsyncTaskWaitData(new cAsyncTaskWaitData.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                appointmentProgressBar.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), MainUserInterface.class);
                startActivity(intent);
                finish();
            }
        });
        insertAppointmentRecord.myURL = getResources().getString(R.string.base_url) + "insertAppointment.php";
        insertAppointmentRecord.execute(backgroundData);
    }

    private void getAppointmentData(String appointmentDate)
    {
        appointmentProgressBar.setVisibility(View.VISIBLE);
        try
        {
            String data_string = URLEncoder.encode("clinic_id", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(clinic_id), "UTF-8");
            data_string +=  "&" + URLEncoder.encode("appointment_date", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(appointmentDate), "UTF-8");

            backgroundData = data_string;

        }
        catch(IOException e)
        {
            // do nothing
        }


        cAsyncTaskWaitData getAppointmentsOnDay = new cAsyncTaskWaitData(new cAsyncTaskWaitData.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                populateSpinner(result);
            }
        });

        getAppointmentsOnDay.myURL = getResources().getString(R.string.base_url) + "getClinicAppointments.php";
        getAppointmentsOnDay.execute(backgroundData);
    }

    private void populateSpinner(String result)
    {
        timeArray = new ArrayList<>();

        if(result.equalsIgnoreCase("{\"server_response\":[]}"))
        {
            int x = 9;
            for(x=9; x<=11; x++)
            {
                timeArray.add(x + ":00 AM");
            }
            timeArray.add(x+ ":00 PM");
            for(int y = 1; y<=4; y++)
            {
                timeArray.add(y+ ":00 PM");
            }
        }
        else
        {
            try
            {
                JSONObject object = new JSONObject(result);
                JSONArray JArray  = object.getJSONArray("server_response");

                for (int i = 0; i < JArray.length(); i++)
                {
                    JSONObject JSONObj= JArray.getJSONObject(i);
                    dateBiMap.put(JSONObj.getString("appointment_time"),JSONObj.getString("appointment_time"));
                }

                if(!dateBiMap.get("09:00:00").equalsIgnoreCase("09:00:00"))
                {
                    timeArray.add("09:00:00 AM");
                }

                for(int x=10; x<=11; x++)
                {
                    String thisTime = x+ ":00:00";
                    if(TextUtils.isEmpty(dateBiMap.get(thisTime)))
                    {
                        timeArray.add(x+":00 AM");
                    }
                }

                for(int x=12; x<=16; x++)
                {
                    int y = x;
                    String thisTime = x+":00:00";
                    if(TextUtils.isEmpty(dateBiMap.get(thisTime)))
                    {
                        if(x>12)
                            y = y - 12;
                        timeArray.add(y+ ":00 PM");
                    }
                }

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }





        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, timeArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner = (Spinner) findViewById(R.id.timeSpinner);
        timeSpinner.setAdapter(adapter);

        appointmentProgressBar.setVisibility(View.INVISIBLE);


    }
}
