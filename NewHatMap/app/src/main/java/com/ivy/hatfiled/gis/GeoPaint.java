package com.ivy.hatfiled.gis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;

import com.ivy.hatfiled.appui.R;

public class GeoPaint {
    private Paint mGeoPaint;
    private Context context;
    public GeoPaint(Context context){
        this.context=context;
    }
    public Paint CreatePen(String MyPen){
        Paint pPaint = new Paint();
        pPaint.setFlags(Paint.ANTI_ALIAS_FLAG);//Ïû³ý¾â³Ý
        if(MyPen=="dark_pen"){
            pPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            pPaint.setColor(Color.argb(255, 0, 0, 0));
        }
        if(MyPen=="red_line"){
            pPaint.setStyle(Paint.Style.STROKE);
            pPaint.setStrokeWidth(2);
            pPaint.setColor(Color.argb(255,255,0,0));
        }
        if(MyPen=="red_surface"){
            pPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            pPaint.setStrokeWidth(2);
            pPaint.setColor(Color.argb(150,255,0,0));
        }
        if(MyPen=="default_point"){
            pPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            pPaint.setColor(Color.argb(255,221,46,221));
        }
        if(MyPen=="default_line"){
            pPaint.setStyle(Paint.Style.STROKE);
            pPaint.setStrokeWidth(3);
            pPaint.setColor(Color.argb(200,255,10,0));
        }
        if(MyPen=="default_surface"){
            pPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            pPaint.setStrokeWidth(2);
            pPaint.setColor(Color.argb(220,123,56,202));
        }
        if (MyPen=="default_outline"){
            pPaint.setStyle(Paint.Style.STROKE);
            pPaint.setColor(Color.argb(150,255,10,0));
            pPaint.setStrokeWidth(3);
        }
        if(MyPen=="picture3_surface"){
            Bitmap pBackBit = BitmapFactory.decodeResource(context.getResources(), R.drawable.fill3);
            Shader mShader = new BitmapShader(pBackBit,Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
            pPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            pPaint.setShader(mShader);
            pPaint.setAlpha(145);
        }
        return pPaint;
    }
}

