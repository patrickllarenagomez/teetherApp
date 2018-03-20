package com.teether.patrick.teetherapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class ConfirmCode extends AppCompatActivity
{
    private String user_code;
    private EditText codeEditText;
    private String userEnteredCode;
    private String backgroundData;
    private String user_code_id;
    private String user_email_address;
    private ProgressBar spinner;
    Handler handler;
    private int user_id;
    private String asyncData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        setContentView(R.layout.activity_confirm_code);

        handler = new Handler();
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();


        if(bundle != null && bundle.getString("code") != null && bundle.getString("user_code_id") != null)
        {
            user_code = bundle.getString("code");
            user_code_id = bundle.getString("user_code_id");
        }

        if(bundle != null && bundle.getInt("user_id", 0) != 0)
        {
            user_id = bundle.getInt("user_id", 0);
            user_email_address = bundle.getString("user_email_address");
            try
            {
                String data_string = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(user_id), "UTF-8");
                asyncData = data_string;
            }
            catch(IOException e)
            {
                // do nothing
            }

            spinner.setVisibility(View.VISIBLE);
            cBackgroundTaskUpdate cBackgroundTaskUpdate = new cBackgroundTaskUpdate();
            cBackgroundTaskUpdate.myURL = getResources().getString(R.string.base_url) + "renewCode.php";
            cBackgroundTaskUpdate.execute(asyncData);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    spinner.setVisibility(View.GONE);
                }
            }, 300);

            try
            {
                String data_string = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(user_id), "UTF-8");
                asyncData = data_string;
            }
            catch(IOException e)
            {
                // do nothing
            }

            cBackgroundTaskUpdate cBackgroundTaskUpdate1 = new cBackgroundTaskUpdate();
            cBackgroundTaskUpdate1.myURL = getResources().getString(R.string.base_url) + "retrieve_code.php";
            String JSON_Data = null;
            try {
                JSON_Data=cBackgroundTaskUpdate1.execute(asyncData).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            try
            {
                JSONObject object = new JSONObject(JSON_Data);
                JSONArray JArray  = object.getJSONArray("server_response");

                for (int i = 0; i < JArray.length(); i++)
                {
                    JSONObject JSONObj= JArray.getJSONObject(i);
                    user_code = JSONObj.getString("code");
                    user_code_id = JSONObj.getString("code_id");
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            try
            {
                String data_string = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(user_email_address, "UTF-8");
                data_string += "&" + URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(user_code, "UTF-8");
                asyncData = data_string;
            }
            catch(IOException e)
            {
                // do nothing
            }

            cBackgroundTaskInsert cBackgroundTaskInsert = new cBackgroundTaskInsert();
            cBackgroundTaskInsert.myURL = getResources().getString(R.string.base_url) + "sendRegistrationCode.php";
            cBackgroundTaskInsert.execute(asyncData);
        }

        codeEditText = (EditText) findViewById(R.id.codeEditText);

        codeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                userEnteredCode = codeEditText.getText().toString();

                if(user_code.equals(userEnteredCode))
                {
                    spinner.setVisibility(View.VISIBLE);
                    try
                    {
                        String data_string = URLEncoder.encode("user_code_id", "UTF-8") + "=" + URLEncoder.encode(user_code_id, "UTF-8");
                        backgroundData = data_string;
                    }
                    catch(IOException e)
                    {
                        // do nothing
                    }

                    cBackgroundTaskUpdate cBackgroundTaskUpdate = new cBackgroundTaskUpdate();
                    cBackgroundTaskUpdate.myURL = getResources().getString(R.string.base_url) + "deleteUserCode.php";
                    cBackgroundTaskUpdate.execute(backgroundData);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            spinner.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 2000);

                }
                else if(s.length() == 5)
                    {
                        Toast.makeText(ConfirmCode.this, "Wrong Code", Toast.LENGTH_SHORT).show();
                    }
            }
        });

    }

}
