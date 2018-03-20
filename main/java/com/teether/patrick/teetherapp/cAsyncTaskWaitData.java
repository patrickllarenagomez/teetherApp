package com.teether.patrick.teetherapp;

import android.os.AsyncTask;

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

import static java.lang.Boolean.TRUE;

/**
 * Created by Patrick on 2/1/2018.
 */


public class cAsyncTaskWaitData extends AsyncTask<String, Void, String> {
    private OnTaskCompleted listener;

    public String myURL;
    public String data_string;
    String JSON_response;


    public interface OnTaskCompleted{
        void onTaskCompleted(String result);
    }


        public cAsyncTaskWaitData(OnTaskCompleted listener){
            this.listener=listener;
        }

    @Override
    protected String doInBackground(String... args) {

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


    // required methods

    protected void onPostExecute(String result)
    {
            listener.onTaskCompleted(result);
        }
    }


