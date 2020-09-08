package com.example.step_music_player.playing_service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class shakeshake_component extends AppCompatActivity implements SensorEventListener {
    private Context context;
    private SensorManager sm;
    private Sensor sensor;
    private final static int shake_threshold=20;
    private long last_update;
    private boolean hasCNT,vibrate_ok,screen_off;
    private Handler shake_handler;
    private Vibrator vibrator;

    public shakeshake_component(Context context){ this.context=context; }

    public boolean init_sensor(Handler shake_handler){
        sm=(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        hasCNT=context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        if(hasCNT){
            sm.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST);
            this.shake_handler=shake_handler;
            last_update=0;
            vibrator= (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrate_ok=true;
            screen_off=false;
            return true;
        }
        else {
            Looper.prepare();
            Toast mytoast=Toast.makeText(context,"您的手机不支持振动检测",Toast.LENGTH_SHORT);
            mytoast.setGravity(Gravity.BOTTOM,0,200);
            mytoast.show();
            Looper.loop();
            return false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Message msg;
        float[] accelator_record=event.values;
        long current_time=System.currentTimeMillis();
        if(!screen_off) {
            if (current_time - last_update > 150) {
                last_update = current_time;
                Log.i("ACCELERATOR_INFO", "x: " + accelator_record[0]);
                Log.i("ACCELERATOR_INFO", "y: " + accelator_record[1]);
                Log.i("ACCELERATOR_INFO", "z: " + accelator_record[2]);
                if (accelator_record[0] > shake_threshold || accelator_record[1] > shake_threshold || accelator_record[2] > shake_threshold) {
                    Log.i("ACCELERATOR_INFO", "Shake detected");
                    if (vibrate_ok) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                            vibrator.vibrate(200);
                        }
                    }
                    msg = new Message();
                    msg.what = 0;
                    shake_handler.sendMessage(msg);
                }
            }
        }
    }

    public void unregister_sensor(){ if(hasCNT){ sm.unregisterListener(this); } }

    public void setVibrate_ok(boolean shakeok){ vibrate_ok=shakeok; }
    public void setScreen_off(boolean screen_off){ this.screen_off=screen_off; }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
