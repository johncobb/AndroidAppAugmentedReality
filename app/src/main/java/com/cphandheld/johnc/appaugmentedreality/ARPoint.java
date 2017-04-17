package com.cphandheld.johnc.appaugmentedreality;

import android.location.Location;

/**
 * Created by jcobb on 4/6/17.
 */

public class ARPoint extends ARDrawable {

    public double OFFSET = 0d;
    public double latitude = 0f;
    public double longitude = 0f;
    public String description;
    public float x = 0;
    public float y = 0;



    public ARPoint(double lat, double lon, String desc) {
        this.latitude = lat;
        this.longitude = lon;
        this.description = desc;
    }



    protected static double bearing(double lat1, double lon1, double lat2, double lon2) {
        double longDiff = Math.toRadians(lon2 - lon1);
        double la1 = Math.toRadians(lat1);
        double la2 = Math.toRadians(lat2);
        double y = Math.sin(longDiff) * Math.cos(la2);
        double x = Math.cos(la1) * Math.sin(la2) - Math.sin(la1) * Math.cos(la2) * Math.cos(longDiff);

        double result = Math.toDegrees(Math.atan2(y, x));
        return (result+360.0d)%360.0d;
    }

}
