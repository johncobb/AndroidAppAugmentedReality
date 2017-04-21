package com.cphandheld.johnc.appaugmentedreality;

/**
 * Created by jcobb on 4/6/17.
 */

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/*
 * Portions (c) 2009 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Coby Plain coby.plain@gmail.com, Ali Muzaffar ali@muzaffar.me
 */

/*
 * Credit Source: https://www.netguru.co/blog/augmented-reality-mobile-android
 */

public class ARDrawSurfaceView extends View {
    ARPoint mARPoint = new ARPoint(37.97280602299139d, -87.40445584058762d, "Me");

    Paint mPaint = new Paint();
    private double OFFSET = 0d;
    private double mScreenWidth, mScreenHeight = 0d;

    private ARRadar mARRadar;


    public ARDrawSurfaceView(Context c, Paint paint) {
        super(c);
    }

    public ARDrawSurfaceView(Context context, AttributeSet set) {
        super(context, set);
        mPaint.setColor(Color.GREEN);
        mPaint.setTextSize(50);
        mPaint.setStrokeWidth(ARDpiUtil.getPxFromDpi(getContext(), 2));
        mPaint.setAntiAlias(true);

        mARRadar = new ARRadar();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("onSizeChanged", "in here w=" + w + " h=" + h);
        mScreenWidth = (double) w;
        mScreenHeight = (double) h;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mARRadar.paint(canvas, mPaint);
    }

    public void setOffset(float offset) {
        this.OFFSET = offset;
    }

    public void setMyLocation(double latitude, double longitude) {
        mARPoint.latitude = latitude;
        mARPoint.longitude = longitude;
        mARRadar.setRadarReference(mARPoint);
    }

    public void setMyOrientation(float azimuth, float pitch, float roll) {
        mARRadar.setRadarOrientation(roll, pitch, azimuth);
    }

}
