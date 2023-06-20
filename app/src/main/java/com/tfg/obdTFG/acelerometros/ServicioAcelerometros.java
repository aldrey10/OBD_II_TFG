package com.tfg.obdTFG.acelerometros;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.tfg.obdTFG.db.DatoOBDHelper;

import java.util.Arrays;


public class ServicioAcelerometros extends Service implements SensorEventListener {
    private SensorManager myManager;
    private IBinder mBinder = new MyBinder();
    private Handler mHandler;
    private Boolean mIsPaused;
    private DatoOBDHelper database;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mIsPaused = true;
        myManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        database = new DatoOBDHelper(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyBinder extends Binder {
        ServicioAcelerometros getService(){
            return ServicioAcelerometros.this;
        }
    }

    public void startPretendLongRunningTask(){
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(mIsPaused){
                    mHandler.removeCallbacks(this);
                    pausePretendLongRunningTask();
                }else{
                    mHandler.postDelayed(this, 100);
                }
            }
        };
        mHandler.postDelayed(runnable, 100);
    }

    private void pausePretendLongRunningTask(){
        mIsPaused = true;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myManager.unregisterListener(this);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId){
        System.out.println("\n\nESATAMOS AQUISFA\n\n");
        myManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        int sensorRefresco = 2;
        Log.i("ServiceSensor", "activaSensoresPosicion: Sensor_Delay = " + sensorRefresco);
        SensorManager sensorManager = this.myManager;

        Sensor defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (defaultSensor != null) {
            sensorManager = this.myManager;
            if (sensorManager != null) {
                sensorManager.registerListener((SensorEventListener)this, defaultSensor, sensorRefresco, sensorRefresco);
            }

        }

        Log.i("ServiceSensor", "Sensor activado: Sensor_Delay = " + sensorRefresco);
        return Service.START_STICKY;
    }


    @Override
    public void onSensorChanged(SensorEvent ev) {
        if (ev != null) {
            if (ev.sensor != null && ev.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float[] a = ev.values;
                float aceleracionBache = a[2];
                aceleracionBache = Math.abs(aceleracionBache - (float)9.8);
                if(aceleracionBache < 0.6){
                    database.guardarGrupoAceleracion(0);
                } else if(aceleracionBache < 1.1){
                    database.guardarGrupoAceleracion(1);
                } else if(aceleracionBache < 1.6){
                    database.guardarGrupoAceleracion(2);
                } else if (aceleracionBache < 2.1){
                    database.guardarGrupoAceleracion(3);
                } else if (aceleracionBache < 2.6){
                    database.guardarGrupoAceleracion(4);
                } else if (aceleracionBache < 3.1){
                    database.guardarGrupoAceleracion(5);
                } else{
                    database.guardarGrupoAceleracion(6);
                }
                return;
            }
        }

        Log.i("ServiceSensor", "Ninguna aceleración, pero activo");
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
