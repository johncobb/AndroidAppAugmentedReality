package com.cphandheld.johnc.appaugmentedreality;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

/**
 * Created by jcobb on 4/12/17.
 */

public class ARDrawable {

    private double OFFSET = 0d;
    private static int PADDING_X = 0;
    private static int PADDING_Y = 50;
    public static int FONT_SIZE = 16;
    public static boolean ANTIALIAS = true;
    public static float RADIUS = 125;

    static float mOriginX = 0.0f;
    static float mOriginY = 0.0f;

    public Canvas mCanvas;
    private int mWidth;
    private int mHeight;
    private Typeface mTypeface;
    public Paint mPaint;
    private Paint mPaintBuffer;

    public ARDrawable() {}

    public ARDrawable(Canvas canvas, Paint paint) {
        mCanvas = canvas;
        mPaint = paint;
    }


    public Canvas getCanvas() {
        return mCanvas;
    }

    public void setCanvas(Canvas canvas) {
        mCanvas = canvas;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }



    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void setStrokeWidth(float width) {
        mPaint.setStrokeWidth(width);
    }

    public void paintLine(float x1, float y1, float x2, float y2) {
        mCanvas.drawLine(x1+PADDING_X, y1+PADDING_Y, x2+PADDING_X, y2+PADDING_Y, mPaint);
    }

    public void paintRect(float x, float y, float width, float height) {
        mCanvas.drawRect(x, y, x + width, y + height, mPaint);
    }

    public void paintCircle(float x, float y, float radius) {
        mCanvas.drawCircle(x, y, radius, mPaint);
    }

    public void paint() {

    }

    public void paintText(float x, float y, String text) {
        mCanvas.drawText(text, x, y, mPaint);
    }

    public void setFontSize(float size) {
        mPaint.setTextSize(size);
    }


    public float getTextWidth(String txt) {
        return mPaint.measureText(txt);
    }

    public float getTextAsc() {
        return -mPaint.ascent();
    }

    public float getTextDesc() {
        return mPaint.descent();
    }

    public void setAntiAlias(boolean antialias){
        mPaint.setAntiAlias(antialias);
    }

    public void setFill(boolean fill) {
        if (fill)
            mPaint.setStyle(Paint.Style.FILL);
        else
            mPaint.setStyle(Paint.Style.STROKE);
    }

}
