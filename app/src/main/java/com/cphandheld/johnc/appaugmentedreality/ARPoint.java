package com.cphandheld.johnc.appaugmentedreality;

/**
 * Created by jcobb on 4/6/17.
 */

public class ARPoint extends ARDrawable {

    public float markerX = 0.0f;
    public float markerY = 0.0f;
    public double latitude = 0d;
    public double longitude = 0d;
    public String description;


    public ARPoint(double lat, double lon, String desc) {
        this.latitude = lat;
        this.longitude = lon;
        this.description = desc;
    }


}
