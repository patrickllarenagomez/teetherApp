package com.teether.patrick.teetherapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView signUpTextView;
    private TextView forgotPasswordTextView;
    AwesomeValidation awesomeValidation;
    private Button signInBtn;
    private EditText emailAddressEditText;
    private EditText passwordEditText;
    private String user_email_address;
    private String password;
    private String user_first_name;
    private int is_active;
    private int user_level;
    private int user_id;
    private String user_credentials;
    private String backgroundData;
    private ProgressBar spinner;
    Handler handler;
    private cSessions sessions;

    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        setContentView(R.layout.activity_main);

        sessions = new cSessions(this);
        if(sessions.getUserID() != 0)
        {
            Intent intent = new Intent(this, MainUserInterface.class);
            startActivity(intent);
            finish();
        }

//        button2 = (Button) findViewById(R.id.button2);
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
//                startActivity(intent);
//            }
//        });

        awesomeValidation = new AwesomeValidation(BASIC);
        handler = new Handler();
        spinner = (ProgressBar) findViewById(R.id.loginProgressBar);
        spinner.setVisibility(View.GONE);


        signUpTextView = (TextView) findViewById(R.id.signUpTextView);

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterUser.class);
                startActivity(intent);
            }
        });

        forgotPasswordTextView = (TextView) findViewById(R.id.forgotPasswordTextView);

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
            }
        });

        emailAddressEditText = (EditText) findViewById(R.id.emailAddressEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);


        signInBtn = (Button) findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(this);
        form_validate();
    }

    @Override
    public void onClick(View view)
    {
        if(awesomeValidation.validate())
        {
            user_email_address = emailAddressEditText.getText().toString().trim();
            password = passwordEditText.getText().toString().trim();

            cHashingPasswords cHashingPasswords = new cHashingPasswords();

            String hashedPassword = cHashingPasswords.hash(password).toString();

            try
            {
                String data_string = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(user_email_address, "UTF-8");
                data_string += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(hashedPassword, "UTF-8");

                backgroundData = data_string;
            }
            catch(IOException e)
            {
                // do nothing
            }


            cBackgroundTaskRetrieve cBackgroundTaskRetrieve = new cBackgroundTaskRetrieve();
            cBackgroundTaskRetrieve.myURL = getResources().getString(R.string.base_url) + "login.php";
            user_credentials = null;
            try
            {
                user_credentials = cBackgroundTaskRetrieve.execute(backgroundData).get();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
            }

            if(user_credentials != null)
            {
                spinner.setVisibility(View.VISIBLE);
                try
                {
                    JSONObject object = new JSONObject(user_credentials);
                    JSONArray Jarray  = object.getJSONArray("server_response");

                    for (int i = 0; i < Jarray.length(); i++)
                    {
                        JSONObject JSONObj= Jarray.getJSONObject(i);
                        user_id = JSONObj.getInt("user_id");
                        user_email_address = JSONObj.getString("user_email_address");
                        user_first_name = JSONObj.getString("user_first_name");
                        user_level = JSONObj.getInt("user_level");
                        is_active = JSONObj.getInt("is_active");
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.GONE);

                    }
                }, 1000);


                if(is_active == 0)
                {
                    Intent intent = new Intent(this, ConfirmCode.class);
                    intent.putExtra("user_id",user_id);
                    intent.putExtra("user_email_address", user_email_address);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    sessions.setUserID(user_id);
                    sessions.setEmail(user_email_address);
                    sessions.setFirstName(user_first_name);
                    sessions.setUserLevel(user_level);
                    Intent intent = new Intent(this, MainUserInterface.class);
                    startActivity(intent);
                }

            }
      }
        else
            {
                Toast.makeText(this, "Wrong User Credentials", Toast.LENGTH_SHORT).show();
            }
    }

    public void form_validate()
    {
        awesomeValidation.addValidation(this, R.id.emailAddressEditText, Patterns.EMAIL_ADDRESS , R.string.invalid_email);
    }
}
