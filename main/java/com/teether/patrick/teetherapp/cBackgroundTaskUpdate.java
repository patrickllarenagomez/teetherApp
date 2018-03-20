package com.teether.patrick.teetherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static java.lang.Boolean.TRUE;

/**
 * Created by Patrick on 1/17/2018.
 */

public class cBackgroundTaskUpdate extends AsyncTask<String, Void, String>
{
    public String myURL;
    public String data_string;
    private String JSON_response;
    Context thisContext;

    public String getJSON(String result)
    {
        return result;
    }

    public void forContext(Context context)
    {
        thisContext = context;
    }

    @Override
    protected String doInBackground(String... args)
    {
        data_string = args[0];

        try
        {
            URL url = new URL(myURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(TRUE);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(data_string);
            bufferedWriter.flush();
            bufferedWriter.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            while((JSON_response = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(JSON_response+"\n");
            }
            outputStream.close();
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return stringBuilder.toString().trim();

        }
        catch (MalformedURLException e)
        {
            // do nothing
        }
        catch(IOException e)
        {
            // do nothing
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values)
    {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result)
    {
           data_string = null;
           getJSON(result);
    }
}
