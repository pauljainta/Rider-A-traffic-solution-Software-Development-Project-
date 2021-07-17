package com.example.rider_atrafficsolution;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TripDetailsActivity extends AppCompatActivity {

    TextView timestampTextview,tripFaretextView,detailsTextview;

    String source,dest,driverName,passengerName,type;

    double userRating,driverRating;
    String startTime,finishTime;

    double fare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

       timestampTextview=findViewById(R.id.tripdetailsTimeStampTextView);
        tripFaretextView=findViewById(R.id.tripFareTextView);
        detailsTextview=findViewById(R.id.detailstextview);

        Intent intent=getIntent();
        source=intent.getStringExtra("source");
        dest=intent.getStringExtra("dest");
        driverName=intent.getStringExtra("driverName");
        passengerName=intent.getStringExtra("passengerName");
        startTime=intent.getStringExtra("startTime");
        finishTime=intent.getStringExtra("finishTime");
        type=intent.getStringExtra("type");

        fare=intent.getDoubleExtra("fare",0);

        userRating=intent.getDoubleExtra("userRating",0);
        driverRating=intent.getDoubleExtra("driverRating",0);

       timestampTextview.setText(startTime+"\n"+finishTime);

        tripFaretextView.setText("BDT:"+fare);

        String details="Journey From "+source+" to "+dest+" by "+type+"\n"+
                "Driver "+driverName+" rated you "+userRating+"/5"+"\n"
                +"You rated him "+driverRating+"/5"+"\n";
        detailsTextview.setText(details);








    }
}