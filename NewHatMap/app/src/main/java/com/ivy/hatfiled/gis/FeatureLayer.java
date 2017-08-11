package com.ivy.hatfiled.gis;

import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.List;

public class FeatureLayer {
    //member
    private String LayerName;
    private String IsShow = "true";
    private boolean EnableEdit = false;
    private int FeatureType;//1点，2线，3面
    private int totalFeatures;
    private List FeatureClass;//存储矢量要素类
    private Point CachePoint;
    private Polyline CacheLine;
    private Surface CacheSurface;

    //function
    public FeatureLayer(){
    }
    public FeatureLayer(String name, int type){
        LayerName = name;
        FeatureType = type;
        FeatureClass = new ArrayList();
        totalFeatures = 0;
    }
    public void drawPointFeatures(Canvas canvas,double[] screen_size,double[] center,double scale,Paint pPaint){
        for(int i=0;i<FeatureClass.size();i++){
            CachePoint = (Point)FeatureClass.get(i);
            CachePoint.DrawPoint(canvas,screen_size,center,scale,pPaint);
        }
    }
    public void drawLineFeatures(Canvas canvas,double[] screen_size,double[] center,double scale,Paint lPaint){
        for(int i=0;i<FeatureClass.size();i++){
            CacheLine = (Polyline)FeatureClass.get(i);
            CacheLine.DrawLine(canvas,screen_size,center,scale,lPaint);
            //CacheLine.DrawLineMBR(canvas,screen_size,center,scale,lPaint);
        }
    }
    public void drawSurfaceFeatures(Canvas canvas,double[] screen_size,double[] center,double scale,Paint sPaint,Paint slPaint){
        for(int i=0;i<FeatureClass.size();i++){
            CacheSurface = (Surface)FeatureClass.get(i);
            CacheSurface.DrawSurface(canvas,screen_size,center,scale,sPaint,slPaint);
        }
    }
    public void addPointFeature(int fid,float[] point){
        CachePoint = new Point(fid,point);
        FeatureClass.add(CachePoint);
        totalFeatures +=1;
    }
    public void addLineFeature(int fid,Object points){
        CacheLine = new Polyline(fid,(List)points);
        FeatureClass.add(CacheLine);
        totalFeatures +=1;
    }
    public void addSurfaceFeature(int fid,Object points){
        CacheSurface = new Surface(fid,(List)points);
        FeatureClass.add(CacheSurface);
        totalFeatures +=1;
    }
    public void setLayerIsShow(String show){
        IsShow = show;
    }
    public void setLayerEdit(boolean edit){
        EnableEdit = edit;
    }
    public int GetLayerType(){
        return FeatureType;
    }
    public String GetLayerIsShow(){
        return IsShow;
    }
    public String GetLayerName(){
        return LayerName;
    }
    public int GetTotalFeatures(){
        return  totalFeatures;
    }
    public List GetFeatureClass(){
        return FeatureClass;
    }
}
