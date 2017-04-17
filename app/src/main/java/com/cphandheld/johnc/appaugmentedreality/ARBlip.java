package com.cphandheld.johnc.appaugmentedreality;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by jcobb on 4/13/17.
 */

public class ARBlip extends ARPoint{


    private Canvas mCanvas;
    private Paint mPaint;
    private double mAngle;
    static int mColor = Color.argb(225, 0, 180, 0);

    /*
     *  Blip: is lat lon detected from objects identified on the radar sweep (Cars)
     *  Bogey: is lat lon superimposed on the radar on the radar sweep (Target Car We Want)
     *  Source: is lat lon of the radar observer: (Phone)
     */

    private double mBlipLatitude;
    private double mBlipLongitude;
    private double mSourceLatitude;
    private double mSourceLongitude;

    private float mOriginX = 0.0f;
    private float mOriginY = 0.0f;

    public ARBlip(double lat, double lon, String desc) {
        super(lat, lon, desc);
        mBlipLatitude = lat;
        mBlipLongitude = lon;

        mPaint = new Paint();
        setFontSize(FONT_SIZE);
        setAntiAlias(ANTIALIAS);
        setColor(mColor);
        setFill(true);

    }


    public ARBlip(double lat, double lon, String desc, Canvas canvas) {
        super(lat, lon, desc);
        setCanvas(canvas);
        mBlipLatitude = lat;
        mBlipLongitude = lon;

        mPaint = new Paint();
        setFontSize(FONT_SIZE);
        setAntiAlias(ANTIALIAS);
        setColor(mColor);
        setFill(true);

    }
    public void scan(double lat, double lon) {
        mSourceLatitude = lat;
        mSourceLongitude = lon;
    }

    public void setCanvas(Canvas canvas) {
        mCanvas = canvas;
    }

    public void setOrigin(float x, float y) {
        mOriginX = x;
        mOriginY = y;
    }

    public void paint() {
//        mAngle = bearing(mBlipLatitude, mBlipLongitude, mSourceLatitude, mSourceLongitude) - OFFSET;


        mCanvas.drawCircle(mOriginX+40, mOriginY+25, 5, mPaint);
    }



}
