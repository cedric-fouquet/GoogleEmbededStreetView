package com.example.googleembededstreetview;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public class StreetView extends FragmentActivity
        implements OnStreetViewPanoramaReadyCallback  {

    StreetViewPanoramaFragment streetViewPanoramaFragment;
    LatLng Position;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        Intent intent = getIntent();
        Position = intent.getExtras().getParcelable("LatLng");


        streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(Position);
        try {
            //Change angle of street view
            final int DURATION = 1000;
            StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
                    .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                    .tilt(streetViewPanorama.getPanoramaCamera().tilt)
                    .bearing(streetViewPanorama.getPanoramaCamera().bearing ) // angle value by Maps Api
                    .build();
            streetViewPanorama.animateTo(camera, DURATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

