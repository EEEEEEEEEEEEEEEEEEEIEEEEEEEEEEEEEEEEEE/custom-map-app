package com.ivy.hatfiled.gis;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.Map;

public class TilesLayer {
    //member
    private int LayerType;//0栅格，1点，2线，3面
    private String LayerName;
    private String IsShow = "true";
    private boolean EnableEdit = false;
    private Map<String, Object> TileList;
    private FeatureLayer Features;
    private Point CachePoint;
    private Polyline CacheLine;
    private Surface CacheSurface;

    //function
    public TilesLayer(){
    }
    public TilesLayer(String name, int type){
        LayerName = name;
        LayerType = type;
        TileList = new HashMap<String, Object>();
    }

    public void drawRasterTiles(Canvas canvas,int[] TileLocation,int TilePixelSize,float[] ScreenPos,String PathToLevel){
        Bitmap bitmapShow = null;
        if(TileList.size()>100) TileList.clear();
        for (int i = TileLocation[0]; i <= TileLocation[2]; i++)
            for (int j = TileLocation[1]; j <= TileLocation[3]; j++) {
                if (TileList.containsKey(i + "," + j)) {
                    bitmapShow = (Bitmap) TileList.get(i + "," + j);
                    canvas.drawBitmap(bitmapShow, ScreenPos[0] + TilePixelSize * (i - TileLocation[0]),ScreenPos[1] + TilePixelSize * (j - TileLocation[1]), null);
                }else{
                    bitmapShow = BitmapFactory.decodeFile(PathToLevel + i + "/" + j + ".png", null);
                    if(null!=bitmapShow) {
                        TileList.put(i + "," + j, bitmapShow);
                        canvas.drawBitmap(bitmapShow, ScreenPos[0] + TilePixelSize * (i - TileLocation[0]),ScreenPos[1] + TilePixelSize * (j - TileLocation[1]), null);
                    }
                }
            }
    }
    public void drawFeatureTiles(Canvas canvas,int[] TileLocation,double[] screen_size,double[] center,double scale,Paint lPaint){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        int r,g,b;
        for (int i = TileLocation[0]; i <= TileLocation[2]; i++)
            for (int j = TileLocation[1]; j <= TileLocation[3]; j++)
                if (TileList.containsKey(i + "," + j)) {
                    Features = (FeatureLayer)TileList.get(i + "," + j);
                    r = (int) (Math.random() * 255);
                    g = (int) (Math.random() * 255);
                    b = (int) (Math.random() * 255);
                    paint.setColor(Color.argb(150, r, g, b));
                    Features.drawLineFeatures(canvas, screen_size, center, scale, paint);
                }
    }

    public String GetLayerIsShow(){
        return IsShow;
    }
    public String GetLayerName(){
        return LayerName;
    }
    public int GetLayerType(){
        return LayerType;
    }
    public void setLayerIsShow(String show){
        IsShow = show;
    }
    public void setLayerEdit(boolean edit){
        EnableEdit = edit;
    }
    public void addTiles(Map<String, Object> tiles){
        TileList = tiles;
    }
    public void ClearTiles(){
        TileList.clear();
    }
}
