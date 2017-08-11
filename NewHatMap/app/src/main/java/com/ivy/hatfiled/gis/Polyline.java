/**
 * Created by hatfiled on 2015/5/9 0009.
 */
package com.ivy.hatfiled.gis;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.ArrayList;
import java.util.List;

public class Polyline {
    //member
    private int FID;
    private int LinePointNum = 0;
    private List Points;
    private float[] MBR={0,0,0,0};

    //function
    public Polyline(){};
    public Polyline(int fid,List list){
        FID = fid;
        Points = new ArrayList();
        Points.addAll(list);
        LinePointNum = Points.size();
        MBR = CalLineMBR();
    }
    public float[] CalLineMBR(){//求外包矩形框MBR
        float point[] = (float[]) Points.get(0);
        float mbr[] = {point[0],point[1],point[0],point[1]};//xmin,ymin,xmax,ymax
        for(int i=1;i<LinePointNum;i++){
            point = (float[]) Points.get(i);
            if(mbr[0] > point[0] ) mbr[0] = point[0];
            if(mbr[1] > point[1] ) mbr[1] = point[1];
            if(mbr[2] < point[0] ) mbr[2] = point[0];
            if(mbr[3] < point[1] ) mbr[3] = point[1];
        }
        return mbr;
    }
    public void DrawLine(Canvas canvas,double[] screensize,double[] center,double scale,Paint lPaint){
        Path newPath = new Path();
        float point[]= (float[]) Points.get(0);
        float latterx,lattery,prex,prey;
        prex= (float)(screensize[0]/2+(point[0]-center[0])/scale);
        prey= (float)(screensize[1]/2+(point[1]-center[1])/scale);
        newPath.moveTo(prex,prey);
        for(int i=1;i<Points.size();i++){
            point = (float[]) Points.get(i);
            latterx= (float)(screensize[0]/2+(point[0]-center[0])/scale);
            lattery= (float)(screensize[1]/2+(point[1]-center[1])/scale);
            newPath.quadTo(prex, prey, latterx, lattery);
            prex = latterx;
            prey = lattery;
        }
        canvas.drawPath(newPath,lPaint);
        newPath.reset();
    }
    public void DrawLineMBR(Canvas canvas,double[] screensize,double[] center,double scale,Paint lPaint){
        float latterx,lattery,prex,prey;
        prex= (float)(screensize[0]/2+(MBR[0]-center[0])/scale);
        prey= (float)(screensize[1]/2+(MBR[1]-center[1])/scale);
        latterx= (float)(screensize[0]/2+(MBR[2]-center[0])/scale);
        lattery= (float)(screensize[1]/2+(MBR[3]-center[1])/scale);
        canvas.drawRect(prex,prey,latterx,lattery,lPaint);
    }
    public List GetPoints(){
        return Points;
    }
    public int GetFID(){
        return FID;
    }
}
