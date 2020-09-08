package com.example.step_music_player.step_sensor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.Objects;

public class step_GPS_component extends Service {
    private Context context;
    private LocationManager lm;
    private Criteria criteria;
    private float speed,longtitude,latitude;
    private GPS_status_listener gps_status_listener;
    private boolean init_flag;
    private Location location;

    public step_GPS_component(Context context) {
        this.context = context;
        init_flag = false;
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            Log.i("GPS_info","GPS open error "+e);
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean init_GPS() {
        try {
            Log.i("GPS_info","Running GPS initialization.");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            else {
                if(!init_flag){
                    gps_status_listener = new GPS_status_listener();
                    lm.addGpsStatusListener(gps_status_listener);
                    init_flag = true;
                }
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    location = lm.getLastKnownLocation(Objects.requireNonNull(lm.getBestProvider(getCriteria(), true)));
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0.5f, location_lisenter);
                    if(location!=null) {
                        Log.i("GPS_info","Initialization location not null.");
                        longtitude= (float) location.getLongitude();
                        latitude= (float) location.getLatitude();
                    }
                    else{
                        Log.i("GPS_info","Initialization location is null. ");
                        location=lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        if(location==null) {
                            Log.i("GPS_info","Initialization location is null. 2222");
                            lm.removeGpsStatusListener(gps_status_listener);
                            lm=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                            gps_status_listener = new GPS_status_listener();
                            lm.addGpsStatusListener(gps_status_listener);
                            location = lm.getLastKnownLocation(Objects.requireNonNull(lm.getBestProvider(getCriteria(), true)));
                            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0.5f, location_lisenter);
                            if(location!=null) {
                                longtitude = (float) location.getLongitude();
                                latitude = (float) location.getLatitude();
                            }
                            else{
                                Log.i("GPS_info","Initialization location is null. 3333");
                                latitude=0.0f;
                                longtitude=0.0f;
                            }
                        }
                        else{
                            longtitude= (float) location.getLongitude();
                            latitude= (float) location.getLatitude();
                        }
                    }
                    Log.i("GPSinfo", "GPS location running");
                    return true;
                }
                else { return false; }
            }
        }
        catch (Exception e){
            Log.i("GPSinfo","Init exception"+e);
            return false;
        }
    }

    public void destroy_GPS(){
        if(lm!=null){ lm.removeGpsStatusListener(gps_status_listener); }
    }

    public float[] get_location() {
        Log.i("GPS_info","Long: "+longtitude+" Lat: "+latitude);
        try { return new float[]{longtitude, latitude}; }
        catch (Exception e) { return new float[] {0.0f,0.0f}; }
    }

    public boolean get_enable(){ return lm.isProviderEnabled(LocationManager.GPS_PROVIDER); }

    private void updatespeedbylocation(Location location){
        Log.i("GPS_info","Long: "+longtitude+" Lat: "+latitude+"testing.");
        longtitude= (float) location.getLongitude();
        latitude= (float) location.getLatitude();
        speed=location.getSpeed();
    }

    public float getSpeed(){ return speed; }

    private Criteria getCriteria(){
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    private class GPS_status_listener implements GpsStatus.Listener{
        @SuppressLint("MissingPermission")
        @Override
        public void onGpsStatusChanged(int event) {
            try{
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0.5f, location_lisenter);
                updatespeedbylocation(location);
            }
            catch (Exception e){ }
        }
    }


    private LocationListener location_lisenter=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("GPS_info","Run GPS Location changed.");
            updatespeedbylocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { speed=0.0f; }

        @Override
        public void onProviderDisabled(String provider) { speed=-1.0f; }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
