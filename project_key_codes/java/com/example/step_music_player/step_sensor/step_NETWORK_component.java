package com.example.step_music_player.step_sensor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.Objects;

public class step_NETWORK_component extends Activity {
    private Context context;
    private LocationManager lm;
    private Criteria criteria;
    private float speed;
    private Location location;
    private final double PI = 3.14159265358979323;
    private final double R = 6371229;

    public step_NETWORK_component(Context context){ this.context=context; }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean init_NETWORK(){
        try {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            else {
                if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    location = lm.getLastKnownLocation(Objects.requireNonNull(lm.getBestProvider(getCriteria(), true)));
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0.5f, location_lisenter);
                    return true;
                }
                else { return false; }
            }
        }
        catch (Exception e){
            Log.i("NETWORKinfo","Init exception"+e);
            return false;
        }
    }

    private Criteria getCriteria(){
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    private LocationListener location_lisenter=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updatespeedbylocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { speed=0.0f; }

        @Override
        public void onProviderDisabled(String provider) { speed=-1.0f; }
    };

    public float getSpeed(){
        return speed;
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public float[] get_location(){
        if(location==null){
            location = lm.getLastKnownLocation(Objects.requireNonNull(lm.getBestProvider(getCriteria(), true)));
            return new float[] {0.0f,0.0f};
        }
        return new float[] {(float) location.getLongitude(), (float) location.getLatitude()};
    }
    public boolean get_enable(){ return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER); }

    private void updatespeedbylocation(Location location){
        double longnitude_old,longnitude_new,latitude_old,latitude_new;
        try {
            longnitude_old = this.location.getLongitude();
            latitude_old = this.location.getLatitude();
        }
        catch (Exception e){
            this.location=location;
            return;
        }
        longnitude_new=location.getLongitude();
        latitude_new=location.getLatitude();
        this.location=location;
        Log.i("NETWORK_info","Old latitude: "+latitude_old);
        Log.i("NETWORK_info","Old longtitude: "+longnitude_old);
        Log.i("Network_info","New latitude: "+latitude_new);
        Log.i("Network_info","New longtitude: "+longnitude_new);
        double dist=getDistance(longnitude_new,latitude_new,longnitude_old,latitude_old);
        Log.i("Network_info","network distance: "+dist);
        speed= (float) (dist*0.01);
    }

    public double getDistance(double longt1, double lat1, double longt2, double lat2){
        double x,y, distance;
        x=(longt2-longt1)*PI*R*Math.cos( ((lat1+lat2)/2)*PI/180)/180;
        y=(lat2-lat1)*PI*R/180;
        distance=Math.hypot(x,y);
        return distance;
    }
}
