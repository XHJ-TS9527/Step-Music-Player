package com.example.step_music_player.step_sensor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class step_sensor_component extends Activity implements SensorEventListener{
    private SensorManager sm;
    private Sensor stepCNT;
    private Context context;
    private boolean hasCNT,pause_flag,changed_flag;
    private int step_number,last_record_value;

    public step_sensor_component(Context context){
        this.context=context;
        step_number=0;
        last_record_value=-1;
    }

    @SuppressLint("InvalidWakeLockTag")
    public boolean init_sensor(){
        sm=(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        changed_flag=false;
        stepCNT=sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        hasCNT=context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
        if (hasCNT){
            Log.i("Sensor_info","I registered this sensor.");
            sm.registerListener(this,stepCNT,SensorManager.SENSOR_DELAY_FASTEST);
            pause_flag=false;
        }
        else{
            Looper.prepare();
            Toast mytoast=Toast.makeText(context,"您的手机不支持步数检测",Toast.LENGTH_SHORT);
            mytoast.setGravity(Gravity.BOTTOM,0,200);
            mytoast.show();
            Looper.loop();
            pause_flag=true;
        }
        return !pause_flag;
    }

    public void unregister_sensor(){
        if(hasCNT&&!pause_flag){
            sm.unregisterListener(this);
        }
    }

    public int getStep_number(){
        int result=0;
        if (hasCNT){ if(last_record_value>=0) { result=step_number-last_record_value; } }
        if(result<0){
            result=0;
            last_record_value=0;
        }
        else{ last_record_value=step_number; }
        return result;
    }

    public void pause_sensor(){
        sm.unregisterListener(this);
        pause_flag=true;
    }

    public void resume_sensor(){
        if (hasCNT) {
            sm.registerListener(this, stepCNT, SensorManager.SENSOR_DELAY_FASTEST);
            pause_flag = false;
        }
    }

    public int get_step_number(){ return step_number; }
    public boolean get_changed_flag(){ return changed_flag; }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i("SENSOR_INFO","The sensor data is "+event.values[0]+" and sensor changed.");
        step_number=(int)event.values[0];
        changed_flag=true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void reregister_sensor(){
        if(hasCNT) {
            try {
                sm.unregisterListener(this); }
            catch (Exception e) { }
            sm.registerListener(this,stepCNT,SensorManager.SENSOR_DELAY_FASTEST);
            Log.i("SENSOR_info","Reregistered the sensor");
        }
    }
}
