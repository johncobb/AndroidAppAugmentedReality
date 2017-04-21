package com.cphandheld.johnc.appaugmentedreality;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by jcobb on 4/13/17.
 */

public class ARBlip extends ARPoint{

    private static int BLIP_RADIUS = 5;
    private static int mColor = Color.argb(225, 0, 180, 0);
    private float mOriginX = 0.0f;
    private float mOriginY = 0.0f;


    public ARBlip(double lat, double lon, String desc, Canvas canvas, Paint paint) {
        super(lat, lon, desc);
        this.latitude = lat;
        this.longitude = lon;
        setCanvas(canvas);
        setPaint(paint);
    }

    public ARBlip(double lat, double lon, String desc) {
        super(lat, lon, desc);
        this.latitude = lat;
        this.longitude = lon;
    }

    public void scan(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public void setOrigin(float x, float y) {
        mOriginX = x;
        mOriginY = y;
    }

    public void setCanvas(Canvas canvas) {
        mCanvas = canvas;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    public void paint(Canvas canvas, Paint paint) {
        setCanvas(canvas);
        setPaint(paint);

        paint();
    }

    public void paint() {
        setFontSize(FONT_SIZE);
        setAntiAlias(ANTIALIAS);
        setColor(mColor);
        setFill(true);

        mCanvas.drawCircle(mOriginX, mOriginY, BLIP_RADIUS, mPaint);
    }

}
