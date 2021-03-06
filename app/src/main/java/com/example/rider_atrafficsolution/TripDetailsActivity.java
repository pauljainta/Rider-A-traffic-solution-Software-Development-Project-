package com.example.rider_atrafficsolution;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TripDetailsActivity extends AppCompatActivity {

    TextView sourceDestTextView,tripFaretextView,detailsTextview, typeTextView;

    String source,dest,driverName,passengerName,type;

    double userRating,driverRating;
    String startTime,finishTime;

    double fare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        sourceDestTextView = findViewById(R.id.sourceDestTextView);
        tripFaretextView = findViewById(R.id.tripFareTextView);
        detailsTextview = findViewById(R.id.detailstextview);
        typeTextView = findViewById(R.id.typeTextView);

        Intent intent=getIntent();
        source=intent.getStringExtra("source");
        dest=intent.getStringExtra("dest");
        driverName=intent.getStringExtra("driverName");
        passengerName=intent.getStringExtra("passengerName");
        startTime=intent.getStringExtra("startTime");
        finishTime=intent.getStringExtra("finishTime");
        System.out.println("finish time in history " + finishTime);

        type=intent.getStringExtra("type");

        fare=intent.getDoubleExtra("fare",0);

        userRating=intent.getDoubleExtra("userRating",0);
        driverRating=intent.getDoubleExtra("driverRating",0);

//        timestampTextview.setText("Start Time: " + startTime + "\n\nFinish Time: " + finishTime);
        sourceDestTextView.setText(source + "\nTO\n" + dest);

        tripFaretextView.setText("BDT "+fare);

        typeTextView.setText(type.toUpperCase());

        String details = "Start: " + startTime +"\nFinish: " + finishTime +
                "\n\nDriver "+driverName+"\nYou were rated "+userRating+"/5"+"\n"
                +"You rated him "+driverRating+"/5"+"\n";
        detailsTextview.setText(details);

        //zulfiqar9726@gmail.com
        //123456







    }
}