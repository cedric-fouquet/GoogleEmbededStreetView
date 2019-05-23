package com.example.googleembededstreetview;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GaeRequestHandler;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.RoadsApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.GeocodingResult;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker markerOrigin;
    private Marker markerDestination;
    private boolean PutMarkerOrigin = true;
    private  Gson gson = new Gson();
    private GeoApiContext geoApiContext;
    private Polyline road;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        geoApiContext = new GeoApiContext.Builder().apiKey("AIzaSyCFyFd-LGtWjzOXCIL2GU4rEkwDZR6GQeA").build();


        LatLng markerPos = new LatLng(-34, 151);
        final MarkerOptions markerOptions = new MarkerOptions().position(markerPos);
        markerOrigin = mMap.addMarker(markerOptions);
        markerDestination = mMap.addMarker(markerOptions);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                try {
                    RetrieveRoadPointTask asyncTask = (RetrieveRoadPointTask) new RetrieveRoadPointTask(new AsyncResponse(){

                        RoadsSnappedPoint roadsSnappedPoint = new RoadsSnappedPoint();
                        @Override
                        public void processFinish(String output){

                            roadsSnappedPoint = gson.fromJson(output,RoadsSnappedPoint.class);
                            if (roadsSnappedPoint.snappedPoints != null) {
                                SnappedPoint Point = roadsSnappedPoint.snappedPoints[0];
                                try{
                                    if(PutMarkerOrigin)
                                    {
                                        markerOrigin.remove();
                                       markerOrigin =  mMap.addMarker(new MarkerOptions().position(Point.getLatLng()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                                       PutMarkerOrigin = !PutMarkerOrigin;
                                    }
                                    else{
                                        markerDestination.remove();
                                        markerDestination =  mMap.addMarker(new MarkerOptions().position(Point.getLatLng()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                        PutMarkerOrigin = !PutMarkerOrigin;
                                        try {
                                            if( road != null)
                                            {
                                                road.remove();
                                            }
                                            road = drawDirection(markerOrigin.getPosition(),markerDestination.getPosition());
                                        } catch (ApiException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                }
                                catch (Exception e)
                                {

                                }
                            }
                        }


                    }).execute(latLng);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        FloatingActionButton startStreetView = (FloatingActionButton)   findViewById(R.id.floatingActionButton);
        startStreetView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StreetView.class);
                ArrayList<ParcelableLatlng> parcelableList = new ArrayList<ParcelableLatlng>();
                for(LatLng point : road.getPoints())
                {
                    parcelableList.add(new ParcelableLatlng(point.latitude,point.longitude));
                }

                Parcelable listParcelable = Parcels.wrap(parcelableList);
                Bundle bundle = new Bundle();
                bundle.putParcelable("PointList",listParcelable);
                intent.putExtra("bundle",bundle);
                startActivity(intent);



            }
        });


    }


    Polyline drawDirection(LatLng origin, LatLng destination) throws ApiException, InterruptedException, IOException {

        DirectionsApiRequest directionApi = new DirectionsApiRequest(geoApiContext);


        directionApi.origin(new com.google.maps.model.LatLng(origin.latitude,origin.longitude) );
        directionApi.destination(new com.google.maps.model.LatLng(destination.latitude,destination.longitude));

        DirectionsResult results= new DirectionsResult();
        try {
            results = directionApi.await();
        } catch (ApiException e) {
            throw e;
        } catch (InterruptedException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        List<com.google.maps.model.LatLng> pointList =  results.routes[0].overviewPolyline.decodePath();
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int z = 0; z < pointList.size(); z++) {
            com.google.maps.model.LatLng point = pointList.get(z);
            options.add(new LatLng( point.lat,point.lng));
        }
        return mMap.addPolyline(options);
    }

}
