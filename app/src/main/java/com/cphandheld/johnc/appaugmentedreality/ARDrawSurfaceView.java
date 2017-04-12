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
    ARPoint _ARPointMe = new ARPoint(37.97280602299139d, -87.40445584058762d, "Me");

    float _Azimuth = 0.0f;
    float _Pitch = 0.0f;
    float _Roll = 0.0f;

    Paint mPaint = new Paint();
    private double OFFSET = 0d;
    private double mScreenWidth, mScreenHeight = 0d;
    private Bitmap[] mSpots, mBlips;
    private Bitmap mRadar;

    public static ArrayList<ARPoint> props = new ArrayList<ARPoint>();
    static {
//        props.add(new ARPoint(90d, 110.8000, "North Pole"));
//        props.add(new ARPoint(-90d, -110.8000, "South Pole"));
//        props.add(new ARPoint(-33.870932d, 151.8000, "East"));
//        props.add(new ARPoint(-33.870932d, 150.8000, "West"));
        props.add(new ARPoint(37.97280602299139d, -87.40445584058762d, "CP Handheld"));
    }

    public ARDrawSurfaceView(Context c, Paint paint) {
        super(c);
    }

    public ARDrawSurfaceView(Context context, AttributeSet set) {
        super(context, set);
        mPaint.setColor(Color.GREEN);
        mPaint.setTextSize(50);
        mPaint.setStrokeWidth(ARDpiUtil.getPxFromDpi(getContext(), 2));
        mPaint.setAntiAlias(true);

//        mRadar = BitmapFactory.decodeResource(context.getResources(), R.drawable.radar);
        mRadar = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_radar);

        mSpots = new Bitmap[props.size()];
        for (int i = 0; i < mSpots.length; i++)
            mSpots[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dot);

        mBlips = new Bitmap[props.size()];
        for (int i = 0; i < mBlips.length; i++)
            mBlips[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_blip);
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

        canvas.drawBitmap(mRadar, 0, 0, mPaint);

        int radarCenterX = mRadar.getWidth() / 2;
        int radarCenterY = mRadar.getHeight() / 2;

        for (int i = 0; i < mBlips.length; i++) {
            Bitmap blip = mBlips[i];
            Bitmap spot = mSpots[i];
            ARPoint u = props.get(i);
            double dist = distInMetres(_ARPointMe, u);

            if (blip == null || spot == null)
                continue;

            if(dist > 70)
                dist = 70; //we have set points very far away for demonstration

            double angle = bearing(_ARPointMe.latitude, _ARPointMe.longitude, u.latitude, u.longitude) - OFFSET;
            double xPos, yPos;

            if(angle < 0)
                angle = (angle+360)%360;

            xPos = Math.sin(Math.toRadians(angle)) * dist;
            yPos = Math.sqrt(Math.pow(dist, 2) - Math.pow(xPos, 2));

            if (angle > 90 && angle < 270)
                yPos *= -1;

            double posInPx = angle * (mScreenWidth / 90d);

            int blipCentreX = blip.getWidth() / 2;
            int blipCentreY = blip.getHeight() / 2;

            xPos = xPos - blipCentreX;
            yPos = yPos + blipCentreY;
            canvas.drawBitmap(blip, (radarCenterX + (int) xPos), (radarCenterY - (int) yPos), mPaint); //radar blip

            //reuse xPos
            int spotCentreX = spot.getWidth() / 2;
            int spotCentreY = spot.getHeight() / 2;
            xPos = posInPx - spotCentreX;

            if (angle <= 45)
                u.x = (float) ((mScreenWidth / 2) + xPos);

            else if (angle >= 315)
                u.x = (float) ((mScreenWidth / 2) - ((mScreenWidth*4) - xPos));

            else
                u.x = (float) (float)(mScreenWidth*9); //somewhere off the screen

            u.y = (float)mScreenHeight/2 + spotCentreY;
            canvas.drawBitmap(spot, u.x, u.y, mPaint); //camera spot
            canvas.drawText(u.description, u.x, u.y, mPaint); //text

            String lat = String.format("%.6f", _ARPointMe.latitude);
            String lon = String.format("%.6f", _ARPointMe.longitude);
            String pitch = String.format("%.2f", _Pitch);
            String roll = String.format("%.2f", _Roll);
            String azimuth = String.format("%.2f", _Azimuth);

            canvas.drawText("Lat:" + lat + " Lon:" + lon, 0.0f, (float)(mScreenHeight - 100.0f), mPaint);
            canvas.drawText("Pitch: " + pitch + " Roll: " + roll + " Azimuth: " + azimuth, 0.0f, (float)(mScreenHeight - 50.0f), mPaint);
//            canvas.drawText("Azimuth:" + _Azimuth , 0.0f, (float)(mScreenWidth - 50.0f), mPaint);
        }
    }

    public void setOffset(float offset) {
        this.OFFSET = offset;
    }

    public void setMyLocation(double latitude, double longitude) {
        _ARPointMe.latitude = latitude;
        _ARPointMe.longitude = longitude;
    }

    public void setMyOrientation(float azimuth, float pitch, float roll) {
        _Azimuth = azimuth;
        _Pitch = pitch;
        _Roll = roll;
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

        double dX = poi.latitude - _ARPointMe.latitude;
        double dY = poi.longitude - _ARPointMe.longitude;

        double phiAngle;
        double tanOfPhiAngle;
        double azimuth;

        tanOfPhiAngle = Math.abs(dY / dX);
        phiAngle = Math.atan(tanOfPhiAngle);
        tanOfPhiAngle = Math.toDegrees(tanOfPhiAngle);

        if (dX > 0 && dY > 0) {
            return azimuth = phiAngle;
        } else if (dX < 0 && dY > 0) {
            return azimuth = 180 - phiAngle;
        } else if (dX < 0 && dY < 0){
            return azimuth = 180 + phiAngle;
        } else if (dX > 0 && dY < 0) {
            return 360 - phiAngle;
        }

        return phiAngle;

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
