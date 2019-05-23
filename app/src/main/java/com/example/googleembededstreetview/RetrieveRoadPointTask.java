package com.example.googleembededstreetview;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RetrieveRoadPointTask  extends AsyncTask<LatLng, Void, String> {

    private Exception exception;

    public AsyncResponse delegate = null;

    public RetrieveRoadPointTask(AsyncResponse delegate){
        this.delegate = delegate;
    }
    @Override
    protected String doInBackground(LatLng... Positions) {

        String URLName = "https://roads.googleapis.com/v1/snapToRoads?&key=AIzaSyCFyFd-LGtWjzOXCIL2GU4rEkwDZR6GQeA&path=";
        URLName += ParseLatLong(Positions);
        URL url = null;
        try {
            url = new URL(URLName);
        } catch (MalformedURLException e) {
            exception =e;
            return "";

        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            exception =e;
            return "";
        }

        String total = "";
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            total = new String(ByteStreams.toByteArray(in),"UTF-8");
        }
        catch (Exception e)
        {
            this.exception = e;

            return null;
//
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//
//            String total = new String(ByteStreams.toByteArray(in ));
//            System.out.println(total);
        }
        finally {

            urlConnection.disconnect();
            return total;
        }
    }

    String ParseLatLong(LatLng[] positions){

        String ParsedLatLong = "";
        for(LatLng position : positions){
            ParsedLatLong += position.latitude +","+position.longitude;
            if(position != positions[positions.length-1])
            {
                ParsedLatLong+="|";
            }
        }
        return ParsedLatLong;
    }
    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
