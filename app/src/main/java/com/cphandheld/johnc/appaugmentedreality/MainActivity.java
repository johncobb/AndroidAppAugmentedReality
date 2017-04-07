package com.cphandheld.johnc.appaugmentedreality;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.security.Timestamp;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = false;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private LocationManager mLocationManager;
    private LocationProvider mHigh;

    private ARDrawSurfaceView mARDrawSurfaceView;

    private float mAzimuth = 0.0f;
    private float mPitch = 0.0f;
    private float mRoll = 0.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager = (SensorManager) MyApplication.getInstance().getSystemService(Context.SENSOR_SERVICE);

        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        setContentView(R.layout.activity_main);

        mARDrawSurfaceView = (ARDrawSurfaceView) findViewById(R.id.drawSurfaceView);

//        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mLocationManager = (LocationManager) MyApplication.getInstance().getSystemService(LOCATION_SERVICE);

        mHigh = mLocationManager.getProvider(mLocationManager.getBestProvider(ARUtil.getFineCriteria(), true));

        initSensors();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DEBUG)
            Log.d(TAG, "onResume");

        mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (DEBUG)
            Log.d(TAG, "onPause");

        mSensorManager.unregisterListener(mListener);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (DEBUG)
            Log.d(TAG, "onStop");

        mSensorManager.unregisterListener(mListener);
        super.onStop();
    }



    private void initSensors() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(mHigh.getName(), 0, 0f, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mARDrawSurfaceView.setMyLocation(location.getLatitude(), location.getLongitude());
                mARDrawSurfaceView.invalidate();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {


            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

    }


    private static final float ALPHA = 0.25f;
    protected float[] sensors;

    /*
     *
     *  Implement sensor rate independent low-pass filter
     *  filter = old + (new-old)/(smoothing/timeSinceLastUpdate)
     *
     */
    protected float[] lpf( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    long lastInvalidate = 0;

    private final SensorEventListener mListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {

//            mAzimuth = event.values[0];
//            mPitch = event.values[1];
//            mRoll = event.values[2];

            // Apply low pass filter to sensors
            sensors = lpf(event.values.clone(), sensors);

            // Update the variables with filtered values
            mAzimuth = sensors[0];
            mPitch = sensors[1];
            mRoll = sensors[2];

            if(DEBUG)
                Log.d(TAG, "sensorChanged Azimuth, Pitch, Roll (" + mAzimuth + ", " + mPitch + ", " + mRoll + ")");


            if (mARDrawSurfaceView != null) {
                mARDrawSurfaceView.setOffset(mAzimuth);
                mARDrawSurfaceView.setMyOrientation(mAzimuth, mPitch, mRoll);

                // Only update every 50 millis so that we don't overload
                // the draw routine
                if((System.currentTimeMillis() - lastInvalidate) > 50) {
                    mARDrawSurfaceView.invalidate();
                    lastInvalidate = System.currentTimeMillis();
                }

            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

}
