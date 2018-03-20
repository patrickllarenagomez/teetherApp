package com.teether.patrick.teetherapp;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoogleMaps extends AppCompatActivity implements OnMapReadyCallback {

    private String markerData;
    Handler handler;
    private GoogleMap mMap;
    private ProgressBar spinner;
    SupportMapFragment mapFragment;
    private MarkerOptions options = new MarkerOptions();
    Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DividerItemDecoration mDividerItemDecoration;
    private List<cClinics> cClinicsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Find A Clinic");
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        setContentView(R.layout.activity_google_layout);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewClinics);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                1
        );
        recyclerView.addItemDecoration(mDividerItemDecoration);



        context = this;
        handler = new Handler();
        spinner = (ProgressBar) findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);

        _getMarkerData();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.GONE);
                callMap();
                populateList();
            }
        }, 2000);

    }

    public void callMap()
    {
        mapFragment.getMapAsync(this);
    }

    private void _getMarkerData()
    {
        cAsyncTaskWaitData mapTask = new cAsyncTaskWaitData(new cAsyncTaskWaitData.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(String result) {
                markerData = result;

            }
        });
        mapTask.myURL = getResources().getString(R.string.base_url) + "getMarkers.php";
        mapTask.execute("");

    }

    public void populateList()
    {
        cClinicsList = new ArrayList<>();

            try
            {
                JSONObject object = new JSONObject(markerData);
                JSONArray JArray  = object.getJSONArray("server_response");

                for (int i = 0; i < JArray.length(); i++)
                {
                    JSONObject JSONObj= JArray.getJSONObject(i);
                    cClinics clinics = new cClinics
                            (
                                    JSONObj.getString("title"),
                                    JSONObj.getString("address"),
                                    JSONObj.getInt("id")
                            );
                    cClinicsList.add(clinics);
                }

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        adapter = new cClinicsAdapter(cClinicsList, this);
        recyclerView.setAdapter(adapter);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        Date opening = null;
        Date closing = null;
        SimpleDateFormat newFormat = new SimpleDateFormat("hh:mm a");
        try
        {
            JSONObject object = new JSONObject(markerData);

            JSONArray JArray  = object.getJSONArray("server_response");
            for (int i = 0; i < JArray.length(); i++)
            {


                JSONObject JSONObj= JArray.getJSONObject(i);
                LatLng latLng = new LatLng(JSONObj.getDouble("lat"),JSONObj.getDouble("lng"));

                try {
                    opening = (Date)formatter.parse(JSONObj.getString("opening"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    closing = (Date)formatter.parse(JSONObj.getString("closing"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String openingString = newFormat.format(opening);
                String closingString = newFormat.format(closing);
                options.position(latLng);
                options.title(JSONObj.getString("title"));
                options.snippet("Address: " + JSONObj.getString("address") + "\n" + "Clinic Hours : "
                        + openingString + " - " +  closingString);
                mMap.addMarker(options);

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(context);
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(context);
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(context);
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(14.599002, 121.005308))
                .radius(300)
                .strokeColor(0x110000ff)
                .fillColor(0x440000ff));

        Circle location = mMap.addCircle(new CircleOptions()
                .center(new LatLng(14.599002, 121.005308))
                .radius(10)
                .strokeColor(0x44888888)
                .fillColor(0xff0000ff));


        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(14.599002,121.005308)));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(16);
        mMap.animateCamera(zoom);
    }
}


