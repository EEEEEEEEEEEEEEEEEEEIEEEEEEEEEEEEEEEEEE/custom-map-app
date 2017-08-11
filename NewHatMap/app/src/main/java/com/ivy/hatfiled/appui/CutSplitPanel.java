package com.ivy.hatfiled.appui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class CutSplitPanel{
    public static void show( Context context,final MapControl mapcontrol){
        final PopupWindow EditTools;
        RelativeLayout edittoollayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.cutsplitlayout,null);
        EditTools  = new PopupWindow(edittoollayout,230,230);
        edittoollayout.findViewById(R.id.GridLine).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                mapcontrol.CutToTiles();//ÏßÇÐÆ¬
            }
        });
        edittoollayout.findViewById(R.id.CloseTools).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditTools.dismiss();
                mapcontrol.setGesture(0);
            }
        });
        EditTools.showAtLocation(mapcontrol, Gravity.NO_GRAVITY, 10, 60);
    }
}
