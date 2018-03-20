package com.teether.patrick.teetherapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationHolder;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.common.hash.HashCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;
import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener
{
    EditText firstNameEditText,lastNameEditText,emailAddressEditText,passwordEditText,
            confirmPasswordEditText;
    String firstName, lastName, emailAddress, password, confirmPassword;
    Button registerBtn;
    private AwesomeValidation AwesomeValidation;
    private ProgressDialog dialog;
    private String code;
    private String backgroundData;
    private ProgressBar spinner;
    private int user_id;
    private String retrieveData;
    private String user_code_id;
    private String Addresses;
    public String getData;
    Handler handler;
    JSONObject object;
    JSONArray Jarray;
    ArrayList<String> emailArray;
    private boolean validated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(R.string.signUp);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        setContentView(R.layout.activity_register_user);

        handler = new Handler();
        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        emailAddressEditText = (EditText) findViewById(R.id.emailAddressEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        spinner = (ProgressBar) findViewById(R.id.registrationProgressBar);

        spinner.setVisibility(View.GONE);
        registerBtn.setOnClickListener(this);
        emailArray = new ArrayList<String>();

        AwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);


        form_validate();
    }

    public void form_validate()
    {
        String regexPassword = ".{6,10}";
        AwesomeValidation.addValidation(this,
                R.id.firstNameEditText, "[a-zA-Z\\s]+", R.string.invalid_name);
        AwesomeValidation.addValidation(this,
                R.id.firstNameEditText, RegexTemplate.NOT_EMPTY, R.string.invalid_name);
        AwesomeValidation.addValidation(this,
                R.id.lastNameEditText, "[a-zA-Z\\s]+", R.string.invalid_name);
        AwesomeValidation.addValidation(this,
                R.id.emailAddressEditText, Patterns.EMAIL_ADDRESS , R.string.invalid_email);
        AwesomeValidation.addValidation(this,
                R.id.passwordEditText, RegexTemplate.NOT_EMPTY, R.string.empty_password);
        AwesomeValidation.addValidation(this,
                R.id.passwordEditText, regexPassword, R.string.password_regex_char);
        AwesomeValidation.addValidation(this,
                R.id.confirmPasswordEditText, RegexTemplate.NOT_EMPTY, R.string.empty_password);
        AwesomeValidation.addValidation(this,
                R.id.confirmPasswordEditText, R.id.passwordEditText, R.string.invalid_confirm_password);

        cBackgroundTaskRetrieve cBackgroundTaskRetrieve = new cBackgroundTaskRetrieve();
        cBackgroundTaskRetrieve.myURL = getResources().getString(R.string.base_url) + "getEmailAddresses.php";

        try {
            Addresses = cBackgroundTaskRetrieve.execute("").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
         try
            {
                object = new JSONObject(Addresses);
                Jarray  = object.getJSONArray("server_response");

                for (int i = 0; i < Jarray.length(); i++)
                {
                    JSONObject JSONObj= Jarray.getJSONObject(i);
                    emailArray.add(JSONObj.getString("user_email_address"));
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

    }

    @Override
    public void onClick(View view)
    {
        AwesomeValidation.clear();
        for(int i=0; i<emailArray.size(); i++) {
            if (emailAddressEditText.getText().toString().trim().equalsIgnoreCase(emailArray.get(i)))
            {
                emailAddressEditText.setError(getText(R.string.email_taken));
                validated = FALSE;
                break;
            }
            else
            {
                validated = TRUE;
            }
        }

        if(AwesomeValidation.validate() && validated==TRUE)
        {
            spinner.setVisibility(View.VISIBLE);

            firstName = firstNameEditText.getText().toString().trim();
            lastName = lastNameEditText.getText().toString().trim();
            emailAddress = emailAddressEditText.getText().toString().trim();
            password = passwordEditText.getText().toString().trim();
            confirmPassword = confirmPasswordEditText.getText().toString().trim();

            cHashingPasswords hashingPasswords = new cHashingPasswords();
            HashCode hashedCode= hashingPasswords.hash(password);
            String hashedPassword = hashedCode.toString();

            try
            {
                String data_string = URLEncoder.encode("firstname", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8");
                data_string += "&" + URLEncoder.encode("lastname", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8");
                data_string += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(emailAddress, "UTF-8");
                data_string += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(hashedPassword, "UTF-8");

                backgroundData = data_string;

            }
            catch(IOException e)
            {
                // do nothing
            }

            cBackgroundTaskInsert backgroundTask = new cBackgroundTaskInsert();
            backgroundTask.myURL = getResources().getString(R.string.base_url) +  "register_user.php";
            backgroundTask.data_string = backgroundData;
            backgroundTask.forContext(this);
            String id = null;
            try {
                id = backgroundTask.execute(backgroundData).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            //get user_id in JSON
            try
            {
                JSONObject object = new JSONObject(id);
                JSONArray Jarray  = object.getJSONArray("server_response");

                for (int i = 0; i < Jarray.length(); i++)
                {
                    JSONObject JSONObj= Jarray.getJSONObject(i);
                    user_id = JSONObj.getInt("user_id");
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            try
            {
                String data_string = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(user_id), "UTF-8");

                retrieveData = data_string;
            }
            catch(IOException e)
            {
                // do nothing
            }

            cBackgroundTaskRetrieve cBackgroundTaskRetrieve = new cBackgroundTaskRetrieve();
            cBackgroundTaskRetrieve.myURL = getResources().getString(R.string.base_url) + "retrieve_code.php";
            //cBackgroundTaskRetrieve.data_string = retrieveData;
            String code = null;
            try {
                code = cBackgroundTaskRetrieve.execute(retrieveData).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            try
            {
                JSONObject object = new JSONObject(code);
                JSONArray JArray  = object.getJSONArray("server_response");

                for (int i = 0; i < JArray.length(); i++)
                {
                    JSONObject JSONObj= JArray.getJSONObject(i);
                    code = JSONObj.getString("code");
                    user_code_id = JSONObj.getString("code_id");
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            try
            {
                String data_string = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(emailAddress, "UTF-8");
                data_string += "&" + URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8");
                backgroundData = data_string;
            }
            catch(IOException e)
            {
                // do nothing
            }

            cBackgroundTaskInsert cBackgroundTaskInsert = new cBackgroundTaskInsert();
            cBackgroundTaskInsert.myURL = getResources().getString(R.string.base_url) + "sendRegistrationCode.php";
            cBackgroundTaskInsert.execute(backgroundData);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    spinner.setVisibility(View.GONE);

                }
            }, 500);

            try
            {
                String data_string = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(user_id), "UTF-8");
                data_string += "&" + URLEncoder.encode("full_name", "UTF-8") + "=" + URLEncoder.encode(firstName + lastName, "UTF-8");
                backgroundData = data_string;

            }
            catch(IOException e)
            {
                // do nothing
            }

            cBackgroundTaskInsert makeFolder = new cBackgroundTaskInsert();
            makeFolder.myURL = getResources().getString(R.string.base_url) + "makeFolder.php";
            makeFolder.execute(backgroundData);



            Intent intent = new Intent(getApplicationContext(), ConfirmCode.class);
            intent.putExtra("code", code);
            intent.putExtra("user_code_id", user_code_id);
            startActivity(intent);
            finish();

        }
    }

}


