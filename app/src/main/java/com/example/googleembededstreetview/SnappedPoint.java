package com.example.googleembededstreetview;

import com.google.android.gms.maps.model.LatLng;

public class SnappedPoint{
    class Location{
        float latitude;
        float longitude;
    }
    Location location;
    int originalIndex;
    String placedId;
    public LatLng getLatLng() throws Exception {
        if(this.location.latitude == 0.0 || this.location.longitude ==0.0)
        {
            throw new Exception("Location Undefined");
        }
        return new LatLng(this.location.latitude,this.location.longitude);
    }
}
