package com.example.googleembededstreetview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.vr.sdk.base.GvrActivity;

class VR_View extends GvrActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr__view);
    }

}
