package com.example.rider_atrafficsolution;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Switch;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {

    Switch userTypeSwitch;
    Button getStartedButton;
    public static boolean isuserTypeSwitchChecked;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Rider A Traffic Solution");
        getSupportActionBar().hide();
        userTypeSwitch=findViewById(R.id.userTypeSwitch);
        getStartedButton=findViewById(R.id.Get_Started_Button);
        Log.i("start","Start");

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isuserTypeSwitchChecked=userTypeSwitch.isChecked();

               // if(isuserTypeSwitchChecked==true)
                //{
                  //  Intent intent=new Intent(getApplicationContext(),DriverVehicleTypeSelection.class);
                   // startActivity(intent);
               // }
                //finish();
                //else
               // {
                    Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);

                //}


            }
        });

        context = getBaseContext();


      //  Intent intent = new Intent(getApplicationContext(), CarBikeSearchActivity.class);


//        Intent intent=new Intent(getApplicationContext(),BusSeatSelection.class);
//        Intent intent=new Intent(getApplicationContext(),DriverCollectCashActivity.class);
//        startActivity(intent);


        //Info.currentEmail = "aaatowsif16@gmail.com";


    }


    public void postData(String code, String email, String validity, int count, double percentage, double max_amount)
    {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = "https://rider-a-traffic-solution-default-rtdb.firebaseio.com/Discount.json";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("promoCode", code);
            jsonBody.put("userEmail", email);
            jsonBody.put("applied", false);
            jsonBody.put("validity", validity);
            jsonBody.put("count", count);
            jsonBody.put("percentage", percentage);
            jsonBody.put("max_amount", max_amount);


            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError
                {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}