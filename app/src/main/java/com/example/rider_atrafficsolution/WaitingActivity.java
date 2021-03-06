package com.example.rider_atrafficsolution;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaitingActivity extends AppCompatActivity
{
    RequestQueue requestQueue;
    Context context;
    boolean accepted = false;
    int driverID = 0;

    private ReentrantLock lock;
    private double sourceLat;
    private double sourceLong;
    private double destLat;
    private double destLong;
    private double driverLat;
    private double driverLong;
    String key;
    String type;
    boolean busy;
    String keyForRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        busy = false;

        context = getBaseContext();
        requestQueue = Volley.newRequestQueue(context);
        lock = new ReentrantLock();

        Intent intent = this.getIntent();

        driverLat = 0;
        driverLong = 0;

        sourceLat = intent.getDoubleExtra("sourceLat", 1);
        sourceLong = intent.getDoubleExtra("sourceLong", 1);
        destLat = intent.getDoubleExtra("destLat", 1);
        destLong = intent.getDoubleExtra("destLong", 1);
        type = intent.getStringExtra("type");
        keyForRequest = intent.getStringExtra("key");


        Handler handler =new Handler();
        final Runnable r = new Runnable()
        {
            public void run()
            {
                if(accepted && driverID != 0)
                {
                    //update();
                    GetDriverLocation();

                    //return;
                }

                if(busy)
                {
                    Intent intent = new Intent(getApplicationContext(), UserSideDriverLocationUpdateActivity.class);

                    intent.putExtra("sourceLat", sourceLat);
                    intent.putExtra("sourceLong", sourceLong);
                    intent.putExtra("destLat", destLat);
                    intent.putExtra("destLong", destLong);
                    intent.putExtra("driverLat", driverLat);
                    intent.putExtra("driverLong", driverLong);
                    intent.putExtra("type", type);
                    intent.putExtra("key", key);
                    intent.putExtra("driverID", driverID);

                    intent.putExtra("classid","waiting");

                    startActivity(intent);

                    return;
                }


                handler.postDelayed(this, 5000);
                Log.i("driver request timer", "updated after 5 seconds");

                checkIfAccepted();
            }
        };
        handler.postDelayed(r, 0000);
    }

    void update()
    {
        Log.i("accepted by", String.valueOf(driverID));

        GetDriverLocation();


    }

    synchronized void checkIfAccepted()
    {
        lock.lock();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://rider-a-traffic-solution-default-rtdb.firebaseio.com/Request/" + keyForRequest + ".json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject)
            {
                //type.clear();
                //try
                {

                    //for(int i=0;i<array.length();i++)
                    {
                        try
                        {

                            boolean pending = jsonObject.getBoolean("pending");


                            if(pending)
                                return;

                            String user = jsonObject.getString("userEmail");
                            String t = jsonObject.getString("type");

                            if(user.equalsIgnoreCase(Info.currentEmail) && type.equalsIgnoreCase(t))
                            {
                                accepted = true;

                                driverID = jsonObject.getInt("accepted_by");

                                key = keyForRequest;

                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("error: " , error.getMessage());
            }
        });

        requestQueue.add(jsonObjectRequest);

        lock.unlock();
    }

    synchronized void GetDriverLocation()
    {
        lock.lock();

        CustomPriorityRequest jsonObjectRequest = new CustomPriorityRequest(Request.Method.GET, "https://rider-a-traffic-solution-default-rtdb.firebaseio.com/Driver.json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                //type.clear();
                //try
                {
                    JSONArray array = response.names();

                    for(int i=0;i<array.length();i++)
                    {
                        try
                        {
                            String key = array.getString(i);

                            JSONObject jsonObject = response.getJSONObject(key);

                            String id = jsonObject.getString("driverID");

                            if(id.equalsIgnoreCase(String.valueOf(driverID)))
                            {
                                driverLat = jsonObject.getDouble("lat");
                                driverLong = jsonObject.getDouble("long");

                                busy = true;

                                break;
                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("error: " , error.getMessage());
            }
        });

        jsonObjectRequest.setPriority(Request.Priority.IMMEDIATE);
        requestQueue.add(jsonObjectRequest);

        lock.unlock();
    }

}