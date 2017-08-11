package com.ivy.hatfiled.gis;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

public class Point {
    //member
    private int FID;
    private float[] Point;

    //function
    public Point(){};
    public Point(int fid,float[] point){
        FID = fid;
        Point = new float[3];
        Point = point;
    }
    public void DrawPoint(Canvas canvas,double[] screensize,double[] center,double scale,Paint pPaint){
        float x,y;
        x= (float)(screensize[0]/2+(Point[0]-center[0])/scale);
        y= (float)(screensize[1]/2+(Point[1]-center[1])/scale);
        canvas.drawCircle(x,y,8,pPaint);
    }
}
