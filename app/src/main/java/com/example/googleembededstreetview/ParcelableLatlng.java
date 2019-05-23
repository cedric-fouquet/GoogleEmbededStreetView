package com.example.googleembededstreetview;

import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@org.parceler.Parcel(Parcel.Serialization.BEAN)
public class ParcelableLatlng {
    private double longitude;
    private double latitude;

    @ParcelConstructor
    public ParcelableLatlng(double latitude,double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }



    public double getlatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }
    public LatLng getLatLng(){
        return new LatLng(this.latitude,this.longitude);
    }


}
