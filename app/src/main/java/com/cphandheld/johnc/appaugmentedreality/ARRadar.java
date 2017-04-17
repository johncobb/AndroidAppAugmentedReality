package com.cphandheld.johnc.appaugmentedreality;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.util.DisplayMetrics;

import java.util.ArrayList;

/**
 * Created by jcobb on 4/11/17.
 */



public class ARRadar {


    private double OFFSET = 0d;

    private Canvas mCanvas;
    private Paint mPaint;

    private float mRange;

    private static int FONT_SIZE = 16;
    private static boolean ANTIALIAS = true;
    private static float RADIUS = 125;
    static float mOriginX = 0.0f;
    static float mOriginY = 0.0f;

    static int mRadarColor = Color.argb(80, 255, 255, 255);
    static int mRadarBorderColor = Color.argb(255, 255, 255, 255);
    static int mRadarCenterColor = Color.argb(255, 255, 255, 255);
    static int mRadarTextColor = Color.argb(255, 255, 255, 255);


    ARPoint mARRadarReference = new ARPoint(0.0d, -0.0d, "RadarReferencePoint");
//    ARBlip mARBlips[];


    public static ArrayList<ARBlip> mARScanResult = new ArrayList<ARBlip>();

    static int mBogeyColor = Color.argb(225, 0, 180, 0);
    static int mBlipColor = Color.argb(100, 220, 0, 0);

    Location mLocationCurrent = new Location("provider");
    Location mLocationDestined = new Location("provider");

    public ARRadar() {

        mPaint = new Paint();
        setFontSize(FONT_SIZE);
        setAntiAlias(ANTIALIAS);
        setColor(mRadarColor);
        setFill(true);

//        mARBlips[0] = new ARBlip(0.0f, 0.0f, "", mCanvas);
        scan();
    }



    public void setRadarReference(ARPoint p) {
        mARRadarReference = p;
    }

    /*
 * Tangent: Opposite/Adgacent
 * Source: https://www.mathsisfun.com/definitions/tangent-function-.html
 *
 */
    protected double distInMetres(ARPoint me, ARPoint u) {

        double lat1 = me.latitude;
        double lng1 = me.longitude;

        double lat2 = u.latitude;
        double lng2 = u.longitude;

        double earthRadius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist * 1000;
    }

    /*
     *  Scan for new targets to paint on radar
     *  For now we just hardcode a few points
     */
    void scan() {


        mARScanResult.add(new ARBlip(37.954814755510235, -87.36339390277863, "JohnC", mCanvas));
        mARScanResult.add(new ARBlip(37.962338d, -87.375918d, "Rudy", mCanvas));
        mARScanResult.add(new ARBlip(38.006867d, -87.387528d, "Nigel", mCanvas));
        mARScanResult.add(new ARBlip(37.96956158065237d, -87.47125223279d, "G", mCanvas));

    }

    /*
     *  Paint targets found during scan. This includes updating location
     *  of existing targets found on previous scans.
     */
    void sweep() {


        for (ARBlip blip: mARScanResult) {

            // Make sure we're paint from the radar center point (origin)
            blip.setOrigin(mOriginX, mOriginY);

            double meters = distInMetres(mARRadarReference, blip);

            double angle = bearing(mARRadarReference.latitude, mARRadarReference.longitude, blip.latitude, blip.longitude) - OFFSET;
            double xPos, yPos;

            // Sanity check
            if(angle < 0)
                angle = (angle+360)%360;

            // Math is fun
            xPos = Math.sin(Math.toRadians(angle)) * meters;
            yPos = Math.sqrt(Math.pow(meters, 2) - Math.pow(xPos, 2));

            // Sanity check
            if (angle > 90 && angle < 270)
                yPos *= -1;


            DisplayMetrics m = ARDpiUtil.getDisplayMetrics(MyApplication.getInstance().getApplicationContext());

//            double posInPx = angle * (mScreenWidth / 90d);
            double posInPx = angle * (m.widthPixels / 90d);



//            int blipCentreX = blip.getWidth() / 2;
//            int blipCentreY = blip.getHeight() / 2;
//
//            xPos = xPos - blipCentreX;
//            yPos = yPos + blipCentreY;
//            canvas.drawBitmap(blip, (radarCenterX + (int) xPos), (radarCenterY - (int) yPos), mPaint); //radar blip
            blip.paint();


        }
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

    public void setCanvas(Canvas canvas) {
        mCanvas = canvas;
    }

    public void paint() {
        // Paint the Radar

        mOriginX = RADIUS * 2;
        mOriginY = RADIUS * 2;

        // Draw Radar Fill
        setFill(true);
        setColor(mRadarColor);
        mCanvas.drawCircle(mOriginX, mOriginY, RADIUS-5, mPaint);

        // Draw Radar Border
        setFill(false);
        setColor(mRadarBorderColor);
        setStrokeWidth(5.0f);
        mCanvas.drawCircle(mOriginX, mOriginY, RADIUS, mPaint);

        // Draw Radar Center
        setFill(true);
        setColor(mRadarCenterColor);
        mCanvas.drawCircle(mOriginX, mOriginY, 5, mPaint);

        // Draw Bogey
//        setFill(true);
//        setColor(mBogeyColor);
//        mCanvas.drawCircle(mOriginX+40, mOriginY+25, 5, mPaint);

//        mARBlips[0].paint();

        sweep();

        setColor(mRadarTextColor);
        setFontSize(30.0f);

        float tw = getTextWidth("N");
        mCanvas.drawText("N", mOriginX - (tw/2), mOriginY-RADIUS-10, mPaint);

    }






    public void setFill(boolean fill) {
        if (fill)
            mPaint.setStyle(Paint.Style.FILL);
        else
            mPaint.setStyle(Paint.Style.STROKE);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void setStrokeWidth(float width) {
        mPaint.setStrokeWidth(width);
    }

    public void setFontSize(float size) {
        mPaint.setTextSize(size);
    }

    public void setFontStyle(int typeface){
        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, typeface));
    }

    public void setAntiAlias(boolean antialias){
        mPaint.setAntiAlias(antialias);
    }

    public float getTextWidth(String txt) {
        return mPaint.measureText(txt);
    }








}
