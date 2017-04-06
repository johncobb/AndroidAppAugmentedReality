package com.cphandheld.johnc.appaugmentedreality;

/**
 * Created by jcobb on 4/6/17.
 */

public class ARPoint {

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

}
