package com.cphandheld.johnc.appaugmentedreality;

import android.graphics.Color;
import android.location.Location;

/**
 * Created by jcobb on 4/11/17.
 */

public class ARRadar {

    private float mRange;

    private static float RADIUS = 40;
    static float mOriginX = 0;
    static float mOriginY = 0;

    static int mRadarColor = Color.argb(100, 220, 0, 0);

    Location mLocationCurrent = new Location("provider");
    Location mLocationDestined = new Location("provider");



}
