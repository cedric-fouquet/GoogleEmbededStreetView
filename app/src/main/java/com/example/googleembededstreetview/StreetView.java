package com.example.googleembededstreetview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StreetView extends FragmentActivity
        implements OnStreetViewPanoramaReadyCallback  {

    StreetViewPanoramaFragment streetViewPanoramaFragment;
    ArrayList<LatLng> pointList;
    Timer timer;
    int ViewIndex = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        timer = new Timer();


        super.onCreate(savedInstanceState);
        pointList = new ArrayList<LatLng>();
        setContentView(R.layout.activity_street_view);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras().getParcelable("bundle");
        Parcelable parcelable = bundle.getParcelable("PointList");
        ArrayList<ParcelableLatlng> parcelableLatlngArrayList = Parcels.unwrap(parcelable);

        for(ParcelableLatlng parcelableLatlng : parcelableLatlngArrayList)
        {
            pointList.add(parcelableLatlng.getLatLng());
        }

        streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

    }

@Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        setNewCameraPosition(streetViewPanorama);
        streetViewPanorama.setStreetNamesEnabled(false);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(ViewIndex < pointList.size()-1) {
                    ViewIndex += 1;
                    setNewCameraPosition(streetViewPanorama);
                }
            }
        },0,3333);
    }
    
    public void setNewCameraPosition(StreetViewPanorama streetViewPanorama) {
        runOnUiThread(() -> {
            streetViewPanorama.setPosition(pointList.get(ViewIndex));
            //Change angle of street view
            final int DURATION = 10000;
            StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
                    .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                    .tilt(streetViewPanorama.getPanoramaCamera().tilt)
                    .bearing(streetViewPanorama.getPanoramaCamera().bearing) // angle value by Maps Api
                    .build();
            streetViewPanorama.animateTo(camera, DURATION);
        });
    }

}

