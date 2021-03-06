package com.example.rider_atrafficsolution;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BusSeatSelection extends AppCompatActivity
{
    private static Context context;
    Spinner busfromSpinner,bustoSpinner,whichbuSpinner, seatCountSpinner;
    private String from;
    private String to;
    private double fare;
    List<String> buses;
    List<String> locations;
    List<String> counts;
    List<Integer> busId;

    ReentrantLock lock;

    private Button confirmButton;
    TextView fareShowTextView;
    List<Double> distanceList;
    List<Double> currentLats;
    List<Double> currentLongs;
    TextView timeTextView;

    public List<Double> fromLatLong;
    public List<Double> toLatLong;

    //public static double fromLat = 0, fromLong = 0 ,toLat = 0, toLong = 0;

    public double minDistLat;
    public double minDistLong;
    public int nearestBusId;
    public String keyForNearestBusID;

    ArrayAdapter<String> busAdapter;
    ArrayAdapter<String> locationAdapter;
    ArrayAdapter<String > countsAdapter;

    private RequestQueue requestQueue;
    private String key;
    private boolean flag;
    private ArrayList<String > allRouteLocations;
    private ArrayList<LatLng> allRouteLatLong;
    private boolean gotRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_seat_selection);

        allRouteLocations = new ArrayList<>();
        allRouteLatLong = new ArrayList<>();
        gotRoute = false;

        flag = false;

        busfromSpinner=findViewById(R.id.busFromSpinner);
        bustoSpinner=findViewById(R.id.busToSpinner);
        whichbuSpinner=findViewById(R.id.whichBusSpinner);
        seatCountSpinner = findViewById(R.id.seatCountSpinner);
        fareShowTextView = findViewById(R.id.fareShowTextView);
        confirmButton = findViewById(R.id.confirmButton);
        timeTextView = findViewById(R.id.timeTextView);

        context = getBaseContext();

        locations = new ArrayList<String>();
        locations.add(".");
        locations.add("Motijheel");
        locations.add("Malibag");
        locations.add("Shahbag");
        locations.add("Farmgate");
        locations.add("Mirpur");

        lock = new ReentrantLock();

        buses = new ArrayList<String>();
        from = "";
        to = "";

        buses.add(".");

        counts = new ArrayList<>();
        counts.add("1");
        counts.add("2");
        counts.add("3");
        counts.add("4");

        fromLatLong = new ArrayList<>();
        toLatLong = new ArrayList<>();
        distanceList = new ArrayList<>();
        currentLats = new ArrayList<>();
        currentLongs = new ArrayList<>();
        busId = new ArrayList<>();

        // Creating adapter for spinner
        locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locations);

        busAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, buses);

        countsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, counts);


        // Drop down layout style - list view with radio button
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        busAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        // attaching data adapter to spinner
        busfromSpinner.setAdapter(locationAdapter);
        bustoSpinner.setAdapter(locationAdapter);
        whichbuSpinner.setAdapter(busAdapter);
        seatCountSpinner.setAdapter(countsAdapter);

        requestQueue = Volley.newRequestQueue(context);


        busfromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position == 0)
                {
                    return;
                }
                from = busfromSpinner.getSelectedItem().toString();
                GetMethodForBusRoute();
                GetMethodForFare();
                GetMethodForCoOrdinates();

                timeTextView.setText("");
                //updateTime();
                hudai();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        bustoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position == 0)
                {
                    return;
                }
                to = bustoSpinner.getSelectedItem().toString();
                GetMethodForBusRoute();
                GetMethodForFare();
                GetMethodForCoOrdinates();

                timeTextView.setText("");
                //updateTime();
                hudai();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        seatCountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                GetMethodForFare();
                hudai();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        whichbuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position == 0)
                    return;

                GetMethodForCurrentDistance();

                hudai();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        confirmButton.setOnClickListener(v ->
        {

//            double dist = getDistanceFromLatLonInKm(fromLat, fromLong, toLat, toLong);
//            System.out.println("dist = " + dist);

            hudai();

            addBusJourney();

            GetLatLongForRoute();

            Intent intent = new Intent(getApplicationContext(),ShowBusLoationActivity.class);

            intent.putExtra("minDistLat", minDistLat);
            intent.putExtra("minDistLong", minDistLong);

            intent.putExtra("fromLat", fromLatLong.get(0));
            intent.putExtra("fromLong", fromLatLong.get(1));

            intent.putExtra("toLat", toLatLong.get(0));
            intent.putExtra("toLong", toLatLong.get(1));

            Log.i("nearest", String.valueOf(nearestBusId));
            intent.putExtra("busId", nearestBusId);

            double f = Double.parseDouble(fareShowTextView.getText().toString());
            intent.putExtra("fare", f);

            Handler h = new Handler();
            Runnable r = new Runnable()
            {
                @Override
                public void run()
                {
                    if(flag)
                    {
                        intent.putExtra("key", key);

                        return;
                    }
                    h.postDelayed(this, 1000);
                }
            };
            h.postDelayed(r, 0);

            Handler h2 = new Handler();
            Runnable r2 = new Runnable()
            {
                @Override
                public void run()
                {
                    if(gotRoute)
                    {
                        intent.putExtra("route", allRouteLatLong);

                        startActivity(intent);

                        return;
                    }
                    h2.postDelayed(this, 1000);
                }
            };
            h2.postDelayed(r2, 0);



        });
    }


    public void hudai()
    {

        for (String b : buses)
        {
            Log.i("b", b);
        }

        for (Double d : fromLatLong)
        {
            Log.i("d", String.valueOf(d));
        }

//        double dist = getDistanceFromLatLonInKm(fromLat, fromLong, toLat, toLong);
//        Log.i("dist in hudai = " , String.valueOf(dist));



        for(Double d : currentLats)
        {
            Log.i("lat", String.valueOf(d));
        }

        for(Double d : currentLongs)
        {
            Log.i("long", String.valueOf(d));
        }
    }

    void updateSeatCounts()
    {
        countsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, counts);

        countsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        seatCountSpinner.setAdapter(countsAdapter);
    }

    public void updateTime()
    {
        if(!distanceList.isEmpty())
        {
            double min = distanceList.get(0);
            minDistLat = currentLats.get(0);
            minDistLong = currentLongs.get(0);
            nearestBusId = busId.get(0);

            for(int i=0;i<distanceList.size();i++)
            {
                Double d = distanceList.get(i);
                Log.i("dst", String.valueOf(d));
                if (min > d)
                {
                    min = d;
                    minDistLat = currentLats.get(i);
                    minDistLong = currentLongs.get(i);
                    nearestBusId = busId.get(i);
                }
            }
            Log.i("min", String.valueOf(min));
            Log.i("minLat", String.valueOf(minDistLat));
            Log.i("minLong", String.valueOf(minDistLong));
            Log.i("id", String.valueOf(nearestBusId));

            int time = (int) Math.ceil(min/15 * 60);
            Log.i("time", String.valueOf(time) + " mins");

            timeTextView.setText(String.format("%s mins", String.valueOf(time)));

            GetAvailableSeats();
        }
    }

    public void GetMethodForFare()
    {
        lock.lock();
        //RequestQueue requestQueue = Volley.newRequestQueue(context);
        CustomPriorityRequest jsonObjectRequest = new CustomPriorityRequest(Request.Method.GET, "https://rider-a-traffic-solution-default-rtdb.firebaseio.com/fare.json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                //try
                {
                    JSONArray array = response.names();

                    //buses.clear();

                    for(int i=0;i<array.length();i++)
                    {
                        List<String> locations = new ArrayList<>();
                        try
                        {
                            String key = array.getString(i);

                            String location1 = response.getJSONObject(key).getString("location1");
                            String location2 = response.getJSONObject(key).getString("location2");

                            if((location1.equalsIgnoreCase(from) && location2.equalsIgnoreCase(to)) || (location2.equalsIgnoreCase(from) && location1.equalsIgnoreCase(to)) )
                            {
                                double count = Double.parseDouble(seatCountSpinner.getSelectedItem().toString());
                                fare = Double.parseDouble(response.getJSONObject(key).getString("fare"));
                                fare = fare * count;
                                Log.i("fare", String.valueOf(fare));
                                fareShowTextView.setText(String.valueOf(fare));
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

        jsonObjectRequest.setPriority(Request.Priority.HIGH);
        requestQueue.add(jsonObjectRequest);

        lock.unlock();
    }

    public void GetMethodForBusRoute()
    {
        lock.lock();
        //RequestQueue requestQueue = Volley.newRequestQueue(context);
        CustomPriorityRequest jsonObjectRequest = new CustomPriorityRequest(Request.Method.GET, "https://rider-a-traffic-solution-default-rtdb.firebaseio.com/BusRoute.json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                //try
                {
                    JSONArray array = response.names();

                    buses.clear();
                    buses.add(".");

                    allRouteLocations.clear();

                    for(int i=0;i<array.length();i++)
                    {
                        List<String> route = new ArrayList<>();
                        try
                        {
                            String key = array.getString(i);
                            route.add(response.getJSONObject(key).getString("from"));
                            //Log.i("names", response.getJSONObject(array.getString(i)).getString("from"));
                            String[] splitted = response.getJSONObject(key).getString("intermediate").split(",");

                            for(String s : splitted)
                            {
                                //Log.i("route", s);
                                route.add(s);
                            }

                            route.add(response.getJSONObject(key).getString("to"));


//                            Log.i("names", array.getString(i));

                            if (route.contains(from) && route.contains(to))
                            {
                                System.out.println(route);

                                String busnos = response.getJSONObject(key).getString("busNo");

                                String [] split = busnos.split(",");

                                for (String s : split)
                                {
                                    buses.add(s);
                                }

                                allRouteLocations.addAll(route);

                                break;
                            }


                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }



                    busAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, buses);

                    whichbuSpinner.setAdapter(busAdapter);
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

//        for (String b : buses)
//        {
//            Log.i("b", b);
//        }
    }

    public void GetMethodForCoOrdinates()
    {
        lock.lock();
        //RequestQueue requestQueue = Volley.newRequestQueue(context);
        CustomPriorityRequest jsonObjectRequest = new CustomPriorityRequest(Request.Method.GET, "https://rider-a-traffic-solution-default-rtdb.firebaseio.com/CoOrdinates.json", null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                //try
                {
                    JSONArray array = response.names();

                    fromLatLong.clear();
                    toLatLong.clear();

                    for (int i = 0; i < array.length(); i++)
                    {
                        try
                        {
                            String key = array.getString(i);
                            String location = response.getJSONObject(key).getString("location");

                            if (from.equalsIgnoreCase(location))
                            {
//                                fromLat = Double.parseDouble(response.getJSONObject(key).getString("lat"));
//                                fromLong = Double.parseDouble(response.getJSONObject(key).getString("long"));

//                                latlong.add(fromLat);
//                                latlong.add(fromLong);

                                double h1 = Double.parseDouble(response.getJSONObject(key).getString("lat"));
                                double h2 = Double.parseDouble(response.getJSONObject(key).getString("long"));

                                fromLatLong.add(h1);
                                fromLatLong.add(h2);

                            } else if (to.equalsIgnoreCase(location))
                            {
//                                toLat = Double.parseDouble(response.getJSONObject(key).getString("lat"));
//                                toLong = Double.parseDouble(response.getJSONObject(key).getString("long"));

//                                latlong.add(toLat);
//                                latlong.add(toLong);

                                double h3 = Double.parseDouble(response.getJSONObject(key).getString("lat"));
                                double h4 = Double.parseDouble(response.getJSONObject(key).getString("long"));

                                toLatLong.add(h3);
                                toLatLong.add(h4);
                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("error: ", error.getMessage());
            }
        });

        jsonObjectRequest.setPriority(Request.Priority.NORMAL);
        requestQueue.add(jsonObjectRequest);

        for (Double d : fromLatLong)
        {
            Log.i("d", String.valueOf(d));
        }

        lock.unlock();

//        Log.i("get", String.valueOf(h2));
//        Log.i("get", String.valueOf(h3));
//        Log.i("get", String.valueOf(h4));
//
//        double dist = getDistanceFromLatLonInKm(h1, h2, h3, h4);
//        Log.i("dist = " , String.valueOf(dist));
    }

    public void GetLatLongForRoute()
    {
        lock.lock();

        allRouteLatLong.clear();

        //RequestQueue requestQueue = Volley.newRequestQueue(context);
        CustomPriorityRequest jsonObjectRequest = new CustomPriorityRequest(Request.Method.GET, "https://rider-a-traffic-solution-default-rtdb.firebaseio.com/CoOrdinates.json", null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                //try
                {
                    JSONArray array = response.names();

                    fromLatLong.clear();
                    toLatLong.clear();

                    for (int i = 0; i < array.length(); i++)
                    {
                        try
                        {
                            String key = array.getString(i);

                            JSONObject jsonObject = response.getJSONObject(key);

                            String loc = jsonObject.getString("location");

                            for (String l : allRouteLocations)
                            {
                                if(loc.equalsIgnoreCase(l))
                                {
                                    double lat = jsonObject.getDouble("lat");
                                    double lon = jsonObject.getDouble("long");

                                    allRouteLatLong.add(new LatLng(lat, lon));

                                    break;
                                }
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    gotRoute = true;
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("error: ", error.getMessage());
            }
        });

        jsonObjectRequest.setPriority(Request.Priority.NORMAL);
        requestQueue.add(jsonObjectRequest);

        for (Double d : fromLatLong)
        {
            Log.i("d", String.valueOf(d));
        }

        lock.unlock();
    }


    public void GetMethodForCurrentDistance()
    {
        lock.lock();
        //RequestQueue requestQueue = Volley.newRequestQueue(context);
        CustomPriorityRequest jsonObjectRequest = new CustomPriorityRequest(Request.Method.GET, "https://rider-a-traffic-solution-default-rtdb.firebaseio.com/BusTable.json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                //try
                {
                    JSONArray array = response.names();

                    int busno = Integer.parseInt(whichbuSpinner.getSelectedItem().toString());

                    distanceList.clear();
                    currentLats.clear();
                    currentLongs.clear();
                    busId.clear();

                    for(int i=0;i<array.length();i++)
                    {
                        try
                        {
                            String key = array.getString(i);

                            int busnoFromJson = response.getJSONObject(key).getInt("busNo");

                            if(busno == busnoFromJson)
                            {
                                double lat = response.getJSONObject(key).getDouble("lat");
                                double longt = response.getJSONObject(key).getDouble("long");
                                Integer id = response.getJSONObject(key).getInt("busID");

//                                double lat = 23.7234;
//                                double longt = 90.4234;

                                double dist = getDistanceFromLatLonInKm(fromLatLong.get(0), fromLatLong.get(1), lat, longt);

                                distanceList.add(dist);
                                currentLats.add(lat);
                                currentLongs.add(longt);
                                busId.add(id);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    updateTime();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("error: " , error.getMessage());
            }
        });

        jsonObjectRequest.setPriority(Request.Priority.NORMAL);
        requestQueue.add(jsonObjectRequest);

        lock.unlock();
    }

    synchronized void GetAvailableSeats()
    {
        CustomPriorityRequest jsonObjectRequest = new CustomPriorityRequest(Request.Method.GET, "https://rider-a-traffic-solution-default-rtdb.firebaseio.com/BusTable.json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                //try
                {
                    JSONArray array = response.names();

                    int busno = Integer.parseInt(whichbuSpinner.getSelectedItem().toString());

                    distanceList.clear();
                    currentLats.clear();
                    currentLongs.clear();
                    busId.clear();

                    for(int i=0;i<array.length();i++)
                    {
                        try
                        {
                            String key = array.getString(i);

                            int busIdFromJson = response.getJSONObject(key).getInt("busID");

                            if(nearestBusId == busIdFromJson)
                            {
                                int seats = response.getJSONObject(key).getInt("availableSeats");

                                int min = Math.min(4, seats);

                                counts.clear();
                                for(int j=1;j<=min;j++)
                                {
                                    counts.add(String.valueOf(j));
                                }

                                break;

                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    updateSeatCounts();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("error: " , error.getMessage());
            }
        });

        jsonObjectRequest.setPriority(Request.Priority.NORMAL);
        requestQueue.add(jsonObjectRequest);
    }


    synchronized void addBusJourney()
    {
        try
        {
            key = String.valueOf(System.currentTimeMillis());

            String URL = "https://rider-a-traffic-solution-default-rtdb.firebaseio.com/BusJourney/" + key + ".json";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("userEmail", Info.currentEmail);
            jsonBody.put("busID", nearestBusId);



            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    flag = true;
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



    double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2)
    {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    double deg2rad(double deg)
    {
        return deg * (Math.PI/180);
    }


}