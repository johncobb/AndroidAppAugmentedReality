package com.cphandheld.johnc.appaugmentedreality;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;

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

    ARBlip mARBlips[];



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

        mARBlips[0] = new ARBlip(0.0f, 0.0f, "", mCanvas);
    }

    public void setCanvas(Canvas canvas) {
        mCanvas = canvas;
    }

    void sweep() {
        for (int i=0; i<mARBlips.length-1; i++) {
            mARBlips[i].paint();
        }
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
        setFill(true);
        setColor(mBogeyColor);
        mCanvas.drawCircle(mOriginX+40, mOriginY+25, 5, mPaint);

        mARBlips[0].paint();

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
