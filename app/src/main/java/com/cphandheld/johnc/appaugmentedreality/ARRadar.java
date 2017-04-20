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
    private static double RADIUS_EARTH = 6371d;

    private Canvas mCanvas;
    private Paint mPaint;

    private float mRange;

    private static int FONT_SIZE = 16;
    private static boolean ANTIALIAS = true;
    private static float RADIUS = 125;
//    private static float MAX_RADAR_RADIUS = 16093.4f; // 10 miles
private static float MAX_RADAR_RADIUS = 8000.0f; // 10 miles

    static float mOriginX = 0.0f;
    static float mOriginY = 0.0f;
    float mRoll = 0.0f;
    float mPitch = 0.0f;
    float mAzimuth = 0.0f;

    static int mRadarColor = Color.argb(80, 255, 255, 255);
    static int mRadarBorderColor = Color.argb(255, 255, 255, 255);
    static int mRadarCenterColor = Color.argb(255, 255, 255, 255);
    static int mRadarTextColor = Color.argb(255, 255, 255, 255);


    ARPoint mARRadarReference = new ARPoint(37.97280602299139d, -87.40445584058762d, "RadarReferencePoint");


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

        sweep();
    }

    public void setRadarReference(ARPoint p) {
        mARRadarReference = p;
    }

    public void setRadarOrientation(float roll, float pitch, float azimuth) {
        mRoll = roll;
        mPitch = pitch;
        mAzimuth = azimuth;
    }



    /*
     *  Scan for new targets to paint on radar
     *  For now we just hardcode a few points
     */
    void sweep() {
        mARScanResult.add(new ARBlip(37.94393890692642d, -87.40509152412415d, "Downtown Newburgh" ));
//        mARScanResult.add(new ARBlip(38.044846621406975d, -87.52309799194336d, "EVV Airport"));
//        mARScanResult.add(new ARBlip(37.930929663198796d, -87.32285499572754d, "Alcoa"));
//        mARScanResult.add(new ARBlip(37.962335361193944d, -87.67450332641602d, "USI"));
    }

    /*
     *  Paint targets found during scan. This includes updating location
     *  of existing targets found on previous scans.
     */
    void paintTargets() {

        for (ARBlip blip: mARScanResult) {

            // Make sure we're paint from the radar center point (origin)
            blip.setOrigin(mOriginX, mOriginY);

            double meters = distInMetres(mARRadarReference, blip);

            /*
             * Only paint targets within radar's radius
             */
            if (meters > MAX_RADAR_RADIUS)
                continue;

            /*
             * Scale distance down to radar units
             */
            meters = meters/MAX_RADAR_RADIUS;
            meters = meters * RADIUS;

            /*
             * Calculate the angle based upon 0.00 degree reference point
             * We'll add to the origin/phone compass bearing.
             */
//            double angle = bearing(mARRadarReference.latitude, mARRadarReference.longitude, blip.latitude, blip.longitude) - OFFSET;

            /*
             * Calculate theoretical azimuth in radians
             */
            double deltaAzimuth = 0d;
            double realAzimuth = mAzimuth;
            double calculatedAzimuth = 0d;
            double theoreticalAzimuth = calculateTheoreticalAzimuth(blip);
            double angle = theoreticalAzimuth;
            double xPos, yPos;


            calculatedAzimuth = (realAzimuth + theoreticalAzimuth)% 360;

            deltaAzimuth = calculatedAzimuth - realAzimuth;

            angle = realAzimuth + deltaAzimuth;





            /*
             * Sanity check
             */
//            if(angle < 0)
//                angle = (angle+360)%360;

            /*
             * Orient the blip according to origin/phone azimuth
             */
//            angle += mAzimuth;

            // Math is fun
//            xPos = Math.sin(Math.toRadians(angle)) * meters;
//            yPos = Math.sqrt(Math.pow(meters, 2) - Math.pow(xPos, 2));

            xPos = Math.sin(angle) * meters;
            yPos = Math.sqrt(Math.pow(meters, 2) - Math.pow(xPos, 2));


            /*
             *         360
             *          *
             *          *
             * 270 **** 0 **** 090
             *          *
             *          *
             *         180
             */
            if (angle > 90 && angle < 270)
                yPos *= -1;


            DisplayMetrics m = ARDpiUtil.getDisplayMetrics(MyApplication.getInstance().getApplicationContext());

            double posInPx = angle * (m.widthPixels / 90d);

            float paintX = mOriginX + (float)xPos;
            float paintY = mOriginY + (float)yPos;

            blip.setOrigin(paintX, paintY);
            blip.paint(mCanvas, mPaint);


            mPaint.setColor(Color.GREEN);
            mPaint.setTextSize(50);
            mPaint.setAntiAlias(true);
            mCanvas.drawText("Azimuth:" + mAzimuth , 0.0f, (float)(m.widthPixels - 150), mPaint);
            mCanvas.drawText("Theoretical Azimuth:" + theoreticalAzimuth , 0.0f, (float)(m.widthPixels - 100.0f), mPaint);
            mCanvas.drawText("Calculated Azimuth:" + calculatedAzimuth , 0.0f, (float)(m.widthPixels - 50.0f), mPaint);

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

    /*
     * Tangent: Opposite/Adgacent
     * Source: https://www.mathsisfun.com/definitions/tangent-function-.html
     * Source: https://www.netguru.co/blog/augmented-reality-mobile-android
     *
     *  Quadrant        Sign of Growth    Relation between azimuth
     *   coords     Δ cos(A)  Δ sin(A)  azimuth and angle ᵠ in grads
     *  --------    --------  --------  ----------------------------
     *     I           +          +                 A=ᵠ
     *    II           -          +               A=200g-ᵠ
     *   III           -          -               A=200g+ᵠ
     *    IV           +          -               A=400g-ᵠ
     *
     */
    protected double calculateTheoreticalAzimuth(ARPoint poi) {

        double dX = poi.latitude - mARRadarReference.latitude;
        double dY = poi.longitude - mARRadarReference.longitude;

        double phiAngle;
        double phiAngleRads;
        double tanPhi;
        double azimuth;

        tanPhi = Math.abs(dY / dX);
        phiAngle = Math.atan(tanPhi);
        phiAngleRads = Math.toDegrees(phiAngle);

        if (dX > 0 && dY > 0) {
            return azimuth = phiAngleRads;
        } else if (dX < 0 && dY > 0) {
            return azimuth = 180 - phiAngleRads;
        } else if (dX < 0 && dY < 0){
            return azimuth = 180 + phiAngleRads;
        } else if (dX > 0 && dY < 0) {
            return azimuth = 360 - phiAngleRads;
        }

        return phiAngleRads;

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


        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = RADIUS_EARTH * c;

        return dist * 1000;
    }



    public void paint(Canvas canvas, Paint paint) {
//        mCanvas = canvas;
//        mPaint = paint;
        setCanvas(canvas);
        setPaint(paint);
        paint();
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


        // Call Radar Paint Targets
        paintTargets();

        setColor(mRadarTextColor);
        setFontSize(30.0f);

        String direction = String.format("%.1f", mAzimuth);

        float tw = getTextWidth(direction);
        mCanvas.drawText(direction, mOriginX - (tw/2), mOriginY-RADIUS-10, mPaint);

    }


    public void setCanvas(Canvas canvas) {
        mCanvas = canvas;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
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
