package com.teether.patrick.teetherapp;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;

public class ClinicDetails extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar imageProgressBar;
    private Handler handler;
    private String backgroundData;
    private String url;
    private int clinic_id;
    private RecyclerView recyclerViewServices;
    private RecyclerView recyclerViewReviews;
    private DividerItemDecoration mDividerItemDecoration;
    private DividerItemDecoration cDividerItemDecoration;
    private List<cClinicsService> cClinicsListServices;
    private List<cClinicsReview> cClinicsListReviews;
    private BiMap<String, String> userBiMap;
    private RecyclerView.Adapter reviewAdapter;
    private RecyclerView.Adapter serviceAdapter;
    private String clinic_name;
    private Button btnFileAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        // storage for user key and value
        userBiMap = HashBiMap.create();
        clinic_name = bundle.getString("clinic_name");
        clinic_id = bundle.getInt("clinic_id");

        setTitle(clinic_name);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        setContentView(R.layout.activity_clinic_details);


        handler = new Handler();
        imageView = (ImageView) findViewById(R.id.clinicImageView);

        imageProgressBar = (ProgressBar) findViewById(R.id.imageProgressBar);
        imageProgressBar.setVisibility(View.VISIBLE);


        recyclerViewServices = (RecyclerView) findViewById(R.id.recyclerViewServices);
        recyclerViewServices.setHasFixedSize(true);
        recyclerViewServices.setLayoutManager(new LinearLayoutManager(this));

        mDividerItemDecoration = new DividerItemDecoration(
                recyclerViewServices.getContext(),
                1
        );

        recyclerViewServices.addItemDecoration(mDividerItemDecoration);


        recyclerViewReviews = (RecyclerView) findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setHasFixedSize(true);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));

        cDividerItemDecoration = new DividerItemDecoration(
                recyclerViewReviews.getContext(),
                1
        );

        recyclerViewReviews.addItemDecoration(cDividerItemDecoration);


        //data for inputstream
        try
        {
            String data_string = URLEncoder.encode("clinic_id", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(clinic_id), "UTF-8");

            backgroundData = data_string;

        }
        catch(IOException e)
        {
            // do nothing
        }

        //get image data
        cAsyncTaskWaitData cAsyncTaskWaitData = new cAsyncTaskWaitData(new cAsyncTaskWaitData.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result)
            {
                parseImageData(result);
                getImage(url);
            }
        });

        cAsyncTaskWaitData.myURL = getResources().getString(R.string.base_url) + "getClinicDetails.php";
        cAsyncTaskWaitData.execute(backgroundData);

//        get user profiles
        cAsyncTaskWaitData getUserProfiles = new cAsyncTaskWaitData(new cAsyncTaskWaitData.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result)
            {
                parseUserData(result);
            }
        });

        getUserProfiles.myURL = getResources().getString(R.string.base_url) + "getUserProfiles.php";
        getUserProfiles.execute("");

        getReviews();
        getServices();


        btnFileAppointment = (Button) findViewById(R.id.btnFileAppointment);
        btnFileAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FileAppointment.class);
                intent.putExtra("clinic_id",clinic_id);
                intent.putExtra("clinic_name", clinic_name);
                startActivity(intent);
            }
        });
    }


    // parsing user json data from server
    private void parseUserData(String result)
    {
        try
        {
            JSONObject object = new JSONObject(result);
            JSONArray Jarray  = object.getJSONArray("server_response");

            for (int i = 0; i < Jarray.length(); i++)
            {
                JSONObject JSONObj= Jarray.getJSONObject(i);
                userBiMap.put(JSONObj.getString("user_id"),
                        JSONObj.getString("user_first_name") + " "
                                + JSONObj.getString("user_last_name"));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    private void parseImageData(String result)
    {
        try
        {
            JSONObject object = new JSONObject(result);
            JSONArray Jarray  = object.getJSONArray("server_response");

            for (int i = 0; i < Jarray.length(); i++)
            {
                JSONObject JSONObj= Jarray.getJSONObject(i);
                url = getResources().getString(R.string.secured_base_url);
                url += JSONObj.getString("clinic_image");
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    private void parseReviewData(String result)
    {
        cClinicsListReviews = new ArrayList<>();

        try
        {
            JSONObject object = new JSONObject(result);
            JSONArray Jarray  = object.getJSONArray("server_response");

            for (int i = 0; i < Jarray.length(); i++)
            {
                JSONObject JSONObj= Jarray.getJSONObject(i);
                cClinicsReview clinics = new cClinicsReview
                        (
                                userBiMap.get(JSONObj.getString("user_id")),
                                JSONObj.getString("clinic_review_text"),
                                JSONObj.getInt("clinic_rating")
                        );
                cClinicsListReviews.add(clinics);
            }

            reviewAdapter = new cClinicsReviewAdapter(cClinicsListReviews, this);
            recyclerViewReviews.setAdapter(reviewAdapter);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void parseServicesData(String result)
    {
        cClinicsListServices = new ArrayList<>();

        try
        {
            JSONObject object = new JSONObject(result);
            JSONArray Jarray  = object.getJSONArray("server_response");

            for (int i = 0; i < Jarray.length(); i++)
            {
                JSONObject JSONObj= Jarray.getJSONObject(i);
                cClinicsService cClinicsService = new cClinicsService(
                        JSONObj.getString("service_name"),
                        "\u20B1 " + NumberFormat.getNumberInstance(Locale.US).format(
                                JSONObj.getDouble("service_cost"))
                );
                cClinicsListServices.add(cClinicsService);
            }
            serviceAdapter = new cClinicsServiceAdapter(cClinicsListServices, this);
            recyclerViewServices.setAdapter(serviceAdapter);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void getReviews()
    {
        try
        {
            String data_string = URLEncoder.encode("clinic_id", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(clinic_id), "UTF-8");

            backgroundData = data_string;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        cAsyncTaskWaitData getReviewsData = new cAsyncTaskWaitData(new cAsyncTaskWaitData.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                parseReviewData(result);
            }
        });

        getReviewsData.myURL = getResources().getString(R.string.base_url) + "getClinicReviews.php";
        getReviewsData.execute(backgroundData);
    }

    private void getServices()
    {
        try
        {
            String data_string = URLEncoder.encode("clinic_id", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(clinic_id), "UTF-8");

            backgroundData = data_string;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        cAsyncTaskWaitData getServicesData = new cAsyncTaskWaitData(new cAsyncTaskWaitData.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                parseServicesData(result);
            }
        });

        getServicesData.myURL = getResources().getString(R.string.base_url) + "getClinicServices.php";
        getServicesData.execute(backgroundData);
    }

    private void getImage(String url)
    {
        Glide
                .with(this)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        imageProgressBar.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        imageProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }
}
