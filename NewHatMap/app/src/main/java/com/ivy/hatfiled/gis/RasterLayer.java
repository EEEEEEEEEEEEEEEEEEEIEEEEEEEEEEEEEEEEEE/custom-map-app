/**
 * Created by hatfiled on 2015/5/9 0009.
 */
package com.ivy.hatfiled.gis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RasterLayer {
    //member
    private int LayerType;//0栅格，1点，2线，3面
    private String LayerName;
    private String IsShow = "true";
    private boolean EnableEdit = false;
    private Map<String, Object> TileList;

    //function
    public RasterLayer(){
    }
    public RasterLayer(String name, int type){
        LayerName = name;
        LayerType = type;
        TileList = new HashMap<String, Object>();
    }

    public void drawRasterLayer(Canvas canvas,int[] TileLocation,int TilePixelSize,float[] ScreenPos,String PathToLevel){
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
    public void ClearTiles(){
        TileList.clear();
    }

}
