package com.cphandheld.johnc.appaugmentedreality;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = true;

    private float mRotationMatrixTemp[];
    private float mRotationMatrix[];
    private float mInclinationMatrix[];

    private SensorManager mSensorManager;
    private List<Sensor> mSensors;

    private Sensor mGyro;
    private Sensor mMag;
    private float[] mGyroRaw;
    private float[] mMagRaw;

    private float mAzimuth = 0.0f;
    private float mPitch = 0.0f;
    private float mRoll = 0.0f;

    private LocationManager mLocationManager;
    private LocationProvider mHigh;
    private ARDrawSurfaceView mARDrawSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Setup the matrix variables
         */
        mRotationMatrixTemp = new float[9];
        mRotationMatrix = new float[9];
        mInclinationMatrix = new float[9];

        setContentView(R.layout.activity_main);
        mARDrawSurfaceView = (ARDrawSurfaceView) findViewById(R.id.drawSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DEBUG)
            Log.d(TAG, "onResume");

        /*
         * Get reference to LocationManager
         */
        mLocationManager = (LocationManager) MyApplication.getInstance().getSystemService(LOCATION_SERVICE);

        /*
         * Get reference to provider that will provide the best/(High) accuracy
         */
        mHigh = mLocationManager.getProvider(mLocationManager.getBestProvider(ARUtil.getFineCriteria(), true));

        /*
         * Get reference to SensorManager
         */
        mSensorManager = (SensorManager) MyApplication.getInstance().getSystemService(Context.SENSOR_SERVICE);

        /*
         * Get Gyroscopic Sensor
         */
        mSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (mSensors.size() > 0) mGyro = mSensors.get(0);

        /*
         * Get Magnetometer Sensor
         */
        mSensors = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (mSensors.size() > 0) mMag = mSensors.get(0);

        /*
         * Register the sensors with the event listener
         */
        mSensorManager.registerListener(mListener, mGyro, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(mListener, mMag, SensorManager.SENSOR_DELAY_GAME);

        initSensors();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (DEBUG)
            Log.d(TAG, "onPause");

        mSensorManager.unregisterListener(mListener, mGyro);
        mSensorManager.unregisterListener(mListener, mMag);
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

    /*
     *  Implement sensor rate independent low-pass filter
     */
    private static final float ALPHA = 0.25f;

    protected float[] lpf( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    /*
     * Implement sensor finite impulse filter
     */
    private final int N = 20;
    private int n = 0;
    private float x[] = new float[N];

    protected float fir(float input) {

        float y = 0.0f;

        // Store current value of input
        x[n] = input;

        // Multiply the filter coefficients by the previous
        // inputs and sum
        for (int i=0; i<N; i++) {
            y += x[n];
        }

        n = (n+ 1) % N;

        return y/N;
    }

    long lastInvalidate = 0;

    private final SensorEventListener mListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {

            // Determine which sensor reported
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGyroRaw = lpf(event.values.clone(), mGyroRaw);
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//                mMagRaw = lpf(event.values.clone(), mMagRaw);
                mMagRaw = event.values.clone();
            }

            // Make sure raw values from both sensors have reporeted at least once
            if ((mGyroRaw!=null) && (mMagRaw!=null)) {
                mSensorManager.getRotationMatrix(mRotationMatrixTemp, mInclinationMatrix, mGyroRaw, mMagRaw);

                // Get reference to WindowManager so we can determine devcice orientation
                int r =  ((WindowManager) MyApplication.getInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();

                // Remap coordinate system according to device orientation
                if (r == Surface.ROTATION_90) {
                    SensorManager.remapCoordinateSystem(mRotationMatrixTemp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, mRotationMatrix);
                } else {
                    SensorManager.remapCoordinateSystem(mRotationMatrixTemp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z, mRotationMatrix);
                }

                // Temp variable for calculating synthetic orientation based on rotatin matrix
                float output[] = new float[3];

                // Calculate synthetic orientation based on rotation matrix
                SensorManager.getOrientation(mRotationMatrix, output);

                // More fun math to determine our orientation. Radians are fun but
                // humans can process degrees better
                mAzimuth = (float)(((output[0]*180.0f)/Math.PI)+180.0f);
                mPitch = (float)(((output[1]*180.0f/Math.PI))+90.0f);
                mRoll = (float)(((output[2]*180.0f/Math.PI)));

                mAzimuth = fir(mAzimuth);

                // Duh
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
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    };

}
